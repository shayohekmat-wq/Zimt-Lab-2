# ACTIVE SCOPE — ZIMT Lab 2

**Authority:** latest explicit user request > this active scope > historical `docs/BUILD_PLAN.md` §§1–16. Do not expand the bucket catalog without a new explicit user instruction.

## Product
Independently installable Eimer-Liste with persistent notes/drawings, charming tactile animation and eventual ZIMT integration.

## Design lab: exactly three user-original paired PNG buckets
- `user-wicker-01`: `flechtkorb_hinten.png` / `flechtkorb_vorne.png`
- `user-wood-01`: `holzkuebel_hinten.png` / `holzkuebel_vorne.png`
- `user-zinc-01`: `bucket_zinc_user_rear.png` / `bucket_zinc_user_front.png`
Keep original assets unchanged; REAR → PAPER → FRONT with identical geometry; retain legacy IDs/data without surfacing historical catalog experiments.

## Active note gesture feature
- Tap opens note. 700ms stationary lift; movement or release carries the note back into the normal bucket overview, never a nested bucket picker. Continuous drag or tap/drag from the carried-paper tray preserves ID, text and drawing; cancellation or a missed drop never mutates Room.
- 1500ms stationary warning with finite 2700ms haptic waveform; ignition no earlier than 4200ms total, with layered flames, embers and reversible soft-delete/Undo.
- Drawing paper expands from the bottom composer into the editor and reverses the identical geometry on return.
- Scroll, multitouch, cancellation, rotation/background, rapid input and accessibility alternatives must not cause accidental data loss.
- Animation and haptic polish welcome. No global freeze on unrelated feature development.

## Immediate engineering priorities
1. Replace duplicated gesture-phase booleans in Compose with a single authoritative state machine and explicit commit semantics; integrate pointer/lifecycle tests.
2. Obtain exact-HEAD CI evidence and fix failures; verify Room move/burn/Undo and upgrade integrity.
3. Continue visual refinement and Moto G6 / Nothing Phone 3a testing as independent work.
4. No release claim without original certificate, higher versionCode and real update-data verification.

See `docs/DEVELOPMENT_DOCTRINE.md`, `docs/ARCHITECTURE.md`, `docs/DEVELOPMENT_STATUS.md` and `docs/RELEASE_AND_DATA.md`. Historical `docs/BUILD_PLAN.md` is retained for provenance; its old expansion sections are not active instructions.

## Doctrine 7.0 — Scope und Akzeptanzabdeckung
Die bestehende Produktdefinition und ausdrückliche Nutzerentscheidungen gelten weiter; die Reihenfolge ist keine numerische Funktionsbegrenzung. `docs/WORK_QUEUE.md` bildet vorläufig AC-L2-001 bis AC-L2-008 ab. Nach jedem Slice aktuelle UI-/Daten-/Gesten-/Designkriterien gegen Code und Nachweise abgleichen und fehlende Aufgaben ergänzen. Historische Kataloggrenzen nur entsprechend aktueller ausdrücklicher Produktentscheidungen interpretieren; nicht als pauschalen Feature-Freeze.
