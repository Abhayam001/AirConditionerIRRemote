package com.xiaomi.acremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaomi.acremote.model.AcSwing
import com.xiaomi.acremote.ui.theme.*

@Composable
fun QuickActionButtons(
    isPowerOn: Boolean,
    swing: AcSwing,
    turbo: Boolean,
    eco: Boolean,
    light: Boolean,
    sleep: Boolean,
    onToggleSwing: () -> Unit,
    onToggleTurbo: () -> Unit,
    onToggleEco: () -> Unit,
    onToggleLight: () -> Unit,
    onToggleSleep: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(
            label = "Swing",
            subLabel = swing.displayName,
            icon = Icons.Default.SwapVert,
            isActive = isPowerOn && swing != AcSwing.OFF,
            enabled = isPowerOn,
            activeColor = CyanAccent,
            onClick = onToggleSwing,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            label = "Turbo",
            subLabel = if (turbo) "On" else "Off",
            icon = Icons.Default.Bolt,
            isActive = isPowerOn && turbo,
            enabled = isPowerOn,
            activeColor = OrangeWarm,
            onClick = onToggleTurbo,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            label = "Eco",
            subLabel = if (eco) "On" else "Off",
            icon = Icons.Default.Eco,
            isActive = isPowerOn && eco,
            enabled = isPowerOn,
            activeColor = EmeraldPower,
            onClick = onToggleEco,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            label = "Light",
            subLabel = if (light) "On" else "Off",
            icon = Icons.Default.Lightbulb,
            isActive = isPowerOn && light,
            enabled = isPowerOn,
            activeColor = Color(0xFFFACC15),
            onClick = onToggleLight,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            label = "Sleep",
            subLabel = if (sleep) "On" else "Off",
            icon = Icons.Default.Bedtime,
            isActive = isPowerOn && sleep,
            enabled = isPowerOn,
            activeColor = Color(0xFFA78BFA),
            onClick = onToggleSleep,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    subLabel: String,
    icon: ImageVector,
    isActive: Boolean,
    enabled: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isActive) activeColor.copy(alpha = 0.18f)
                else DarkSurfaceVariant
            )
            .border(
                1.dp,
                if (isActive) activeColor.copy(alpha = 0.6f) else DarkCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = when {
                    !enabled -> TextMuted
                    isActive -> activeColor
                    else -> TextSecondary
                },
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = when {
                    !enabled -> TextMuted
                    isActive -> activeColor
                    else -> TextPrimary
                }
            )
            Text(
                text = subLabel,
                fontSize = 9.sp,
                color = if (enabled) TextMuted else TextMuted.copy(alpha = 0.5f)
            )
        }
    }
}
