package de.zimt.eimerliste.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.zimt.eimerliste.domain.ShakeDecision
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Register motion sensors only while a finger is holding a bucket.
 * No background service, permissions or battery-hungry permanent listener.
 */
@Composable
fun ObserveBucketShake(heldBucketId: String?, onShaken: (String) -> Unit) {
    val context = LocalContext.current
    val currentCallback by rememberUpdatedState(onShaken)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(heldBucketId, context, lifecycle) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            ?: manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var fired = false
        val detector = ShakeDecision()
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val id = heldBucketId ?: return
                if (fired || event.values.size < 3 ||
                    !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)
                val linear = event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION
                val motion = if (linear) magnitude else abs(magnitude - SensorManager.GRAVITY_EARTH)
                val threshold = if (linear) 10.5f else 7.5f
                if (detector.observePeak(motion, event.timestamp / 1_000_000L, threshold)) {
                    fired = true
                    currentCallback(id)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        fun updateRegistration() {
            manager.unregisterListener(listener)
            if (!fired && heldBucketId != null && sensor != null &&
                lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
        }
        val observer = LifecycleEventObserver { _, _ -> updateRegistration() }
        lifecycle.addObserver(observer)
        updateRegistration()
        onDispose {
            lifecycle.removeObserver(observer)
            manager.unregisterListener(listener)
        }
    }
}
