package com.xiaomi.acremote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaomi.acremote.ui.components.*
import com.xiaomi.acremote.ui.theme.*
import com.xiaomi.acremote.ui.viewmodel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRemoteScreen(
    viewModel: RemoteViewModel
) {
    val acState by viewModel.acState.collectAsState()
    val lastLog by viewModel.lastLog.collectAsState()
    val allLogs by viewModel.logs.collectAsState()
    val currentPairingBrand by viewModel.currentPairingBrand.collectAsState()

    var showBrandPicker by remember { mutableStateOf(false) }
    var showPairingDialog by remember { mutableStateOf(false) }
    var showSignalInspector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Brand pill selector
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                                .clickable { showBrandPicker = true }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = acState.selectedBrand.brandName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = CyanAccent,
                                    fontSize = 16.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Change Brand",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Quick Auto-Pair wizard button
                    IconButton(
                        onClick = {
                            viewModel.testCurrentPairingCode()
                            showPairingDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Universal Match",
                            tint = CyanAccent
                        )
                    }

                    // Diagnostic signal inspector
                    IconButton(onClick = { showSignalInspector = true }) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "IR Signal Inspector",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // IR Hardware / Simulation status banner
            IrPulseVisualizer(
                hasHardwareEmitter = viewModel.hasHardwareEmitter,
                lastEntry = lastLog
            )

            // Tactile Power Button
            PowerButton(
                isPowerOn = acState.power,
                onTogglePower = { viewModel.togglePower() }
            )

            // Temperature Dial and Steppers
            TemperatureWheel(
                temperature = acState.targetTemperature,
                mode = acState.mode,
                isPowerOn = acState.power,
                onTemperatureChange = { viewModel.setTemperature(it) }
            )

            // Operation Mode Selector (Cool, Heat, Auto, Dry, Fan)
            AcModeSelector(
                selectedMode = acState.mode,
                isPowerOn = acState.power,
                onModeSelected = { viewModel.setMode(it) }
            )

            // Fan Speed / Blower Selector
            FanSpeedSelector(
                selectedFanSpeed = acState.fanSpeed,
                isPowerOn = acState.power,
                onFanSpeedSelected = { viewModel.setFanSpeed(it) }
            )

            // Quick Actions: Swing, Turbo, Eco, Light, Sleep
            QuickActionButtons(
                isPowerOn = acState.power,
                swing = acState.swing,
                turbo = acState.turbo,
                eco = acState.eco,
                light = acState.displayLight,
                sleep = acState.sleep,
                onToggleSwing = { viewModel.toggleSwing() },
                onToggleTurbo = { viewModel.toggleTurbo() },
                onToggleEco = { viewModel.toggleEco() },
                onToggleLight = { viewModel.toggleLight() },
                onToggleSleep = { viewModel.toggleSleep() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Brand Picker Bottom Sheet
        if (showBrandPicker) {
            BrandPickerSheet(
                selectedBrand = acState.selectedBrand,
                onBrandSelected = { viewModel.selectBrand(it) },
                onDismiss = { showBrandPicker = false }
            )
        }

        // Universal Auto-Match / Pairing Dialog
        if (showPairingDialog) {
            QuickPairingDialog(
                currentBrand = currentPairingBrand,
                onTestSignal = { viewModel.testCurrentPairingCode() },
                onNextCode = { viewModel.nextPairingCode() },
                onPreviousCode = { viewModel.previousPairingCode() },
                onConfirm = {
                    viewModel.confirmPairingCode()
                    showPairingDialog = false
                },
                onDismiss = { showPairingDialog = false }
            )
        }

        // IR Signal Inspector Bottom Sheet
        if (showSignalInspector) {
            IrSignalInspectorSheet(
                logs = allLogs,
                onDismiss = { showSignalInspector = false }
            )
        }
    }
}
