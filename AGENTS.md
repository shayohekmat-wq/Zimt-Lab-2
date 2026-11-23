# ZIMT Lab — engineering agent contract

## Mission and authority
Build a polished, reliable, independently testable Android lab whose features can later be integrated into the main ZIMT app. The **latest explicit user request** controls product scope; preserve established safety, privacy and data-integrity requirements. Read the repository README, current source and any active product plan before implementing. Never assume requirements or a framework from another lab apply here.

## Execution protocol
1. Inspect actual HEAD, relevant code, existing tests, CI configuration and the most recent verified checkpoint. Identify a concrete, end-to-end acceptance target; avoid repeating planning when implementation is possible.
2. Implement a coherent vertical slice: UI, state, persistence, animations/haptics where applicable, accessibility, cancellation and error handling, and focused tests. Preserve modularity and integration boundaries. Keep app-specific shell and package identity separate from reusable feature logic.
3. Build/test after meaningful changes, **read the real output**, repair failures and rerun. A commit, tool success, green compilation or launched CI job is not proof of product completion. Avoid needless build loops and version bumps.
4. Validate the strongest available evidence: unit tests, integration tests, CI, Android device behavior and upgrade compatibility. Record what was actually verified; mark device-only observations as unverified if no device was available.
5. For requested APK updates, verify exact source commit, applicationId, monotonic versionCode, correct original signing certificate, installability and existing-data retention. Never distribute an unsigned/debug APK as a safe update or expose signing secrets.
6. Stop at a delivered, verified result or a genuine tool/execution blocker after completing other independent work. When interrupted, leave a concise factual checkpoint (commit, tests, unresolved acceptance criteria and next executable action); resume from it rather than starting another plan.

## Non-negotiable engineering safeguards
- **No silent data loss:** never reset databases or discard user drawings, notes or settings to simplify an update. Test migrations and backups when relevant.
- **Portability to ZIMT:** keep domain models and business logic reusable, isolate Android Activity/navigation and storage adapters, use stable IDs and explicit interfaces. Different app IDs do not share private Android data automatically; design a tested export/import or migration path.
- **Reliability and UX:** handle gesture cancellation, multitouch, rotation/backgrounding, reduced motion, older supported Android versions and low-end hardware as relevant. Do not assert device behavior based only on source inspection.
- **Security:** do not commit private keys, tokens or credentials; do not introduce network services or permissions without a product need.
- **Scope discipline:** no speculative redesign, new features or destructive refactors in place of the requested milestone.

## Communication and recovery
For direct implementation orders, avoid progress-only messages and premature completion reports. Distinguish **verified**, **implemented but untested**, **incomplete** and **blocked**. If no project-specific checkpoint exists, create one only when a real interruption makes it useful. These instructions cannot extend execution limits or schedule background work.

## Lab 2 — Eimer-Liste
- Active requirements: `docs/BUILD_PLAN.md` **§17** (three user bucket designs; overrides older expansion in §§1–16) and **§19** (note lift/drag/warning/burn). Do not reactivate superseded design catalogs.
- Only `user-wicker-01`, `user-wood-01`, `user-zinc-01` are active user designs. Preserve six original paired REAR/FRONT PNGs, their shared geometry, stable bucket/note IDs, text, drawings and Room data.
- Accidental ignition during drag/scroll/multitouch/cancellation/lifecycle is forbidden; soft-delete and Undo must work reliably. Device verification of gestures/occlusion/haptics is distinct from CI success.
- On the development branch consult `docs/ACCEPTANCE_CRITERIA.md`, `docs/ARCHITECTURE.md`, `docs/DEVELOPMENT_STATUS.md`, `docs/RELEASE_CHECKLIST.md` and `docs/RELEASE_AND_DATA.md`. Those documents may not yet exist on main: check before relying on them.
- Current active feature work belongs on `development/integrated-milestone` until reviewed; do not silently merge incomplete work into main.
