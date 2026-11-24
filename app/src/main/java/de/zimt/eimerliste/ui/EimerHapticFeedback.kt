package de.zimt.eimerliste.ui

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalView

internal data class HapticPlan(val preferred: Int, val fallback: Int)

internal fun eimerHapticPlan(type: HapticFeedbackType, sdk: Int): HapticPlan {
    val fallback = when (type) {
        HapticFeedbackType.LongPress, HapticFeedbackType.Reject -> HapticFeedbackConstants.LONG_PRESS
        HapticFeedbackType.SegmentTick -> HapticFeedbackConstants.CLOCK_TICK
        else -> HapticFeedbackConstants.KEYBOARD_TAP
    }
    val preferred = when (type) {
        HapticFeedbackType.SegmentTick ->
            if (sdk >= 34) HapticFeedbackConstants.SEGMENT_TICK else fallback
        HapticFeedbackType.GestureThresholdActivate ->
            if (sdk >= 34) HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE else fallback
        HapticFeedbackType.Confirm ->
            if (sdk >= 30) HapticFeedbackConstants.CONFIRM else fallback
        HapticFeedbackType.Reject ->
            if (sdk >= 30) HapticFeedbackConstants.REJECT else fallback
        HapticFeedbackType.GestureEnd ->
            if (sdk >= 30) HapticFeedbackConstants.GESTURE_END else fallback
        else -> fallback
    }
    return HapticPlan(preferred, fallback)
}

/** Use platform feedback first and a compatible primitive if the effect is rejected.
 * The return value is framework acceptance, not proof of physical vibration.
 * Never bypass the user's system haptic setting.
 */
internal class EimerHapticFeedback(private val view: View) {
    fun performHapticFeedback(type: HapticFeedbackType) {
        val plan = eimerHapticPlan(type, Build.VERSION.SDK_INT)
        if (!view.performHapticFeedback(plan.preferred) && plan.preferred != plan.fallback)
            view.performHapticFeedback(plan.fallback)
    }
}

@Composable
internal fun rememberEimerHapticFeedback(): EimerHapticFeedback {
    val view = LocalView.current
    return remember(view) { EimerHapticFeedback(view) }
}
