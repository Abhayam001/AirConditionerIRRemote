package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class LgProtocol : AcProtocol {
    override val protocolId: String = "LG"
    override val brandName: String = "LG"
    override val carrierFrequency: Int = 38000

    private val headerMark = 8500
    private val headerSpace = 4250
    private val bit0Mark = 550
    private val bit0Space = 550
    private val bit1Mark = 550
    private val bit1Space = 1600
    private val stopMark = 550

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()

        // Nibbles 0..5 (MSB to LSB):
        val n0 = 0x8
        val n1 = 0x8 // LG prefix 0x88

        val n2: Int
        val n3: Int
        val n4: Int
        val n5: Int

        if (!state.power) {
            // Power off frame: 0x88C0051
            n2 = 0xC
            n3 = 0x0
            n4 = 0x0
            n5 = 0x5
        } else {
            n2 = 0x0 // normal operational update

            // Mode nibble (n3)
            n3 = when (state.mode) {
                AcMode.COOL -> 0x0
                AcMode.DRY -> 0x1
                AcMode.FAN -> 0x2
                AcMode.AUTO -> 0x3
                AcMode.HEAT -> 0x4
            }

            // Temp nibble (n4): temp - 15 (16°C -> 1, 24°C -> 9)
            val temp = state.targetTemperature.coerceIn(16, 30)
            n4 = (temp - 15) and 0x0F

            // Fan speed nibble (n5)
            n5 = when (state.fanSpeed) {
                AcFanSpeed.LOW -> 0x0
                AcFanSpeed.MEDIUM -> 0x2
                AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x4
                AcFanSpeed.AUTO -> 0x5
            }
        }

        // Checksum: sum of the first 6 nibbles modulo 16
        val checksum = (n0 + n1 + n2 + n3 + n4 + n5) and 0x0F

        // 28-bit word (MSB first)
        val word: Long = ((n0.toLong() and 0xF) shl 24) or
                ((n1.toLong() and 0xF) shl 20) or
                ((n2.toLong() and 0xF) shl 16) or
                ((n3.toLong() and 0xF) shl 12) or
                ((n4.toLong() and 0xF) shl 8) or
                ((n5.toLong() and 0xF) shl 4) or
                (checksum.toLong() and 0xF)

        // Header
        builder.pulse(headerMark, headerSpace)

        // 28 bits MSB first
        for (i in 27 downTo 0) {
            val bit = ((word shr i) and 1L) == 1L
            builder.bit(bit, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }

        // Stop bit
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
