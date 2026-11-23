# Active acceptance criteria — ZIMT Lab 2

**Authority:** Latest explicit user request > explicit scope freeze in `docs/BUILD_PLAN.md` §17 > current feature requirements in §19 > older historical ideas in §§1–16. The older proposals for many designer buckets are **superseded**, not additional backlog. Preserve legacy data/assets but do not expose those designs in the active design lab.

This file is an actionable **verification checklist**, not evidence that any feature works. Mark an item passed only with a reproducible test/run/device observation, commit SHA and date. Compilation does not establish visual or gesture correctness.

| ID | Requirement | Automated evidence | Device/manual evidence | Current status |
|---|---|---|---|---|
| DESIGN-01 | Exactly three selectable user designs with stable IDs; legacy data preserved | Catalog/ID tests and asset integrity check | Verify gallery contains only three | Unverified for current HEAD |
| DESIGN-02 | Original REAR and FRONT PNGs share canvas geometry; render REAR → PAPER → FRONT | Layer ordering and paired-size tests | Inspect seams, transparency and paper occlusion on device | Unverified for current HEAD |
| DESIGN-03 | Paper drop/ejection and bucket feedback work in gallery and main grid | Animation/state tests where possible | Device video/screenshot and haptic check | Unverified for current HEAD |
| DATA-01 | Notes, drawings, bucket IDs, design choice survive restart and upgrade | Room DAO/repository tests and schema migration tests | Upgrade from installed version with real data | Unverified for current HEAD |
| GESTURE-01 | Tap opens note; 700 ms lifts; dragging transfers existing note to valid bucket | Boundary, movement, target and cancellation tests | Real touch test including invalid targets | Unverified for current HEAD |
| GESTURE-02 | Stationary 1500 ms warns; stationary 2000 ms ignites; drag/scroll/multitouch/cancel never ignites | Timing and state-machine tests | Real touch/rotation/background tests | Unverified for current HEAD |
| BURN-01 | Paper burn includes convincing edge/ash/smoke and respects reduced motion; Undo restores content | Soft-delete/Undo tests | Visual performance and accessibility checks | Unverified for current HEAD |
| RELEASE-01 | Correct app ID, higher versionCode, original signing certificate, installable update | CI package/signature verification | Existing-app upgrade without data loss | Not released for current HEAD |

**Release rule:** Any failed safety/data/signing criterion blocks an update release. Missing device verification must be explicitly reported; do not silently equate CI green with device acceptance. Where device testing is unavailable, label the result as unverified rather than claiming production-ready.

**Evidence entry format:** `ID | status | commit SHA | CI run/test command | device/version | observation | outstanding issue`. Store only concise new evidence in `docs/DEVELOPMENT_STATUS.md`; never overwrite history with assumptions.
