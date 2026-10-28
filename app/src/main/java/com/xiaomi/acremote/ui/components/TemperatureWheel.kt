package com.xiaomi.acremote.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.ui.theme.*

@Composable
fun TemperatureWheel(
    temperature: Int,
    mode: AcMode,
    isPowerOn: Boolean,
    onTemperatureChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeGlowColor by animateColorAsState(
        targetValue = when {
            !isPowerOn -> TextMuted
            mode == AcMode.COOL -> CyanAccent
            mode == AcMode.HEAT -> OrangeWarm
            mode == AcMode.DRY -> Color(0xFF67E8F9)
            mode == AcMode.FAN -> Color(0xFFA78BFA)
            else -> CyanNeon
        },
        label = "glowColor"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Circular Dial
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activeGlowColor.copy(alpha = if (isPowerOn) 0.18f else 0.04f),
                            DarkSurfaceVariant.copy(alpha = 0.8f),
                            DarkSurface
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            activeGlowColor.copy(alpha = if (isPowerOn) 0.8f else 0.2f),
                            activeGlowColor.copy(alpha = if (isPowerOn) 0.2f else 0.05f),
                            activeGlowColor.copy(alpha = if (isPowerOn) 0.8f else 0.2f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isPowerOn) "$temperature°" else "--°",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 68.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isPowerOn) TextPrimary else TextMuted
                    )
                )
                Text(
                    text = if (isPowerOn) mode.displayName.uppercase() else "OFF",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = activeGlowColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stepper buttons (- and +)
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { if (isPowerOn && temperature > 16) onTemperatureChange(temperature - 1) },
                enabled = isPowerOn && temperature > 16,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary,
                    disabledContainerColor = DarkSurface.copy(alpha = 0.5f),
                    disabledContentColor = TextMuted
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Temperature",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Quick Preset Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(18, 22, 24, 26).forEach { preset ->
                    FilterChip(
                        selected = isPowerOn && temperature == preset,
                        onClick = { if (isPowerOn) onTemperatureChange(preset) },
                        enabled = isPowerOn,
                        label = { Text("${preset}°", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = activeGlowColor.copy(alpha = 0.25f),
                            selectedLabelColor = activeGlowColor,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = isPowerOn,
                            selected = temperature == preset,
                            borderColor = if (temperature == preset) activeGlowColor else DarkCardBorder
                        )
                    )
                }
            }

            FilledIconButton(
                onClick = { if (isPowerOn && temperature < 30) onTemperatureChange(temperature + 1) },
                enabled = isPowerOn && temperature < 30,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary,
                    disabledContainerColor = DarkSurface.copy(alpha = 0.5f),
                    disabledContentColor = TextMuted
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Temperature",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
