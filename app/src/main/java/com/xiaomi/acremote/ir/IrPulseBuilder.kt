package com.xiaomi.acremote.ir

/**
 * Builds alternating IR pulse duration arrays (microseconds) expected by Android's
 * [android.hardware.ConsumerIrManager.transmit].
 *
 * Array format: [Mark1, Space1, Mark2, Space2, ...]
 */
class IrPulseBuilder {
    private val pulses = ArrayList<Int>(512)
    private var expectingMark: Boolean = true

    fun mark(durationMicros: Int): IrPulseBuilder {
        require(durationMicros > 0) { "Duration must be positive" }
        if (expectingMark) {
            pulses.add(durationMicros)
            expectingMark = false
        } else {
            // If already expecting space, merge with previous mark
            pulses[pulses.size - 1] += durationMicros
        }
        return this
    }

    fun space(durationMicros: Int): IrPulseBuilder {
        require(durationMicros > 0) { "Duration must be positive" }
        if (!expectingMark) {
            pulses.add(durationMicros)
            expectingMark = true
        } else {
            // If at the start or consecutive space, merge or ignore leading space
            if (pulses.isNotEmpty()) {
                pulses[pulses.size - 1] += durationMicros
            }
        }
        return this
    }

    fun pulse(markMicros: Int, spaceMicros: Int): IrPulseBuilder {
        mark(markMicros)
        space(spaceMicros)
        return this
    }

    fun bit(
        bit: Boolean,
        mark0: Int,
        space0: Int,
        mark1: Int,
        space1: Int
    ): IrPulseBuilder {
        if (bit) {
            pulse(mark1, space1)
        } else {
            pulse(mark0, space0)
        }
        return this
    }

    fun byteLsbFirst(
        byteVal: Int,
        mark0: Int,
        space0: Int,
        mark1: Int,
        space1: Int
    ): IrPulseBuilder {
        for (i in 0 until 8) {
            val bit = ((byteVal shr i) and 1) == 1
            bit(bit, mark0, space0, mark1, space1)
        }
        return this
    }

    fun byteMsbFirst(
        byteVal: Int,
        mark0: Int,
        space0: Int,
        mark1: Int,
        space1: Int
    ): IrPulseBuilder {
        for (i in 7 downTo 0) {
            val bit = ((byteVal shr i) and 1) == 1
            bit(bit, mark0, space0, mark1, space1)
        }
        return this
    }

    fun bitsLsbFirst(
        value: Long,
        bitCount: Int,
        mark0: Int,
        space0: Int,
        mark1: Int,
        space1: Int
    ): IrPulseBuilder {
        for (i in 0 until bitCount) {
            val bit = ((value shr i) and 1L) == 1L
            bit(bit, mark0, space0, mark1, space1)
        }
        return this
    }

    fun raw(vararg values: Int): IrPulseBuilder {
        for (v in values) {
            if (expectingMark) {
                mark(v)
            } else {
                space(v)
            }
        }
        return this
    }

    fun build(): IntArray {
        // Ensure even number of pulses (end with trailing space) or ending with mark
        // Android ConsumerIrManager handles both, but valid transmissions usually end on a final mark or space
        return pulses.toIntArray()
    }
}
