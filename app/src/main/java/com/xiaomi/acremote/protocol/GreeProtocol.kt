package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.ir.IrPulseBuilder
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.model.AcState
import com.xiaomi.acremote.model.AcSwing

class GreeProtocol : AcProtocol {
    override val protocolId: String = "GREE"
    override val brandName: String = "Gree"
    override val carrierFrequency: Int = 38000

    private val headerMark = 9000
    private val headerSpace = 4500
    private val bit0Mark = 675
    private val bit0Space = 560
    private val bit1Mark = 675
    private val bit1Space = 1650
    private val midMark = 675
    private val midSpace = 20000
    private val endMark = 675
    private val endSpace = 40000

    override fun encode(state: AcState): IntArray {
        val builder = IrPulseBuilder()

        // 8 bytes payload
        val bytes = IntArray(8)

        // Byte 0: Mode (0..2), Power (3), Fan (4..5), Swing (6), Sleep (7)
        val modeBits = when (state.mode) {
            AcMode.AUTO -> 0
            AcMode.COOL -> 1
            AcMode.DRY -> 2
            AcMode.FAN -> 3
            AcMode.HEAT -> 4
        }
        val powerBit = if (state.power) 1 else 0
        val fanBits = when (state.fanSpeed) {
            AcFanSpeed.AUTO -> 0
            AcFanSpeed.LOW -> 1
            AcFanSpeed.MEDIUM -> 2
            AcFanSpeed.HIGH, AcFanSpeed.TURBO -> 3
        }
        val swingBit = if (state.swing != AcSwing.OFF) 1 else 0
        val sleepBit = if (state.sleep) 1 else 0

        bytes[0] = (modeBits and 0x07) or
                ((powerBit and 0x01) shl 3) or
                ((fanBits and 0x03) shl 4) or
                ((swingBit and 0x01) shl 6) or
                ((sleepBit and 0x01) shl 7)

        // Byte 1: Temperature (0..3) in range 16-30 mapped to 0-14
        val tempBits = (state.targetTemperature - 16).coerceIn(0, 14)
        bytes[1] = (tempBits and 0x0F)

        // Byte 2 & 3: Standard configuration
        bytes[2] = 0x20
        bytes[3] = 0x50

        // Byte 4: Turbo, Light, Vane
        val turboBit = if (state.turbo || state.fanSpeed == AcFanSpeed.TURBO) 1 else 0
        val lightBit = if (state.displayLight) 1 else 0
        bytes[4] = ((turboBit and 0x01) shl 4) or ((lightBit and 0x01) shl 7)

        // Byte 5 & 6
        bytes[5] = 0x00
        bytes[6] = 0x00

        // Byte 7: Checksum calculation
        // Gree checksum algorithm:
        var sum = 0
        for (i in 0..6) {
            sum += (bytes[i] and 0x0F) + ((bytes[i] shr 4) and 0x0F)
        }
        sum += 0x0A // Gree magic offset
        val checksumNibble = sum and 0x0F
        bytes[7] = (checksumNibble shl 4)

        // Assemble IR stream:
        // Header
        builder.pulse(headerMark, headerSpace)

        // Block 1 (Bytes 0 to 3) transmitted LSB first
        for (i in 0..3) {
            builder.byteLsbFirst(bytes[i], bit0Mark, bit0Space, bit1Mark, bit1Space)
        }

        // Mid pause
        builder.pulse(midMark, midSpace)

        // Block 2 (Bytes 4 to 7) transmitted LSB first
        for (i in 4..7) {
            builder.byteLsbFirst(bytes[i], bit0Mark, bit0Space, bit1Mark, bit1Space)
        }

        // Final closing pulse
        builder.pulse(endMark, endSpace)

        return builder.build()
    }
}
