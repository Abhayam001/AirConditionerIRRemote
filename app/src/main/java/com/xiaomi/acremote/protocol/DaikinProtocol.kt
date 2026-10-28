package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState
import com.xiaomi.acremote.model.AcSwing

class DaikinProtocol : AcProtocol {
    override val protocolId: String = "DAIKIN"
    override val brandName: String = "Daikin"
    override val carrierFrequency: Int = 38000

    private val headerMark = 3450
    private val headerSpace = 1700
    private val bit0Mark = 430
    private val bit0Space = 430
    private val bit1Mark = 430
    private val bit1Space = 1300
    private val framePauseSpace = 29000
    private val stopMark = 430

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()

        // Frame 1: 8 bytes
        val f1 = intArrayOf(0x11, 0xDA, 0x27, 0x00, 0xC5, 0x00, 0x00, 0x00)
        var sum1 = 0
        for (i in 0..6) sum1 += f1[i]
        f1[7] = sum1 and 0xFF

        // Frame 2: 19 bytes
        val f2 = IntArray(19)
        f2[0] = 0x11
        f2[1] = 0xDA
        f2[2] = 0x27
        f2[3] = 0x00
        f2[4] = 0x42

        // Byte 5: Power & Mode
        val powerBit = if (state.power) 0x01 else 0x00
        val modeBits = when (state.mode) {
            AcMode.AUTO -> 0x00
            AcMode.DRY -> 0x20
            AcMode.COOL -> 0x30
            AcMode.HEAT -> 0x40
            AcMode.FAN -> 0x60
        }
        f2[5] = modeBits or powerBit

        // Byte 6: Temperature (degrees * 2)
        val temp = state.targetTemperature.coerceIn(16, 32)
        f2[6] = (temp * 2) and 0xFF

        f2[7] = 0x00

        // Byte 8: Fan speed & swing
        val fanBits = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0xA0
            AcFanSpeed.LOW -> 0x30
            AcFanSpeed.MEDIUM -> 0x50
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x70
        }
        val swingBits = if (state.swing != AcSwing.OFF) 0x0F else 0x00
        f2[8] = fanBits or swingBits

        f2[9] = 0x00
        f2[10] = 0x00
        f2[11] = 0x00
        f2[12] = 0x00
        f2[13] = 0x00
        f2[14] = 0x00
        f2[15] = 0xC1
        f2[16] = 0x80
        f2[17] = 0x00

        // Frame 2 checksum: sum of f2[0..17] mod 256
        var sum2 = 0
        for (i in 0..17) sum2 += f2[i]
        f2[18] = sum2 and 0xFF

        // Emit Frame 1
        builder.pulse(headerMark, headerSpace)
        for (b in f1) {
            builder.byteLsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }
        builder.pulse(stopMark, framePauseSpace)

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
