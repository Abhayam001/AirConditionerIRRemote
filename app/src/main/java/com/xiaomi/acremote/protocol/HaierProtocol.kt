package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class HaierProtocol : AcProtocol {
    override val protocolId: String = "HAIER"
    override val brandName: String = "Haier"
    override val carrierFrequency: Int = 38000

    private val headerMark = 3000
    private val headerSpace = 3000
    private val bit0Mark = 550
    private val bit0Space = 550
    private val bit1Mark = 550
    private val bit1Space = 1650
    private val stopMark = 550

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()
        val bytes = IntArray(14)

        // Haier lead signature
        bytes[0] = 0xA6
        bytes[1] = 0x59

        val powerBit = if (state.power) 0x20 else 0x00
        val modeBits = when (state.mode) {
            AcMode.AUTO -> 0x00
            AcMode.COOL -> 0x01
            AcMode.DRY -> 0x02
            AcMode.HEAT -> 0x03
            AcMode.FAN -> 0x04
        }
        bytes[2] = powerBit or modeBits

        val temp = state.targetTemperature.coerceIn(16, 30)
        bytes[3] = (temp - 16) and 0x0F

        bytes[4] = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0x00
            AcFanSpeed.LOW -> 0x01
            AcFanSpeed.MEDIUM -> 0x02
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x03
        }

        // Checksum
        var sum = 0
        for (i in 0..12) sum += bytes[i]
        bytes[13] = sum and 0xFF

        builder.pulse(headerMark, headerSpace)
        for (b in bytes) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
