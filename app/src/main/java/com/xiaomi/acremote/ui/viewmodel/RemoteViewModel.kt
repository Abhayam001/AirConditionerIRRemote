package com.xiaomi.acremote.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xiaomi.acremote.ir.IrLogEntry
import com.xiaomi.acremote.ir.IrSignalLogger
import com.xiaomi.acremote.ir.IrTransmitter
import com.xiaomi.acremote.model.*
import com.xiaomi.acremote.protocol.ProtocolRegistry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val transmitter = IrTransmitter(application.applicationContext)

    private val _acState = MutableStateFlow(AcState())
    val acState: StateFlow<AcState> = _acState.asStateFlow()

    val hasHardwareEmitter: Boolean = transmitter.hasHardwareEmitter

    val logs: StateFlow<List<IrLogEntry>> = IrSignalLogger.logs

    val lastLog: StateFlow<IrLogEntry?> = IrSignalLogger.logs.map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Quick-Pairing state
    private val scanList: List<AcBrand> = AcBrand.getAllBrands()
    private val _pairingIndex = MutableStateFlow(0)
    val currentPairingBrand: StateFlow<AcBrand> = _pairingIndex.map { scanList[it % scanList.size] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, scanList[0])

    init {
        // Initial dry transmit or readiness check
    }

    fun selectBrand(brand: AcBrand) {
        _acState.update { it.copy(selectedBrand = brand) }
        transmitCurrentState("Brand Changed: ${brand.brandName}")
    }

    fun togglePower() {
        val newPower = !_acState.value.power
        _acState.update { it.copy(power = newPower) }
        transmitCurrentState(if (newPower) "Turn ON" else "Turn OFF")
    }

    fun setTemperature(temp: Int) {
        val clamped = temp.coerceIn(16, 30)
        _acState.update { it.copy(targetTemperature = clamped) }
        transmitCurrentState("Set Temp ${clamped}°C")
    }

    fun setMode(mode: AcMode) {
        _acState.update { it.copy(mode = mode) }
        transmitCurrentState("Mode: ${mode.displayName}")
    }

    fun setFanSpeed(speed: AcFanSpeed) {
        _acState.update { it.copy(fanSpeed = speed) }
        transmitCurrentState("Fan: ${speed.displayName}")
    }

    fun toggleSwing() {
        val nextSwing = when (_acState.value.swing) {
            AcSwing.OFF -> AcSwing.VERTICAL
            AcSwing.VERTICAL -> AcSwing.HORIZONTAL
            AcSwing.HORIZONTAL -> AcSwing.BOTH
            AcSwing.BOTH -> AcSwing.OFF
        }
        _acState.update { it.copy(swing = nextSwing) }
        transmitCurrentState("Swing: ${nextSwing.displayName}")
    }

    fun toggleTurbo() {
        val newTurbo = !_acState.value.turbo
        _acState.update { it.copy(turbo = newTurbo) }
        transmitCurrentState("Turbo: ${if (newTurbo) "ON" else "OFF"}")
    }

    fun toggleEco() {
        val newEco = !_acState.value.eco
        _acState.update { it.copy(eco = newEco) }
        transmitCurrentState("Eco: ${if (newEco) "ON" else "OFF"}")
    }

    fun toggleLight() {
        val newLight = !_acState.value.displayLight
        _acState.update { it.copy(displayLight = newLight) }
        transmitCurrentState("Light: ${if (newLight) "ON" else "OFF"}")
    }

    fun toggleSleep() {
        val newSleep = !_acState.value.sleep
        _acState.update { it.copy(sleep = newSleep) }
        transmitCurrentState("Sleep: ${if (newSleep) "ON" else "OFF"}")
    }

    // Quick-Pairing / Universal Code Search Cycle
    fun testCurrentPairingCode() {
        val brand = currentPairingBrand.value
        val protocol = ProtocolRegistry.getProtocol(brand)
        val pattern = protocol.encodePowerToggle(turnOn = true)

        transmitter.transmit(
            carrierFrequencyHz = protocol.carrierFrequency,
            pattern = pattern,
            brandName = brand.brandName,
            actionSummary = "Auto-Pair Test Signal"
        )
    }

    fun nextPairingCode() {
        _pairingIndex.update { (it + 1) % scanList.size }
        testCurrentPairingCode()
    }

    fun previousPairingCode() {
        _pairingIndex.update { if (it == 0) scanList.size - 1 else it - 1 }
        testCurrentPairingCode()
    }

    fun confirmPairingCode() {
        val pairedBrand = currentPairingBrand.value
        selectBrand(pairedBrand)
    }

    private fun transmitCurrentState(actionSummary: String) {
        viewModelScope.launch {
            val currentState = _acState.value
            val protocol = ProtocolRegistry.getProtocol(currentState.selectedBrand)
            val pattern = protocol.encode(currentState)

            transmitter.transmit(
                carrierFrequencyHz = protocol.carrierFrequency,
                pattern = pattern,
                brandName = currentState.selectedBrand.brandName,
                actionSummary = actionSummary
            )
        }
    }
}
