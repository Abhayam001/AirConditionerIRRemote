package com.xiaomi.acremote.model

/**
 * Supported Air Conditioner Brands.
 * Many brands share common OEM protocols (e.g. Gree, Midea, AUX, TCL, Daikin).
 */
enum class AcBrand(
    val brandName: String,
    val protocolKey: String,
    val description: String
) {
    GREE("Gree", "GREE", "Gree, GE, Tosot, Kinghome, Lennox"),
    MIDEA("Midea", "MIDEA", "Midea, Carrier, Pioneer, Senville, Danby, Comfee"),
    DAIKIN("Daikin", "DAIKIN", "Daikin Inverter & ARC433 series"),
    LG("LG", "LG", "LG Dual Inverter & Classic Split AC"),
    SAMSUNG("Samsung", "SAMSUNG", "Samsung WindFree & Digital Inverter"),
    PANASONIC("Panasonic", "PANASONIC", "Panasonic Aero Series, Econavi, Inverter"),
    MITSUBISHI("Mitsubishi", "MITSUBISHI", "Mitsubishi Electric (Mr Slim, MSZ series)"),
    AUX("AUX", "AUX", "AUX, York, Pioneer, Daewoo OEM"),
    TCL("TCL", "TCL", "TCL Elite, Miracle, Titan series"),
    HAIER("Haier", "HAIER", "Haier Smart Cool & Inverter series"),
    VOLTAS("Voltas", "GREE", "Voltas All-Weather / Inverter (Gree/OEM)"),
    HITACHI("Hitachi", "MIDEA", "Hitachi Yoshi / Kashikoi / OEM compatible"),
    CARRIER("Carrier", "MIDEA", "Carrier XPower / DuraFresh (Midea/OEM)"),
    WHIRLPOOL("Whirlpool", "GREE", "Whirlpool 6th Sense (Gree/OEM)"),
    BLUE_STAR("Blue Star", "GREE", "Blue Star Inverter Split AC"),
    GODREJ("Godrej", "MIDEA", "Godrej Green Balance series"),
    SHARP("Sharp", "PANASONIC", "Sharp Plasmacluster series"),
    TOSHIBA("Toshiba", "MIDEA", "Toshiba Daiseikai / Inverter"),
    HISENSE("Hisense", "AUX", "Hisense Silent / Comfort series"),
    ELECTROLUX("Electrolux", "GREE", "Electrolux ChillFlex series");

    companion object {
        fun getAllBrands(): List<AcBrand> = values().toList().sortedBy { it.brandName }
    }
}
