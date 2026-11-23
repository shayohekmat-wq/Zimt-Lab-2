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
