# Release gate — ZIMT Lab 2

Do not interpret a successful commit, debug APK or unsigned APK as a production-safe update.

## Required checks
- [ ] Approved milestone acceptance criteria are implemented; test evidence and known device limitations documented.
- [ ] Relevant CI debug compilation and unit tests passed on the **exact release commit**.
- [ ] Release APK compiled from the **same commit**; actual manifest package equals `de.zimt.eimerliste`.
- [ ] Actual APK versionCode is **greater than the highest version already distributed**, not merely greater than an obsolete hard-coded floor.
- [ ] Signed with the **original** update key; `apksigner verify --verbose --print-certs` confirms the expected certificate and applicable signature schemes.
- [ ] App upgrade preserves existing Room data, notes, drawings and design IDs; migrations tested for any schema changes.
- [ ] Signed artifact downloaded and checked for existence, nonzero size and correct provenance (commit, run, SHA-256).
- [ ] Device verification results and any untested behavior clearly disclosed; do not claim device tests if none ran.

## Signing
Original certificate SHA-256: `40e6b73383a29f9ca792595b369329e2ab8f9165f49eec69b4eb57a8ffe26fe1`.
Never put the keystore or passwords in Git, issue comments, build logs or public artifacts. If repository secrets are unavailable, use the existing private offline-signing route; **an unsigned APK is not a safe update**.

## Current workflow caveat
Development pushes compile debug and unsigned release candidates; signing remains private and main is not merged automatically. The Android 9 workflow installs the previous exact debug build, seeds real Room rows and preferences, installs the new debug APK with adb install -r and checks data. This verifies an emulator upgrade with a CI debug identity, not the production-key installation on a physical phone.

## Evidence to record
`release commit | CI run URL | unit-test result | APK filename | APK SHA-256 | package | versionCode | certificate fingerprint | upgrade-test result | device-test result`.

No automatic release on every development push. If any critical gate fails, fix and rerun or explicitly report the blocker.
