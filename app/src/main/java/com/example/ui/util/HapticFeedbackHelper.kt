package com.example.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Utility helper providing tactile haptic and vibration feedback.
 * Combines Compose HapticFeedback with system Vibrator for tactile realism.
 */
object HapticFeedbackHelper {

    /**
     * Tactile click feedback for interactive buttons, chips, and subscription cards.
     */
    fun performClick(context: Context, composeHaptic: HapticFeedback? = null) {
        try {
            composeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) { }

        vibratePulse(context, durationMs = 35, amplitude = 180)
    }

    /**
     * Distinct tactile feedback for copy actions (e.g., copying Super Qi account number).
     * Emits a crisp double-pulse vibration.
     */
    fun performCopySuccess(context: Context, composeHaptic: HapticFeedback? = null) {
        try {
            composeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        } catch (_: Exception) { }

        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 45, 60, 50)
                val amplitudes = intArrayOf(0, 200, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                val timings = longArrayOf(0, 45, 60, 50)
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (_: Exception) { }
    }

    private fun vibratePulse(context: Context, durationMs: Long, amplitude: Int) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        amplitude.coerceIn(1, 255)
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) { }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }
}
