#!/usr/bin/env python3
"""Register the original Eimer-Liste identity on a private signer and sign CI candidates."""
import argparse
from dataclasses import dataclass
import hashlib
import os
from pathlib import Path
import re
import shutil
import stat
import subprocess
import sys
import tempfile

CERT_SHA256 = "40e6b73383a29f9ca792595b369329e2ab8f9165f49eec69b4eb57a8ffe26fe1"
PACKAGE = "de.zimt.eimerliste"
KEYSTORE = "Eimer-Liste-Update-Schluessel.p12"
LIBRARY_PASSWORD = "Eimer-Liste-Update-Passwort.txt"
DEFAULT_LIBRARY = Path("/workspace/library-files")
STORE_PASS = "store.password"
KEY_PASS = "key.password"


def run(*args):
    result = subprocess.run(args, stdin=subprocess.DEVNULL, capture_output=True)
    if result.returncode:
        # Tool diagnostics can contain paths, aliases or input-dependent data.
        raise ValueError(f"{Path(args[0]).name} ist fehlgeschlagen (Exit {result.returncode}).")
    return result.stdout


def fingerprint(path):
    with path.open("rb") as handle:
        return hashlib.file_digest(handle, "sha256").hexdigest()


def location():
    default = Path(os.environ.get("XDG_DATA_HOME") or Path.home() / ".local/share") / "zimt/eimerliste"
    path = Path(os.environ.get("EIMER_SIGNING_DIR") or default)
    if not path.is_absolute():
        raise ValueError("Der Signierpfad muss absolut sein.")
    return path


def check_private(path, directory=False):
    info = path.lstat()
    kind = stat.S_ISDIR if directory else stat.S_ISREG
    if not kind(info.st_mode) or info.st_uid not in {0, os.geteuid()} or info.st_mode & 0o077:
        raise ValueError("Signierablage erfordert private, eigene Dateien und Verzeichnisse (0600/0700).")


@dataclass(frozen=True)
class IdentityFiles:
    keystore: Path
    store_password: Path
    key_password: Path


def registered_files(directory):
    check_private(directory, directory=True)
    for name in (KEYSTORE, STORE_PASS, KEY_PASS):
        check_private(directory / name)
    return IdentityFiles(directory / KEYSTORE, directory / STORE_PASS, directory / KEY_PASS)


def library_files(directory):
    if not directory.is_absolute():
        raise ValueError("Der Bibliothekspfad muss absolut sein.")
    if not directory.is_dir():
        raise ValueError(f"Die private Dateibibliothek ist unter {directory} nicht eingebunden.")
    paths = []
    for name in (KEYSTORE, LIBRARY_PASSWORD):
        matches = list(directory.rglob(name))
        if not matches:
            raise ValueError(f"{name} fehlt unter {directory}. Beide Originaldateien privat und lesbar einbinden.")
        if len(matches) != 1:
            raise ValueError(f"{name} ist mehrfach eingebunden. Mit --library-dir eine eindeutige Auswahl angeben.")
        path = matches[0]
        for parent in path.parents:
            info = parent.lstat()
            if not stat.S_ISDIR(info.st_mode) or info.st_uid not in {0, os.geteuid()} or info.st_mode & 0o022:
                raise ValueError("Der Bibliothekspfad muss gegen Änderungen durch andere Benutzer geschützt sein.")
            if parent == directory:
                break
        check_private(path)
        paths.append(path)
    keystore, password = paths
    lines = password.read_bytes().splitlines()
    if len(lines) != 1 or not lines[0]:
        raise ValueError("Die eingebundene Passwortdatei muss genau eine nichtleere Passwortzeile enthalten.")
    return IdentityFiles(keystore, password, password)


def signing_files(library):
    if library is not None:
        return library_files(library)
    if os.environ.get("EIMER_LIBRARY_DIR"):
        return library_files(Path(os.environ["EIMER_LIBRARY_DIR"]))
    registered = location()
    if os.environ.get("EIMER_SIGNING_DIR") or registered.exists():
        return registered_files(registered)
    return library_files(DEFAULT_LIBRARY)


def key_alias(files):
    base = ("keytool", "-J-Duser.language=en", "-J-Duser.country=US", "-keystore",
            files.keystore, "-storetype", "PKCS12", "-storepass:file", files.store_password)
    listing = run(*base, "-list", "-v").decode()
    for entry in re.split(r"(?m)^Alias name: ", listing)[1:]:
        alias = entry.splitlines()[0]
        if "Entry type: PrivateKeyEntry" not in entry.splitlines():
            continue
        certificate = run(*base, "-exportcert", "-alias", alias)
        if hashlib.sha256(certificate).hexdigest() == CERT_SHA256:
            # This requests a signature on an in-memory CSR, verifying private-key access.
            run(*base, "-certreq", "-alias", alias, "-keypass:file", files.key_password)
            return alias
    raise ValueError("Der Originalschlüssel mit dem festgelegten Zertifikat ist hier nicht verfügbar.")


def enroll(args, target):
    if target.exists():
        key_alias(registered_files(target))
        print("Der registrierte Originalschlüssel ist verwendbar.")
        return
    parent = target.parent
    repo = Path(__file__).resolve().parents[1]
    if target.resolve().is_relative_to(repo) or target.resolve().is_relative_to("/workspace"):
        raise ValueError("Die private Signierablage muss außerhalb des Projekt-Workspaces liegen.")
    parent.mkdir(mode=0o700, parents=True, exist_ok=True)
    check_private(parent, directory=True)
    with tempfile.TemporaryDirectory(dir=parent) as staging:
        bundle = Path(staging) / "identity"
        bundle.mkdir(mode=0o700)
        shutil.copyfile(args.keystore, bundle / KEYSTORE)
        (bundle / KEYSTORE).chmod(0o600)
        for source, name in [(args.store_password_file, STORE_PASS),
                             (args.key_password_file or args.store_password_file, KEY_PASS)]:
            lines = source.read_bytes().splitlines()
            if len(lines) != 1 or not lines[0]:
                raise ValueError("Eine private Eingabedatei mit genau einer Passwortzeile ist erforderlich.")
            (bundle / name).write_bytes(lines[0] + b"\n")
            (bundle / name).chmod(0o600)
        key_alias(registered_files(bundle))
        try:
            bundle.rename(target)
        except OSError:
            if not target.is_dir():
                raise
            key_alias(registered_files(target))  # Another registration must have installed the same identity.
    print("Originalschlüssel registriert und privater Schlüsselzugriff geprüft.")


def sign(args, files):
    alias = key_alias(files)
    if args.output.exists():
        raise ValueError("Die Ausgabedatei existiert bereits.")
    with tempfile.TemporaryDirectory(dir=args.output.parent) as staging:
        candidate = Path(staging) / "candidate.apk"
        signed = Path(staging) / "signed.apk"
        shutil.copyfile(args.apk, candidate)
        if fingerprint(candidate) != args.sha256.lower():
            raise ValueError("Der APK-Hash stimmt nicht mit dem geprüften CI-Kandidaten überein.")
        manifest = run("aapt", "dump", "badging", candidate).decode()
        match = re.search(r"^package: name='([^']+)' versionCode='(\d+)'", manifest, re.M)
        if not match or match[1] != PACKAGE or int(match[2]) <= args.previous_version_code:
            raise ValueError("Paketkennung oder Versionsfolge passt nicht zum Update.")
        # apksigner consumes successive lines when the same password file is specified twice.
        key_pass = () if files.key_password == files.store_password else ("--key-pass", f"file:{files.key_password}")
        run("apksigner", "sign", "--ks", files.keystore, "--ks-type", "PKCS12",
            "--ks-key-alias", alias, "--ks-pass", f"file:{files.store_password}",
            *key_pass, "--v2-signing-enabled", "true",
            "--v3-signing-enabled", "true", "--v4-signing-enabled", "false",
            "--out", signed, candidate)
        report = run("apksigner", "verify", "--verbose", "--print-certs", signed).decode()
        # Build Tools 37 labels the single V3 signer by scheme instead of ordinal.
        signers = re.findall(r"^(?:Signer #\d+|V3\.0 Signer:) certificate SHA-256 digest: ([0-9a-f]{64})$", report, re.M)
        schemes = [f"Verified using v{v} scheme (APK Signature Scheme v{v}): true" for v in (2, 3)]
        required = ["Number of signers: 1", *schemes]
        if signers != [CERT_SHA256] or any(line not in report.splitlines() for line in required):
            raise ValueError("Originalzertifikat oder APK-Signaturschema konnte nicht bestätigt werden.")
        digest = fingerprint(signed)
        os.link(signed, args.output)  # Atomic and never overwrites an existing artifact.
    print(f"{PACKAGE} versionCode={match[2]} APK-SHA256={digest} Zertifikat-SHA256={CERT_SHA256} v2+v3")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    status = commands.add_parser("status", help="Check access to the original private key")
    register = commands.add_parser("enroll", help="Register on a persistent, private signing host")
    register.add_argument("keystore", type=Path)
    register.add_argument("store_password_file", type=Path)
    register.add_argument("--key-password-file", type=Path)
    release = commands.add_parser("sign", help="Verify and sign a CI candidate")
    for command in (status, release):
        command.add_argument("--library-dir", type=Path,
                             help="Absolute private mount containing both original library files")
    release.add_argument("apk", type=Path)
    release.add_argument("output", type=Path)
    release.add_argument("--sha256", required=True)
    release.add_argument("--previous-version-code", required=True, type=int)
    args = parser.parse_args()
    os.umask(0o077)
    if args.command == "enroll":
        enroll(args, location())
    elif args.command == "status":
        key_alias(signing_files(args.library_dir))
        print(f"Originalschlüssel verwendbar. Zertifikat-SHA256={CERT_SHA256}")
    elif args.previous_version_code < 1:
        sys.exit("Die höchste bereits ausgegebene Versionsnummer ist erforderlich.")
    else:
        sign(args, signing_files(args.library_dir))


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError) as error:
        sys.exit(str(error))
