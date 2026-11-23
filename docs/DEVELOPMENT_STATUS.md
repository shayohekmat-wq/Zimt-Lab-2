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

## Development Doctrine 5.0 rollout — 2026-11-23
- IMPLEMENTED: binding risk-based doctrine in all four Lab repositories, including Lab 2 main and development branch; no blanket feature freeze.
- IMPLEMENTED: `docs/ACTIVE_SCOPE.md` on Lab 2 main and development; historical `BUILD_PLAN.md` now begins with an explicit authority pointer. Active catalog is only the three original paired-PNG buckets; active note gesture scope remains unchanged.
- IMPLEMENTED: `AGENTS.md` pointers updated in all Labs; Lab 2 CI contract now checks doctrine and active scope files.
- IMPLEMENTED: hourly Lab 2 development automation prompt aligned with Doctrine 5.0, current branch routing, incremental testing and truthful release gates; schedule unchanged.
- UNVERIFIED: new CI checks have not been shown passing on current HEAD. Existing gesture state still has duplicated Compose flags; the single-source-of-truth refactor, Room/lifecycle regression suite, physical Moto G6/Nothing tests and signed update APK remain open. Do not claim these are complete.
- NEXT: safely refactor the gesture adapter with one authoritative phase and explicit commit semantics; run exact-SHA CI, fix failures, test migration/Undo and hardware behavior before release.

## Doctrine 5.0 implementation checkpoint — 2026-11-23 (single-phase gesture refactor)
- **IMPLEMENTED:** `EimerApp.kt` pointer adapter now delegates authoritative gesture phases, deadlines and release-origin to `NotePressSafety`; old duplicated `lifted/moved/warned/ignited/cancelled/released` flags removed from the note handler. `try/finally` clears drag/warning/burning visuals and drop target on cancellation.
- **IMPLEMENTED:** At explicit ignition the reversible `onIgnite` soft-delete path is invoked before animation delay rather than depending on the animation coroutine surviving 1250 ms. **Design caveat:** current sheet closure may interrupt visual burn choreography; needs a dedicated UI effect/state and device review before approval.
- **IMPLEMENTED TEST SOURCE:** Added release-phase, drag-after-warning, scroll-before-lift and ignition terminal assertions. Tests have NOT been run in a verified Gradle/Android environment.
- **PERFORMANCE:** Removed allocation of a temporary `setOf` for every gesture phase tracking check.
- **UNVERIFIED:** compile, unit/instrumentation tests, current SHA CI, Android device behavior, exact gesture geometry, physical haptics, original-signature update and data retention. The available local runtime has Java but no Gradle command or Android SDK; connector exposes no workflow dispatch action. Existing APKs are historical and NOT artifacts of this change.
- **NEXT:** run current-HEAD Android CI and inspect logs, fix compilation/test failures, validate full burn choreography and pointer coordinate spaces, verify upgrade with existing notes/drawings, then review/sign release.
