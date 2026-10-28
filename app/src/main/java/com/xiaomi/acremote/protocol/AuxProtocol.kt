package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class AuxProtocol : AcProtocol {
    override val protocolId: String = "AUX"
    override val brandName: String = "AUX"
    override val carrierFrequency: Int = 38000

    private val headerMark = 9000
    private val headerSpace = 4500
    private val bit0Mark = 560
    private val bit0Space = 560
    private val bit1Mark = 560
    private val bit1Space = 1680
    private val stopMark = 560

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()
        val bytes = IntArray(13)

        // AUX signature
        bytes[0] = 0xC3
        bytes[1] = 0x00

        // Byte 2: Power and Mode
        val powerBit = if (state.power) 0x20 else 0x00
        val modeBits = when (state.mode) {
            AcMode.COOL -> 0x01
            AcMode.DRY -> 0x02
            AcMode.FAN -> 0x03
            AcMode.HEAT -> 0x04
            AcMode.AUTO -> 0x00
        }
        bytes[2] = powerBit or modeBits

        // Byte 3: Temperature (temp - 16)
        val temp = state.targetTemperature.coerceIn(16, 31)
        bytes[3] = (temp - 16) and 0x0F

        // Byte 4: Fan speed
        bytes[4] = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0x00
            AcFanSpeed.LOW -> 0x01
            AcFanSpeed.MEDIUM -> 0x02
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x03
        }

        // Checksum byte (modulo 256 sum)
        var sum = 0
        for (i in 0..11) sum += bytes[i]
        bytes[12] = sum and 0xFF

        builder.pulse(headerMark, headerSpace)
        for (b in bytes) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
