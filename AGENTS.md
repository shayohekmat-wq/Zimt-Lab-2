# ZIMT Lab 2 — Engineering agent contract

**Mission:** Deliver reliable, beautiful Android features in this independent lab while preserving existing user data and keeping functionality portable into the main ZIMT app. This document controls *how* to work, not what features to invent.

## 1. Authority and scope
- Follow the latest explicit user request and applicable safety/data constraints. Product requirements live in `docs/BUILD_PLAN.md`; **§17 supersedes historical design-expansion ideas in §§1–16**, and §19 specifies current note gestures. Do not silently combine conflicting historical plans.
- Active, testable requirements: `docs/ACCEPTANCE_CRITERIA.md`. If a genuine conflict remains, resolve from dated user instructions and record the decision; ask only when the conflict materially blocks safe implementation.
- Preserve exactly three active user-designed buckets (`user-wicker-01`, `user-wood-01`, `user-zinc-01`), six original REAR/FRONT PNGs, legacy IDs, notes and drawings. Do not add design styles without a new explicit user instruction.

## 2. Execution loop
1. Read current HEAD, the relevant source, `docs/DEVELOPMENT_STATUS.md`, acceptance criteria and CI results. Establish what is **verified**, not merely claimed. Read `docs/ARCHITECTURE.md` for code locations and build commands.
2. Implement a coherent end-to-end batch: UI, state, persistence, cancellation/error paths and appropriate tests. Keep scope focused. Commits, tool successes and partial features are checkpoints, **not** completion events.
3. After a meaningful batch, run targeted tests/CI. Read the actual failure output, fix root causes and rerun. Do not defer all builds until an imagined perfect final implementation; do not build releases on every push.
4. Compare results against each active acceptance criterion. Compilation does not verify gestures, visual quality, haptics or data migration. Mark device-only checks as unverified until observed.
5. Deliver the requested tested artifact when possible. For update APKs follow `docs/RELEASE_CHECKLIST.md` and `docs/RELEASE_AND_DATA.md`: package identity, strictly increasing versionCode, original certificate, non-destructive upgrade, exact artifact provenance.
6. If genuinely blocked by tools/time, first complete independent work; then leave a factual checkpoint with HEAD, tested commands/CI runs, failures and next executable action. Never invent successful tests, APKs or autonomous continuation.

## 3. Repository and quality gates
- Current work branch: `development/integrated-milestone`. Explicitly target it for writes; do not silently modify `main`. Review and integrate only when acceptance checks justify it.
- `app/build.gradle.kts` is authoritative for SDK, app ID and version; `.github/workflows/android.yml` is authoritative for actual CI triggers. Current development workflow compiles debug and runs JUnit on development pushes/PRs; release compilation/signing require manual dispatch on `main` **after the workflow is merged there**.
- Protect Room data and migrations; do not introduce destructive fallbacks. Note drag/scroll/multitouch/cancel/lifecycle must never accidentally ignite; soft-delete and Undo must preserve note content.
- Signing secrets never enter Git, logs or shared artifacts. Unsigned/debug APKs are not safe replacements for an installed signed release.
- Record objective release evidence: commit, run, tests, APK hash, package/version, certificate, upgrade/device checks. See `docs/RELEASE_CHECKLIST.md`.

## 4. Resume and communication
- Use `docs/DEVELOPMENT_STATUS.md` only as a concise evidence-based checkpoint; reconcile it against real code/CI on every resumed run. Do not create progress-only commits or restart planning when executable work remains.
- For direct development orders, avoid small interim reports; give one substantial result or an exact blocker. An `AGENTS.md` instruction cannot itself extend tool execution limits, schedule background work or guarantee APK delivery.
