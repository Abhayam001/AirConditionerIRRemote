package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class PanasonicProtocol : AcProtocol {
    override val protocolId: String = "PANASONIC"
    override val brandName: String = "Panasonic"
    override val carrierFrequency: Int = 38000

    private val headerMark = 3500
    private val headerSpace = 1750
    private val bit0Mark = 450
    private val bit0Space = 450
    private val bit1Mark = 450
    private val bit1Space = 1300
    private val pauseSpace = 10000
    private val stopMark = 450

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()

        // Frame 1: 8 bytes preamble
        val f1 = intArrayOf(0x02, 0x20, 0xE0, 0x04, 0x00, 0x00, 0x00, 0x06)

        // Frame 2: 19 bytes
        val f2 = IntArray(19)
        f2[0] = 0x02
        f2[1] = 0x20
        f2[2] = 0xE0
        f2[3] = 0x04

        // Byte 4: Power & Mode
        val powerBit = if (state.power) 0x01 else 0x00
        val modeBits = when (state.mode) {
            AcMode.AUTO -> 0x00
            AcMode.HEAT -> 0x10
            AcMode.COOL -> 0x20
            AcMode.DRY -> 0x30
            AcMode.FAN -> 0x60
        }
        f2[4] = modeBits or powerBit

        // Byte 5: Temperature (temp * 2)
        val temp = state.targetTemperature.coerceIn(16, 30)
        f2[5] = (temp * 2) and 0xFF

        f2[6] = 0x80

        // Byte 7: Fan & Swing
        val fanBits = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0xA0
            AcFanSpeed.LOW -> 0x30
            AcFanSpeed.MEDIUM -> 0x50
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x70
        }
        f2[7] = fanBits

        f2[8] = 0x00
        f2[9] = 0x00
        f2[10] = 0x00
        f2[11] = 0x00
        f2[12] = 0x00
        f2[13] = 0x00
        f2[14] = 0x00
        f2[15] = 0x00
        f2[16] = 0x00
        f2[17] = 0x00

        // Checksum modulo 256 for frame 2
        var sum = 0
        for (i in 0..17) sum += f2[i]
        f2[18] = sum and 0xFF

        // Emit Frame 1
        builder.pulse(headerMark, headerSpace)
        for (b in f1) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.pulse(stopMark, pauseSpace)

        // Emit Frame 2
        builder.pulse(headerMark, headerSpace)
        for (b in f2) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
