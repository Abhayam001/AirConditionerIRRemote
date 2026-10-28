package com.xiaomi.acremote.protocol

import com.xiaomi.acremote.model.AcBrand

object ProtocolRegistry {

    private val protocols = mapOf(
        "GREE" to GreeProtocol(),
        "MIDEA" to MideaProtocol(),
        "LG" to LgProtocol(),
        "DAIKIN" to DaikinProtocol(),
        "SAMSUNG" to SamsungProtocol(),
        "PANASONIC" to PanasonicProtocol(),
        "MITSUBISHI" to MitsubishiProtocol(),
        "AUX" to AuxProtocol(),
        "TCL" to TclProtocol(),
        "HAIER" to HaierProtocol()
    )

    fun getProtocol(brand: AcBrand): AcProtocol {
        return protocols[brand.protocolKey] ?: protocols["GREE"]!!
    }

    fun getProtocolByKey(key: String): AcProtocol {
        return protocols[key] ?: protocols["GREE"]!!
    }

    /**
     * List of unique protocol families available for universal auto-scanning.
     */
    fun getScanFamilies(): List<Pair<String, AcProtocol>> {
        return protocols.entries.map { it.key to it.value }
    }
}
