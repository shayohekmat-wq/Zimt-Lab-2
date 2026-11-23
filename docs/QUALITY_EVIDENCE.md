# Evidence ledger — ZIMT quality gate

Use the sequence **PLANNED → IMPLEMENTED → CI_VERIFIED → DEVICE_VERIFIED**. Never skip a state or infer device verification from a successful build. Record each feature separately.

| Feature / claim | Source implementation | CI evidence (run URL + SHA) | Device evidence (model, OS, video/trace) | Current status |
|---|---|---|---|---|
| Moto G6 60 Hz animation performance | Motion First specification only | None | None | PLANNED / UNVERIFIED |
| Visual design and haptic quality | Design principles and QA contract | Not a CI-verifiable aesthetic property | None | PLANNED / UNVERIFIED |
| Standalone Performance Lab APK | Backlog in Motion First plan | None | None | PLANNED |
| Future ZIMT module portability | Interface proposal | None | None | PLANNED |

## Release gate
1. Exact commit builds and tests pass; retain CI run URL and test reports.
2. Data integrity, lifecycle and gesture cancellation have specific tests; manual device checks for haptics/visuals.
3. Confirm actual applicationId, monotonically increasing versionCode, original release certificate and upgrade from existing user data.
4. A missing test is UNVERIFIED, not PASS. Never represent a proposed architecture as production code.
5. Protect user drawings, notes, asset originals, IDs, and existing installs.

## New ideas: admission rule
Capture user goal, visual interaction sketch, acceptance criteria, minimum supported API, expected Moto G6 load, persistence impact, haptic fallback and future ZIMT interface. Prototype the smallest slice. Only extract a shared library after two Labs demonstrate the same real requirement. Do not add features to blank Labs 3/4 without an explicit brief.

## Lab 2 active development branch audit
- `NotePressSafety.kt` pure state machine and `NotePressSafetyTest.kt` boundary/cancellation tests are **IMPLEMENTED BUT NOT CI VERIFIED**. The existing Compose gesture handler has **not** been refactored to use this state machine; do not claim runtime safety from these tests alone.
- Workflow stale `Priority classes` assertion replaced with Motion First 2.2 document checks. **CI outcome not yet verified**.
- Existing note burn/drag/Undo, lifecycle and Room migration behavior: **UNVERIFIED** on a physical device. No release authorization.

## P0 integration checkpoint — 2026-11-23
- **IMPLEMENTED IN SOURCE:** `EimerApp.kt` note gesture now creates `NotePressSafety`, updates it on touch move, release and cancel, and gates ignition plus deferred `onIgnite` through `mayCommitBurn()` (commit `92e7d421`).
- **IMPLEMENTED TESTS, NOT EXECUTED:** `NotePressSafetyTest.kt` now includes drag/cancel after warning and timing boundary scenarios (commit `34a4b550`).
- **NOT VERIFIED:** No commit-specific GitHub workflow runs or combined status checks were returned by available connector queries. Do not claim compilation, passing tests, device behavior, or release readiness. Gesture handler still needs end-to-end Compose/device validation, including multitouch, lifecycle cancellation, pointer consumption, and Room/Undo upgrade tests.
- **P0 NEXT:** obtain CI run evidence, fix any compilation errors, test actual pointer sequences and process/lifecycle transitions, then verify original signing and upgrade before release.
