package de.zimt.eimerliste.domain

/** Two distinct deliberate shake peaks, with a lockout to prevent repeat draws. */
class ShakeDecision {
    private var firstPeakAt: Long = -1L
    private var lastPeakAt: Long = -1L
    private var lastTriggerAt: Long = -5000L

    fun observePeak(magnitude: Float, timeMs: Long, threshold: Float): Boolean {
        if (magnitude < threshold || timeMs - lastTriggerAt < 1600L) return false
        if (lastPeakAt >= 0 && timeMs - lastPeakAt < 145L) return false
        lastPeakAt = timeMs
        if (firstPeakAt < 0 || timeMs - firstPeakAt > 850L) {
            firstPeakAt = timeMs
            return false
        }
        firstPeakAt = -1L
        lastTriggerAt = timeMs
        return true
    }
}
