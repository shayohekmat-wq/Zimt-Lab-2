# WORK QUEUE — ZIMT Lab 2 Eimer-Liste
**Branch:** development/integrated-milestone. Single canonical queue. Reconcile with current HEAD and evidence; concurrent autonomous commits may alter implementation.

| ID | Goal / acceptance criterion | Priority / dependencies | Status | Verification | Evidence / blocker / next action |
|---|---|---|---|---|---|
| L2-001 | AC-L2-001: note press/lift/warning/drag/ignite has one authoritative state machine | P0 / none | IMPLEMENTED | UNVERIFIED | NotePressSafety adapter and tests committed historically; execute tests and device sequences |
| L2-002 | AC-L2-002: ignition/soft delete persists once, Undo restores and animation interruption loses no note | P0 / L2-001 | IMPLEMENTED | CI_VERIFIED | Build 47 Room regression passed; persistent recovery UI present. Actual pointer/device validation outstanding. |
| L2-003 | AC-L2-003: all note content survives app restart and regular signed update | P0 / persistence | READY | UNVERIFIED | Verify Room migrations, versionCode, original cert, install-over-existing |
| L2-004 | AC-L2-004: paper writing/drawing/edit switching and folding preserve text and strokes | P1 / none | READY | UNVERIFIED | Audit editor lifecycle, Enter semantics, undo/redo and note layout |
| L2-005 | AC-L2-005: material animation/haptics clearly express bucket selection, note throw, shake and lift | P1 / L2-001 | READY | UNVERIFIED | Test on Nothing Phone 3a and Moto G6; preserve delightful style |
| L2-006 | AC-L2-006: bucket assets render correctly with REAR → PAPER → FRONT and documented provenance | P1 / assets | READY | UNVERIFIED | Check exact image assets, layering, licenses and actual design |
| L2-007 | AC-L2-007: separately installable Lab can migrate notes safely into ZIMT | P2 / schema | IMPLEMENTED | CI_VERIFIED | Build 47 versioned JSON export/import, stable IDs and Room restore tests passed. Host-app integration still outstanding. |
| L2-008 | AC-L2-008: build/tests on exact SHA and reproducible debug artifact | P2 / CI | IMPLEMENTED | CI_VERIFIED | Build 47 CI run 45294180118 + Room run 45294179965; new HEAD requires fresh evidence. |

## Coverage
Prioritization never caps authorized features. Reconcile current ACTIVE_SCOPE and observed code, add missing work; do not stop merely because a milestone is committed.
