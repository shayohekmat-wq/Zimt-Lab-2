# Development checkpoint — ZIMT Lab 2

This is an evidence ledger, **not** a completion claim. Reconcile against current HEAD and GitHub Actions on every resumed run.

## Current milestone
- Note interaction: lift after 700 ms; drag and drop into another bucket; stationary warning/haptics at 1500 ms; ignition at 2000 ms with animated embers/smoke/ash; accessible soft-delete Undo.
- Guard against accidental ignition during scroll, drag, cancellation, multitouch, and lifecycle changes.
- Design lab frozen to exactly three original user designs with paired rear/front PNG layers (see `docs/BUILD_PLAN.md` §17). Preserve Room notes, drawings and bucket IDs.

## Source status (unverified)
- `app/src/main/java/de/zimt/eimerliste/ui/EimerApp.kt`: drag and hold gesture changes, burn particle overlay and Undo snackbar path were edited on the development branch. **No successful compilation or device test has been verified for these edits.** Review pointer coordinate spaces and cancellation behavior before release.
- `app/src/main/java/de/zimt/eimerliste/ui/NoteHoldTiming.kt` and its JUnit tests exist; their passing status for current HEAD is **unknown**.
- `.github/workflows/android.yml`: development pushes/PRs run debug+unit tests; release compilation and signing gated to manual dispatch on main. **This workflow is only on the development branch until merged; main retains its own version.**

## Next executable actions
1. Check actual GitHub Actions run results for development HEAD; inspect compile/test failures and fix the Kotlin/Compose code.
2. Verify gesture tests, pointer coordinate conversion, sheet-to-bucket drop target reachability, burn cancellation, undo and lifecycle edge cases. Add tests as feasible.
3. Only after integration is ready, merge reviewed changes to main, manually dispatch release CI, retrieve artifact, verify original signing certificate and higher versionCode, then attach the exact APK.
4. If CI access/artifact retrieval is unavailable, document the exact missing capability and continue independent code/test work; never present an old APK as a new build.

## Engineering-system implementation checkpoint — 2026-11-23
- Agent contract consolidated in `AGENTS.md`; actual precedence clarified: active design freeze in BUILD_PLAN §17 supersedes old expansion ideas §§1–16, current note gesture specification in §19.
- Added `docs/ACCEPTANCE_CRITERIA.md` (8 evidence-based acceptance rows), `docs/RELEASE_CHECKLIST.md` (APK provenance/signing/update gate), and source/build map appended to existing `docs/ARCHITECTURE.md`.
- CI now uses per-ref concurrency to cancel superseded non-release runs and uploads JUnit XML reports even when tests fail; existing debug/test and manual release gates retained.
- Draft PR #6: https://github.com/shayohekmat-wq/Zimt-Lab-2/pull/6 . **Do not merge without reviewing the pre-existing untested UI changes.**
- At this checkpoint, the GitHub connector returned **zero pull-request workflow runs** for commit `c837d3dd560d4b7823462c81cd85e250a11fcea9`; this does **not** prove tests failed or succeeded. CI/device/signature verification remains outstanding.
- Next: check PR Actions/Checks for actual run and logs, fix compilation/test failures; inspect gesture drop target geometry and lifecycle behavior; perform data-upgrade/device tests before signed APK release. A working APK has **not** been produced by this engineering-system change.

## Masterplan 3.0 execution checkpoint — 2026-11-23
- **IMPLEMENTED:** corrected CI's obsolete adaptive-motion assertion to require Motion First 2.2 and Moto G6 reference documentation.
- **IMPLEMENTED, NOT INTEGRATED:** pure `NotePressSafety` state machine with tests for timing boundaries, drag disarm, multitouch cancellation, terminal release and restart. The production Compose pointer handler still uses its prior local flags and is **not yet guarded by this class**. Do not treat the new unit tests as proof that accidental ignition is solved.
- **DOCUMENTED:** quality evidence ledger, future integration contract and consolidated Motion First agent rules across Labs 1–4.
- **UNVERIFIED:** current CI test/build success, actual Motorola performance, touch/haptic UX, lifecycle and data-upgrade safety. The connector's commit-specific PR workflow-run query yielded no runs; absence of runs is not evidence of success or failure.
- **NEXT P0:** integrate and test state machine in Compose handler, add lifecycle/drag/Room regression tests, obtain a successful CI run and inspect artifacts before any signed APK or release.

## P0 integration checkpoint — 2026-11-23
- **IMPLEMENTED IN SOURCE:** `EimerApp.kt` note gesture now creates `NotePressSafety`, updates it on touch move, release and cancel, and gates ignition plus deferred `onIgnite` through `mayCommitBurn()` (commit `92e7d421`).
- **IMPLEMENTED TESTS, NOT EXECUTED:** `NotePressSafetyTest.kt` now includes drag/cancel after warning and timing boundary scenarios (commit `34a4b550`).
- **NOT VERIFIED:** No commit-specific GitHub workflow runs or combined status checks were returned by available connector queries. Do not claim compilation, passing tests, device behavior, or release readiness. Gesture handler still needs end-to-end Compose/device validation, including multitouch, lifecycle cancellation, pointer consumption, and Room/Undo upgrade tests.
- **P0 NEXT:** obtain CI run evidence, fix any compilation errors, test actual pointer sequences and process/lifecycle transitions, then verify original signing and upgrade before release.
