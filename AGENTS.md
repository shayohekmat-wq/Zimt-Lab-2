# Agent workflow — binding project-wide execution guidance

These instructions apply to autonomous runs and interactive development of Zimt-Lab-2. They do not supersede new user requirements or the product scope freeze in docs/BUILD_PLAN.md.

## Completion-driven execution
- Treat the **entire open user milestone**, not a commit, version bump, status message, or single changed file, as the unit of work.
- Before editing, inspect HEAD, recent changes, the relevant source paths, and available build/test workflows. Identify the minimum coherent set of files needed for a working end-to-end feature.
- Implement the full vertical slice, including UI, state management, persistence, error handling, accessibility, haptics, compatibility, and tests where applicable.
- Keep working after a successful commit, test, or build if substantial agreed work remains and tools/time permit. Avoid artificial stopping points.
- Prefer coherent, verified commits; checkpoint earlier only to protect work from interruption. A commit never implies completion.
- Spend time on actual implementation and verification before updating version numbers, docs, or writing a progress report. Do not increase versionCode just to show activity.
- Inspect existing code critically. Fix compilation failures, race conditions, lifecycle issues, gesture cancellation, regressions, and data-integrity problems before declaring a feature complete.
- Test on the strongest available level: static inspection, unit tests, Android build, CI, device behavior. Explicitly identify any level not verified; never invent successful tests.
- Only release an APK after verifying build, applicationId, versionCode, update signature, and safe data retention. Never commit private signing keys or secrets.
- If blocked by a real limit, leave a precise checkpoint: implemented files/commits, verified tests, known defects, next concrete action. Do not claim the milestone is complete.

## Product-specific constraints
- Honor the three-design scope freeze (§17 of docs/BUILD_PLAN.md). Preserve original user PNGs, matching REAR/FRONT coordinates, stable bucket IDs, existing notes/drawings, and non-destructive storage.
- For note gestures, prevent accidental ignition from drag, scrolling, cancellation, multitouch, and lifecycle changes. Verify soft-delete and Undo before release.
- ZIMT Labs features must remain modular and transferable into the main ZIMT app.

## Reporting
Distinguish **verified working**, **implemented but untested**, **incomplete**, and **blocked**. Prefer one concise end-of-session report over repeated progress-only messages.
