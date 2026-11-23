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

## Historical checkpoints (superseded, retained for traceability)
Earlier audits described the pure `NotePressSafety` class as not integrated and later described partial integration. Those statements were true only at their historical checkpoints; they are **not** the current code state. See the current Doctrine 5.0 checkpoint below. The latest source has one authoritative phase in the note gesture adapter; this remains **UNVERIFIED by CI/device**.

## Doctrine 5.0 implementation checkpoint — 2026-11-23 (single-phase gesture refactor)
- **IMPLEMENTED:** `EimerApp.kt` pointer adapter now delegates authoritative gesture phases, deadlines and release-origin to `NotePressSafety`; old duplicated `lifted/moved/warned/ignited/cancelled/released` flags removed from the note handler. `try/finally` clears drag/warning/burning visuals and drop target on cancellation.
- **IMPLEMENTED:** At explicit ignition the reversible `onIgnite` soft-delete path is invoked before animation delay rather than depending on the animation coroutine surviving 1250 ms. **Design caveat:** current sheet closure may interrupt visual burn choreography; needs a dedicated UI effect/state and device review before approval.
- **IMPLEMENTED TEST SOURCE:** Added release-phase, drag-after-warning, scroll-before-lift and ignition terminal assertions. Tests have NOT been run in a verified Gradle/Android environment.
- **PERFORMANCE:** Removed allocation of a temporary `setOf` for every gesture phase tracking check.
- **UNVERIFIED:** compile, unit/instrumentation tests, current SHA CI, Android device behavior, exact gesture geometry, physical haptics, original-signature update and data retention. The available local runtime has Java but no Gradle command or Android SDK; connector exposes no workflow dispatch action. Existing APKs are historical and NOT artifacts of this change.
- **NEXT:** run current-HEAD Android CI and inspect logs, fix compilation/test failures, validate full burn choreography and pointer coordinate spaces, verify upgrade with existing notes/drawings, then review/sign release.
