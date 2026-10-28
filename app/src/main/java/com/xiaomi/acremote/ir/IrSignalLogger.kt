package com.xiaomi.acremote.ir

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class IrLogEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date()),
    val brand: String,
    val actionSummary: String,
    val frequencyHz: Int,
    val pulseCount: Int,
    val totalDurationMs: Double,
    val isHardwareEmitted: Boolean,
    val patternSample: List<Int>
)

object IrSignalLogger {
    private val _logs = MutableStateFlow<List<IrLogEntry>>(emptyList())
    val logs: StateFlow<List<IrLogEntry>> = _logs.asStateFlow()

    fun logTransmission(
        brand: String,
        actionSummary: String,
        frequencyHz: Int,
        pattern: IntArray,
        isHardwareEmitted: Boolean
    ) {
        val totalMicros = pattern.fold(0L) { acc, d -> acc + d }
        val entry = IrLogEntry(
            brand = brand,
            actionSummary = actionSummary,
            frequencyHz = frequencyHz,
            pulseCount = pattern.size,
            totalDurationMs = totalMicros / 1000.0,
            isHardwareEmitted = isHardwareEmitted,
            patternSample = pattern.take(16)
        )
        _logs.value = listOf(entry) + _logs.value.take(49) // Keep last 50
    }

    fun clear() {
        _logs.value = emptyList()
    }
}
