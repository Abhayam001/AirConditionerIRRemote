package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class SamsungProtocol : AcProtocol {
    override val protocolId: String = "SAMSUNG"
    override val brandName: String = "Samsung"
    override val carrierFrequency: Int = 38000

    private val headerMark = 4500
    private val headerSpace = 4500
    private val bit0Mark = 560
    private val bit0Space = 560
    private val bit1Mark = 560
    private val bit1Space = 1680
    private val stopMark = 560

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()

        // 14 bytes Samsung frame
        val bytes = IntArray(14)
        bytes[0] = 0x02
        bytes[1] = 0x92
        bytes[2] = 0x0F
        bytes[3] = 0x00
        bytes[4] = 0x00
        bytes[5] = 0x00
        bytes[6] = 0xF0
        bytes[7] = 0x01

        val powerNibble = if (state.power) 0x2 else 0x1
        val modeNibble = when (state.mode) {
            AcMode.AUTO -> 0x0
            AcMode.COOL -> 0x1
            AcMode.DRY -> 0x2
            AcMode.FAN -> 0x3
            AcMode.HEAT -> 0x4
        }
        bytes[8] = ((modeNibble shl 4) or powerNibble) and 0xFF

        val temp = state.targetTemperature.coerceIn(16, 30)
        val tempNibble = (temp - 16) and 0x0F
        val fanNibble = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0x0
            AcFanSpeed.LOW -> 0x2
            AcFanSpeed.MEDIUM -> 0x4
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x6
        }
        bytes[9] = ((fanNibble shl 4) or tempNibble) and 0xFF

        bytes[10] = 0x00
        bytes[11] = 0x00
        bytes[12] = 0x00

        // Checksum calculation (XOR / sum)
        var sum = 0
        for (i in 0..12) {
            sum += bytes[i]
        }
        bytes[13] = (sum.inv() + 1) and 0xFF

        // Build IR
        builder.pulse(headerMark, headerSpace)
        for (b in bytes) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
