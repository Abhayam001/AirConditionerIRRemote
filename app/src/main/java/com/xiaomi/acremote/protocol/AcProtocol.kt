package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.model.AcState

interface AcProtocol {
    val protocolId: String
    val brandName: String
    val carrierFrequency: Int get() = 38000

    /**
     * Encodes the complete AC state into an alternating mark/space microsecond array
     * ready for ConsumerIrManager.transmit.
     */
    fun encode(state: AcState): IntArray

    /**
     * Generates a dedicated toggle or power burst (used during brand auto-search / pairing).
     */
    fun encodePowerToggle(turnOn: Boolean = true): IntArray {
        return encode(AcState(power = turnOn))
    }
}
