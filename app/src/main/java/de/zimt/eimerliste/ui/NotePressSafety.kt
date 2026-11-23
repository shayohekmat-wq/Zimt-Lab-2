package de.zimt.eimerliste.ui

/**
 * Pure state machine for a single stationary note press.
 * UI/Room side effects MUST be driven separately. Once disarmed, a gesture can never ignite.
 */
internal class NotePressSafety {
    enum class Phase { IDLE, PRESSED, LIFTED, WARNING, DRAGGING, IGNITED, CANCELLED, RELEASED }
    var phase: Phase = Phase.IDLE
        private set
    private var startedAt: Long = 0
    private var disarmed = false
    private var completed = false

    fun down(uptimeMs: Long) {
        startedAt = uptimeMs
        disarmed = false
        completed = false
        phase = Phase.PRESSED
    }

    fun move(beyondSlop: Boolean, multiplePointers: Boolean = false) {
        if (completed || phase == Phase.IDLE) return
        if (multiplePointers) { cancel(); return }
        if (beyondSlop) {
            disarmed = true
            phase = Phase.DRAGGING
        }
    }

    fun advance(uptimeMs: Long): Phase {
        if (completed || disarmed || phase == Phase.IDLE) return phase
        val elapsed = (uptimeMs - startedAt).coerceAtLeast(0)
        phase = when {
            elapsed >= NoteHoldTiming.IGNITION_MS -> { completed = true; Phase.IGNITED }
            elapsed >= NoteHoldTiming.WARNING_MS -> Phase.WARNING
            elapsed >= NoteHoldTiming.LIFT_MS -> Phase.LIFTED
            else -> Phase.PRESSED
        }
        return phase
    }

    fun release(): Phase {
        if (completed) return phase
        completed = true
        phase = Phase.RELEASED
        return phase
    }

    fun cancel(): Phase {
        if (completed) return phase
        completed = true
        disarmed = true
        phase = Phase.CANCELLED
        return phase
    }

    fun mayCommitBurn(): Boolean = phase == Phase.IGNITED && !disarmed
}
