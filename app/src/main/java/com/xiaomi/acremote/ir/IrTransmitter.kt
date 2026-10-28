package com.xiaomi.acremote.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class IrTransmitter(private val context: Context) {

    private val consumerIrManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    val hasHardwareEmitter: Boolean
        get() = consumerIrManager?.hasIrEmitter() == true

    val supportedFrequencyRanges: List<Pair<Int, Int>>
        get() {
            val ranges = consumerIrManager?.carrierFrequencies ?: return emptyList()
            return ranges.map { it.minFrequency to it.maxFrequency }
        }

    fun transmit(
        carrierFrequencyHz: Int = 38000,
        pattern: IntArray,
        brandName: String,
        actionSummary: String
    ): Result<Unit> {
        triggerHaptic()

        if (pattern.isEmpty()) {
            return Result.failure(IllegalArgumentException("IR Pattern is empty"))
        }

        return try {
            val hasIr = hasHardwareEmitter
            if (hasIr && consumerIrManager != null) {
                // Ensure carrier frequency is supported or clamped
                val targetFreq = resolveFrequency(carrierFrequencyHz)
                consumerIrManager.transmit(targetFreq, pattern)
                Log.d("IrTransmitter", "Transmitted ${pattern.size} pulses via IR hardware at ${targetFreq}Hz for $brandName ($actionSummary)")
            } else {
                Log.w("IrTransmitter", "Hardware IR emitter not available on this device. Simulating transmission for $brandName ($actionSummary).")
            }

            IrSignalLogger.logTransmission(
                brand = brandName,
                actionSummary = actionSummary,
                frequencyHz = carrierFrequencyHz,
                pattern = pattern,
                isHardwareEmitted = hasIr
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("IrTransmitter", "Failed to transmit IR signal", e)
            Result.failure(e)
        }
    }

    private fun resolveFrequency(preferredFreq: Int): Int {
        val ranges = consumerIrManager?.carrierFrequencies
        if (ranges.isNullOrEmpty()) return preferredFreq

        for (range in ranges) {
            if (preferredFreq in range.minFrequency..range.maxFrequency) {
                return preferredFreq
            }
        }
        // Fallback to first available or standard 38000
        return ranges.firstOrNull()?.minFrequency ?: preferredFreq
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {
            // Ignore vibration errors if disabled or unavailable
        }
    }
}
