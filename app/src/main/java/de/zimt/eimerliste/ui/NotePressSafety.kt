package de.zimt.eimerliste.ui

/**
 * Pure note gesture state machine. The pointer adapter owns coordinates, not phase.
 * IGNITED is an explicit irreversible gesture decision; the repository still performs
 * a reversible soft-delete, and must not depend on the animation coroutine surviving.
 */
internal class NotePressSafety {
    enum class Phase { IDLE, PRESSED, LIFTED, WARNING, DRAGGING, IGNITED, CANCELLED, RELEASED }

    var phase: Phase = Phase.IDLE
        private set
    var releasedFrom: Phase = Phase.IDLE
        private set
    private var startedAt: Long = 0
    private var disarmed = false

    fun down(uptimeMs: Long) {
        startedAt = uptimeMs
        disarmed = false
        releasedFrom = Phase.IDLE
        phase = Phase.PRESSED
    }

    fun isTracking(): Boolean = when (phase) {
        Phase.PRESSED, Phase.LIFTED, Phase.WARNING, Phase.DRAGGING -> true
        else -> false
    }

    fun nextDeadlineMs(): Long? = when (phase) {
        Phase.PRESSED -> NoteHoldTiming.LIFT_MS
        Phase.LIFTED -> NoteHoldTiming.WARNING_MS
        Phase.WARNING -> NoteHoldTiming.IGNITION_MS
        else -> null
    }

    fun move(beyondSlop: Boolean, multiplePointers: Boolean = false) {
        if (!isTracking()) return
        if (multiplePointers) { cancel(); return }
        if (beyondSlop) {
            disarmed = true
            // Before the lift threshold, movement belongs to the scrolling parent.
            phase = if (phase == Phase.PRESSED) Phase.CANCELLED else Phase.DRAGGING
        }
    }

    fun advance(uptimeMs: Long): Phase {
        if (!isTracking() || disarmed) return phase
        val elapsed = (uptimeMs - startedAt).coerceAtLeast(0)
        phase = when {
            elapsed >= NoteHoldTiming.IGNITION_MS -> Phase.IGNITED
            elapsed >= NoteHoldTiming.WARNING_MS -> Phase.WARNING
            elapsed >= NoteHoldTiming.LIFT_MS -> Phase.LIFTED
            else -> Phase.PRESSED
        }
        return phase
    }

    fun release(): Phase {
        if (!isTracking()) return phase
        releasedFrom = phase
        phase = Phase.RELEASED
        return phase
    }

    fun cancel(): Phase {
        if (!isTracking()) return phase
        disarmed = true
        phase = Phase.CANCELLED
        return phase
    }

    fun mayCommitBurn(): Boolean = phase == Phase.IGNITED && !disarmed
}
