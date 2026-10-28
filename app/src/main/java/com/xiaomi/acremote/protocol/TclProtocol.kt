package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class TclProtocol : AcProtocol {
    override val protocolId: String = "TCL"
    override val brandName: String = "TCL"
    override val carrierFrequency: Int = 38000

    private val headerMark = 3100
    private val headerSpace = 1650
    private val bit0Mark = 500
    private val bit0Space = 500
    private val bit1Mark = 500
    private val bit1Space = 1000
    private val stopMark = 500

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()
        val bytes = IntArray(14)

        // TCL lead bytes
        bytes[0] = 0x23
        bytes[1] = 0xCB
        bytes[2] = 0x26

        // Power & Mode
        val powerBit = if (state.power) 0x04 else 0x00
        val modeBits = when (state.mode) {
            AcMode.COOL -> 0x01
            AcMode.DRY -> 0x02
            AcMode.AUTO -> 0x00
            AcMode.HEAT -> 0x04
            AcMode.FAN -> 0x07
        }
        bytes[3] = powerBit or modeBits

        // Temperature (temp - 16)
        val temp = state.targetTemperature.coerceIn(16, 31)
        bytes[4] = (temp - 16) and 0x0F

        // Fan speed
        bytes[5] = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0x00
            AcFanSpeed.LOW -> 0x02
            AcFanSpeed.MEDIUM -> 0x04
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x06
        }

        // Checksum byte 13
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
