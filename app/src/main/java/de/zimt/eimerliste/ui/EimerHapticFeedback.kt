package de.zimt.eimerliste.ui

import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationAttributes
import android.media.AudioAttributes
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import android.provider.Settings
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
    /**
     * A finite, perceptible warning waveform. The composition cancels it on
     * release, drag, lifecycle interruption or ignition; no background vibration.
     */
    fun beginBurnWarning(): (() -> Unit)? {
        val context = view.context
        if (!view.isHapticFeedbackEnabled ||
            Settings.System.getInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) == 0)
            return null
        val vibrator = if (Build.VERSION.SDK_INT >= 31)
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        else @Suppress("DEPRECATION") (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        if (vibrator == null || !vibrator.hasVibrator()) {
            performHapticFeedback(HapticFeedbackType.LongPress)
            return null
        }
        // Nine short beats over exactly 2.7 seconds, growing subtly in intensity.
        val timing = LongArray(18) { if (it % 2 == 0) 100L else 200L }
        val strengths = IntArray(18) { if (it % 2 == 0) (135 + it * 6).coerceAtMost(235) else 0 }
        return try {
            val effect = VibrationEffect.createWaveform(timing, strengths, -1)
            if (Build.VERSION.SDK_INT >= 33) {
                vibrator.vibrate(effect, VibrationAttributes.Builder()
                    .setUsage(VibrationAttributes.USAGE_TOUCH).build())
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(effect, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            }
            val stop: () -> Unit = { vibrator.cancel() }
            stop
        } catch (_: SecurityException) {
            performHapticFeedback(HapticFeedbackType.LongPress)
            null
        }
    }

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
