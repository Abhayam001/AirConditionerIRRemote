package com.xiaomi.acremote.model

/**
 * Complete state representation of an Air Conditioner.
 * Air Conditioner remotes are stateful: each button press sends the full operational state.
 */
data class AcState(
    val power: Boolean = true,
    val targetTemperature: Int = 24, // 16°C to 30°C
    val mode: AcMode = AcMode.COOL,
    val fanSpeed: AcFanSpeed = AcFanSpeed.AUTO,
    val swing: AcSwing = AcSwing.OFF,
    val turbo: Boolean = false,
    val eco: Boolean = false,
    val displayLight: Boolean = true,
    val sleep: Boolean = false,
    val selectedBrand: AcBrand = AcBrand.GREE
) {
    init {
        require(targetTemperature in 16..30) {
            "Temperature must be between 16°C and 30°C"
        }
    }
}
