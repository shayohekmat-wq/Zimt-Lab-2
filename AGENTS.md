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


## Mandatory execution architecture: implement first, release last
1. **Environment preflight before feature work.** Determine whether a persistent repository checkout, Android SDK, JDK, Gradle, device/emulator, CI, signing materials and artifact retrieval are actually available. Record which capabilities are verified, not merely assumed. Prefer a cohesive development environment; if the current environment cannot build, continue all independent implementation work rather than stopping.
2. **Single integrated milestone.** Enumerate the complete acceptance checklist and dependencies; implement all feasible tasks in a coherent working tree before starting expensive builds, releases, or delivery operations. Do not treat one-file GitHub commits as milestones. Avoid starting long-running jobs while meaningful independent implementation work remains.
3. **Explicit development/release separation.** Make all source edits on `development/integrated-milestone`, specifying that branch explicitly in every GitHub write. Never push feature changes to `main`. Do not open a PR or dispatch release CI until the integrated milestone is implementation-complete and has passed the checks possible without a full build. The workflow must not trigger release builds on ordinary pushes.
4. **Verification gate.** After integration, run targeted static/unit checks, then the full Android build and tests; resolve failures, rerun, and verify the generated APK, application ID, strictly increasing versionCode, original signing certificate and non-destructive upgrade. A build is evidence of compilation, not proof of correct UX; device checks remain required where applicable.
5. **No false completion or premature reporting.** If a hard environment/time limit prevents the deliverable, leave a reproducible checkpoint listing missing capabilities, exact code state, unverified behaviors and next action. A status update is not a substitute for implementation.
6. **Cross-project default.** Apply this architecture to every software project: persistent working tree + complete vertical-slice implementation + gated build/test + verified release. Project-specific user requirements override these defaults.

**Release gate:** All agreed milestone tasks implemented → integration review → tests/build → signed upgrade verification → artifact delivery. Never reverse this order merely to produce visible activity.

## Optimized execution loop — apply to every software project

**Completion criterion:** A direct implementation request ends with the requested usable artifact and evidence of verification, or a concrete technical blocker after exhausting independently executable work. A successful tool call, file write, commit, CI trigger, or build is NEVER an answer-completion event.

1. **Preflight (short):** Read user requirements, current source and tests, identify acceptance criteria and dependencies. Verify actual repo write access, build/test path, artifact download and signing. Do not spend the entire run writing plans or meta-instructions.
2. **Implement a coherent batch:** Make the largest safely integrated set of code/UI/data/test changes feasible in the working branch. Preserve compatibility, existing data, signing secrets and project-specific scope. Intermediate commits are transport/checkpoints only.
3. **Run verification early enough to find integration errors:** After a coherent batch, invoke targeted tests or CI builds when technically useful. Do NOT delay every build until perfection: the historic successful ZIMT workflow used repeated build → diagnose → fix → rebuild loops. Do not trigger a release on every source push.
4. **Treat every tool result as the next input:** Read build status/logs; if failed, repair and rerun; if passed, inspect remaining acceptance criteria and verify runtime behavior where available. Never respond just because a tool returned successfully.
5. **Release gate:** Only when milestone features are implemented and required checks pass, produce release/update artifact; validate package ID, monotonic versionCode, original signing certificate, migrations and installation compatibility. Download and attach the exact verified artifact.
6. **Stop rules:** Stop only after the artifact is delivered, the user changes/cancels scope, or a real execution/environment limit blocks further useful work. In the last case report exact completed work, tests, blocking condition and reproducible next action; do not claim success.
7. **Communication:** For direct software-development orders, no small intermediate chat updates or status-only responses. Prefer one substantial final deliverable. Do not promise autonomous continuation beyond the actual execution environment; use persistent scheduled work only when available and authorized.

**Reusable across repositories:** Copy these principles into each project's own agent instructions or engineering workflow when write access exists. This repository file does not automatically control unrelated repositories or future chats.

## Product-specific constraints
- Honor the three-design scope freeze (§17 of docs/BUILD_PLAN.md). Preserve original user PNGs, matching REAR/FRONT coordinates, stable bucket IDs, existing notes/drawings, and non-destructive storage.
- For note gestures, prevent accidental ignition from drag, scrolling, cancellation, multitouch, and lifecycle changes. Verify soft-delete and Undo before release.
- ZIMT Labs features must remain modular and transferable into the main ZIMT app.

## Reporting
Distinguish **verified working**, **implemented but untested**, **incomplete**, and **blocked**. Prefer one concise end-of-session report over repeated progress-only messages.
