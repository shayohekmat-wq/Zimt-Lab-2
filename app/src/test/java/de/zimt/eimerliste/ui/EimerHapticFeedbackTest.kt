package de.zimt.eimerliste.ui

import android.view.HapticFeedbackConstants
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import org.junit.Assert.*
import org.junit.Test

class EimerHapticFeedbackTest {
    @Test fun androidNineUsesCompatibleClickAndHoldEffects() {
        val tick = eimerHapticPlan(HapticFeedbackType.SegmentTick, 28)
        assertEquals(HapticFeedbackConstants.CLOCK_TICK, tick.preferred)
        assertEquals(tick.preferred, tick.fallback)
        val lift = eimerHapticPlan(HapticFeedbackType.GestureThresholdActivate, 28)
        assertEquals(HapticFeedbackConstants.KEYBOARD_TAP, lift.preferred)
        assertEquals(HapticFeedbackConstants.LONG_PRESS,
            eimerHapticPlan(HapticFeedbackType.Reject, 28).preferred)
    }

    @Test fun modernEffectsKeepSafeFallback() {
        val tick = eimerHapticPlan(HapticFeedbackType.SegmentTick, 34)
        assertEquals(HapticFeedbackConstants.SEGMENT_TICK, tick.preferred)
        assertEquals(HapticFeedbackConstants.CLOCK_TICK, tick.fallback)
        val confirm = eimerHapticPlan(HapticFeedbackType.Confirm, 30)
        assertEquals(HapticFeedbackConstants.CONFIRM, confirm.preferred)
        assertEquals(HapticFeedbackConstants.KEYBOARD_TAP, confirm.fallback)
    }
}
