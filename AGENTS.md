# ZIMT Lab 2 — Agent instructions

Android/Kotlin/Jetpack Compose app. Features built in this lab must remain modular and transferable into the main ZIMT app. Current product requirements and acceptance criteria: `docs/BUILD_PLAN.md`; direct user instructions take precedence.

## Execute the requested milestone
1. Inspect the relevant source, tests, requirements and available tools. Define concrete acceptance criteria internally; avoid spending the run on planning or process documentation.
2. Implement a **substantial, coherent vertical slice** (UI, state, persistence, edge cases and tests as applicable). Keep working after individual edits, tool results or commits. Do not expand the agreed scope.
3. **Verify iteratively:** after a meaningful batch, run targeted tests/builds as available; inspect failures, fix them and rerun. Do not postpone all compilation until a supposedly perfect implementation. A successful build is not proof of correct device behavior.
4. **Finish the actual deliverable:** for a release, verify the package, versionCode, signing certificate and safe update path; retrieve and deliver the tested APK. Never present an old or unsigned build as a safe update.
5. **Only stop** after delivering the requested result, a user-directed scope change, or a genuine execution limit. If blocked, continue independent work first, then report exactly what changed, what was tested, what remains and what is blocking it. Never claim unrun tests or completed features.
6. For direct development orders, **no small progress-only replies**. A file write, commit, CI trigger or partial implementation is not a completion signal. Do not promise work outside the actual execution session.

## Repository and build
- Implement current milestone work on `development/integrated-milestone` and explicitly select that branch for connector writes. Do not silently modify `main`; merge only when the integrated changes are ready.
- Android application ID: `de.zimt.eimerliste`; `minSdk = 26`; JDK 17; Gradle 9.6 in CI. Read `app/build.gradle.kts` for current SDK, dependencies and version.
- CI workflow: `.github/workflows/android.yml`. **Inspect its actual triggers before assuming CI is gated:** at the time of this edit it builds on pushes to `main` and pull requests. A release build must not be mistaken for a verified signed update.
- CI build/test commands: `gradle --no-daemon :app:assembleDebug :app:testDebugUnitTest`, then `gradle --no-daemon :app:assembleRelease`. Inspect CI logs and repeat the fix/build cycle as necessary.
- Preserve package identity, existing Room data, note IDs and drawings. Update versionCode monotonically for a release. Do not commit signing keys, credentials or other secrets.

## Non-negotiable product constraints
- Honor the three-design scope freeze in `docs/BUILD_PLAN.md` §17: only the original Flechtkorb, Holzkübel and Zinkeimer user designs, each with precisely aligned REAR/FRONT PNGs. Do not replace or mutate original assets.
- Note gestures must not accidentally ignite during drag, scroll, cancellation, multitouch or lifecycle changes. Preserve soft-delete and accessible Undo.
- Validate update compatibility and data preservation; distinguish static inspection, successful compilation, automated tests and actual device testing.

These are repository instructions, not a mechanism that guarantees long-running execution or automatically applies to other repositories.
