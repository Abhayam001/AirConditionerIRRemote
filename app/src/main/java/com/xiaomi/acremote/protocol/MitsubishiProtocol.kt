package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class MitsubishiProtocol : AcProtocol {
    override val protocolId: String = "MITSUBISHI"
    override val brandName: String = "Mitsubishi Electric"
    override val carrierFrequency: Int = 38000

    private val headerMark = 3400
    private val headerSpace = 1750
    private val bit0Mark = 450
    private val bit0Space = 450
    private val bit1Mark = 450
    private val bit1Space = 1300
    private val stopMark = 450

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()
        val bytes = IntArray(18)

        // Mitsubishi signature header
        bytes[0] = 0x23
        bytes[1] = 0xCB
        bytes[2] = 0x26
        bytes[3] = 0x01
        bytes[4] = 0x00

        // Byte 5: Power
        bytes[5] = if (state.power) 0x20 else 0x00

        // Byte 6: Mode
        bytes[6] = when (state.mode) {
            AcMode.COOL -> 0x18
            AcMode.HEAT -> 0x08
            AcMode.AUTO -> 0x20
            AcMode.DRY -> 0x10
            AcMode.FAN -> 0x38
        }

        // Byte 7: Temperature (16-31 deg C -> temp - 16)
        val temp = state.targetTemperature.coerceIn(16, 31)
        bytes[7] = (temp - 16) and 0x0F

        // Byte 8: Fan speed
        bytes[8] = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0x00
            AcFanSpeed.LOW -> 0x01
            AcFanSpeed.MEDIUM -> 0x02
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x03
        }

        // Checksum: modulo 256 sum of bytes 0..16
        var sum = 0
        for (i in 0..16) sum += bytes[i]
        bytes[17] = sum and 0xFF

        // Header
        builder.pulse(headerMark, headerSpace)
        for (b in bytes) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
