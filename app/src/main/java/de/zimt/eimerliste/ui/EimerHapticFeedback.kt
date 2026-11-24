package de.zimt.eimerliste.ui

import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationAttributes
import android.media.AudioAttributes
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
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

/** Direct finite motor pulses instead of relying on OEM View effect acceptance.
 * Touch usage lets Android honor the device's vibration settings. Default
 * amplitude works on motors without amplitude control, including older phones.
 */
internal class EimerHapticFeedback(private val view: View) {
    private val context = view.context
    private val vibrator: Vibrator?
        get() = if (Build.VERSION.SDK_INT >= 31)
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        else @Suppress("DEPRECATION") (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)

    private fun play(effect: VibrationEffect): Boolean {
        val motor = vibrator ?: return false
        if (!motor.hasVibrator()) return false
        return try {
            if (Build.VERSION.SDK_INT >= 33) motor.vibrate(effect, VibrationAttributes.Builder()
                .setUsage(VibrationAttributes.USAGE_TOUCH).build())
            else {
                @Suppress("DEPRECATION")
                motor.vibrate(effect, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            }
            true
        } catch (_: RuntimeException) { false }
    }

    fun beginBurnWarning(): (() -> Unit)? {
        val motor = vibrator ?: return null
        if (!motor.hasVibrator()) return null
        val timing = longArrayOf(0, 140, 160, 140, 160, 140, 160, 150, 150,
            160, 140, 170, 130, 180, 120, 200, 100, 240, 60)
        val effect = if (motor.hasAmplitudeControl()) {
            val amps = IntArray(timing.size) { if (it % 2 == 0) 0 else (165 + it * 5).coerceAtMost(255) }
            VibrationEffect.createWaveform(timing, amps, -1)
        } else VibrationEffect.createWaveform(timing, -1)
        if (!play(effect)) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return null
        }
        return { motor.cancel() }
    }

    fun performHapticFeedback(type: HapticFeedbackType) {
        val duration = when (type) {
            HapticFeedbackType.LongPress -> 65L
            HapticFeedbackType.Reject -> 52L
            HapticFeedbackType.Confirm -> 44L
            HapticFeedbackType.GestureThresholdActivate -> 34L
            HapticFeedbackType.GestureEnd -> 30L
            else -> 24L
        }
        if (!play(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))) {
            val plan = eimerHapticPlan(type, Build.VERSION.SDK_INT)
            if (!view.performHapticFeedback(plan.preferred) && plan.preferred != plan.fallback)
                view.performHapticFeedback(plan.fallback)
        }
    }
}

@Composable
internal fun rememberEimerHapticFeedback(): EimerHapticFeedback {
    val view = LocalView.current
    return remember(view) { EimerHapticFeedback(view) }
}
