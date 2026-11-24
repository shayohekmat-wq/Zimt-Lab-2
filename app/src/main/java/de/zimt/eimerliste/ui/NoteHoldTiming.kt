package de.zimt.eimerliste.ui

/**
 * Timings for the note's stationary press gesture.
 *
 * A drag permanently disarms ignition for the current gesture; lifting the finger
 * before ignition cancels it. No database operation belongs in this state machine.
 */
internal object NoteHoldTiming {
    const val LIFT_MS = 700L
    const val WARNING_MS = 1500L
    const val WARNING_DURATION_MS = 2700L
    const val IGNITION_MS = WARNING_MS + WARNING_DURATION_MS

    enum class Stage { PRESSED, LIFTED, WARNING, IGNITED, DRAGGING, RELEASED }

    fun stage(elapsedMs: Long, movedBeyondSlop: Boolean, released: Boolean): Stage = when {
        released -> Stage.RELEASED
        movedBeyondSlop -> Stage.DRAGGING
        elapsedMs >= IGNITION_MS -> Stage.IGNITED
        elapsedMs >= WARNING_MS -> Stage.WARNING
        elapsedMs >= LIFT_MS -> Stage.LIFTED
        else -> Stage.PRESSED
    }

    fun mayIgnite(elapsedMs: Long, everDragged: Boolean, stillPressed: Boolean): Boolean =
        stillPressed && !everDragged && elapsedMs >= IGNITION_MS
}
