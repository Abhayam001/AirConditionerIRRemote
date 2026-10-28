package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState

class MideaProtocol : AcProtocol {
    override val protocolId: String = "MIDEA"
    override val brandName: String = "Midea"
    override val carrierFrequency: Int = 38000

    private val headerMark = 4400
    private val headerSpace = 4400
    private val bit0Mark = 560
    private val bit0Space = 560
    private val bit1Mark = 560
    private val bit1Space = 1680
    private val stopMark = 560

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()

        // 6 bytes array (data bytes alternating with inverted verification bytes)
        val bytes = IntArray(6)

        // Byte 0: Midea AC signature byte
        bytes[0] = 0xB2
        bytes[1] = bytes[0].inv() and 0xFF

        if (!state.power) {
            // Power OFF command
            bytes[2] = 0x7B
            bytes[3] = bytes[2].inv() and 0xFF
            bytes[4] = 0xE0
            bytes[5] = bytes[4].inv() and 0xFF
        } else {
            // Mode bits (bits 0..2)
            val modeBits = when (state.mode) {
                AcMode.AUTO -> 0x00
                AcMode.COOL -> 0x00
                AcMode.DRY -> 0x04
                AcMode.HEAT -> 0x0C
                AcMode.FAN -> 0x04
            }

            // Fan bits (bits 5..7)
            val fanBits = when (state.fanSpeed) {
                AcFanSpeed.AUTO -> 0xA0
                AcFanSpeed.LOW -> 0x20
                AcFanSpeed.MEDIUM -> 0x40
                AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 0x80
            }

            bytes[2] = (modeBits or fanBits) and 0xFF
            bytes[3] = bytes[2].inv() and 0xFF

            // Temperature mapping (17-30 deg C)
            val temp = state.targetTemperature.coerceIn(17, 30)
            val tempBits = when (temp) {
                17 -> 0x00
                18 -> 0x08
                19 -> 0x18
                20 -> 0x10
                21 -> 0x30
                22 -> 0x38
                23 -> 0x28
                24 -> 0x20
                25 -> 0x60
                26 -> 0x68
                27 -> 0x78
                28 -> 0x70
                29 -> 0x50
                30 -> 0x58
                else -> 0x20
            }

            bytes[4] = (tempBits or 0x00) and 0xFF
            bytes[5] = bytes[4].inv() and 0xFF
        }

        // Header
        builder.pulse(headerMark, headerSpace)

        // Encode 6 bytes, MSB first
        for (b in bytes) {
            builder.byteMsbFirst(b, bit0Mark, bit0Space, bit1Mark, bit1Space)
        }

        // Final stop bit
        builder.mark(stopMark)
        builder.space(40000)

        return builder.build()
    }
}
