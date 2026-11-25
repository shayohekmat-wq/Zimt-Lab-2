"""Regression tests for private signing inputs. All keys used here are temporary."""
import argparse
import hashlib
import os
from pathlib import Path
import re
import tempfile
import unittest
from unittest.mock import patch

import eimer_signer as signer


class LibrarySigningTest(unittest.TestCase):
    def setUp(self):
        temp = tempfile.TemporaryDirectory()
        self.addCleanup(temp.cleanup)
        self.root = Path(temp.name)
        self.library = self.root / "library"
        self.library.mkdir(mode=0o700)
        self.key = self.library / signer.KEYSTORE
        self.password = self.library / signer.LIBRARY_PASSWORD

    def make_files(self):
        self.key.touch(mode=0o600)
        self.password.touch(mode=0o600)
        self.password.write_text("fixture-only\n")

    def test_source_selection_never_falls_back_from_explicit_paths(self):
        self.make_files()
        registered = self.root / "registered"
        with patch.dict(os.environ, {"EIMER_SIGNING_DIR": str(registered), "EIMER_LIBRARY_DIR": ""}):
            self.assertEqual(signer.signing_files(self.library).keystore, self.key)
            with self.assertRaises(FileNotFoundError):
                signer.signing_files(None)
            with patch.dict(os.environ, {"EIMER_LIBRARY_DIR": str(self.library)}):
                self.assertEqual(signer.signing_files(None).key_password, self.password)
        with patch.dict(os.environ, {"EIMER_SIGNING_DIR": "", "EIMER_LIBRARY_DIR": "",
                                     "XDG_DATA_HOME": str(self.root)}), patch.object(signer, "DEFAULT_LIBRARY", self.library):
            self.assertEqual(signer.signing_files(None).keystore, self.key)

    def test_incomplete_ambiguous_or_unprotected_mounts_stop(self):
        with self.assertRaisesRegex(ValueError, signer.KEYSTORE):
            signer.library_files(self.library)
        self.make_files()
        nested = self.library / "attachment"
        nested.mkdir(mode=0o700)
        duplicate = nested / signer.KEYSTORE
        self.key.rename(duplicate)
        self.assertEqual(signer.library_files(self.library).keystore, duplicate)
        self.key.touch(mode=0o600)
        with self.assertRaisesRegex(ValueError, "mehrfach"):
            signer.library_files(self.library)
        duplicate.unlink()
        self.key.chmod(0o644)
        with self.assertRaisesRegex(ValueError, "private"):
            signer.library_files(self.library)
        self.key.chmod(0o600)
        self.password.write_text("heading\nfixture-only\n")
        with self.assertRaisesRegex(ValueError, "Passwortzeile"):
            signer.library_files(self.library)
        self.password.unlink()
        with self.assertRaisesRegex(ValueError, signer.LIBRARY_PASSWORD):
            signer.library_files(self.library)

    @unittest.skipUnless(os.environ.get("EIMER_TEST_APK"), "Set EIMER_TEST_APK to a debug/test APK")
    def test_private_key_proof_enrollment_and_apk_signing(self):
        apk = Path(os.environ["EIMER_TEST_APK"])
        manifest = signer.run("aapt", "dump", "badging", apk).decode()
        match = re.search(r"^package: name='([^']+)' versionCode='(\d+)'", manifest, re.M)
        self.assertTrue(match and match[1].endswith((".debug", ".test")))
        version = int(match[2])
        self.assertGreater(version, 1)
        self.password.touch(mode=0o600)
        self.password.write_text(os.urandom(24).hex() + "\n")
        base = ("keytool", "-keystore", self.key, "-storetype", "PKCS12", "-storepass:file", self.password)
        signer.run(*base, "-genkeypair", "-alias", "fixture", "-dname", "CN=Test fixture",
                   "-keyalg", "RSA", "-keysize", "2048", "-validity", "1")
        self.key.chmod(0o600)
        files = signer.library_files(self.library)
        original = [signer.fingerprint(p) for p in (self.key, self.password)]
        pin = hashlib.sha256(signer.run(*base, "-exportcert", "-alias", "fixture")).hexdigest()
        with self.assertRaisesRegex(ValueError, "Originalschlüssel"):
            signer.key_alias(files)
        # Only the in-process test identity and test package change. The product CLI pins both.
        with patch.object(signer, "CERT_SHA256", pin), patch.object(signer, "PACKAGE", match[1]):
            self.assertEqual(signer.key_alias(files), "fixture")
            args = argparse.Namespace(apk=apk, output=self.root / "test-signed.apk",
                                      sha256="0"*64, previous_version_code=version-1)
            with self.assertRaisesRegex(ValueError, "APK-Hash"):
                signer.sign(args, files)
            args.sha256 = signer.fingerprint(apk)
            args.previous_version_code = version
            with self.assertRaisesRegex(ValueError, "Versionsfolge"):
                signer.sign(args, files)
            self.assertFalse(args.output.exists())
            args.previous_version_code = version-1
            signer.sign(args, files)
            self.assertTrue(args.output.is_file())
            register = argparse.Namespace(keystore=self.key, store_password_file=self.password, key_password_file=None)
            target = self.root / "registered"
            signer.enroll(register, target)
            self.assertEqual(signer.key_alias(signer.registered_files(target)), "fixture")
        self.assertEqual(original, [signer.fingerprint(p) for p in (self.key, self.password)])


if __name__ == "__main__":
    unittest.main()
