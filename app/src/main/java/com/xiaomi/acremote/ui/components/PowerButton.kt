package com.xiaomi.acremote.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.xiaomi.acremote.ui.theme.*

@Composable
fun PowerButton(
    isPowerOn: Boolean,
    onTogglePower: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor by animateColorAsState(
        targetValue = if (isPowerOn) EmeraldPower else CrimsonPower,
        label = "powerColor"
    )

    Box(
        modifier = modifier
            .size(76.dp)
            .shadow(
                elevation = if (isPowerOn) 16.dp else 4.dp,
                shape = CircleShape,
                ambientColor = activeColor,
                spotColor = activeColor
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        activeColor.copy(alpha = if (isPowerOn) 0.35f else 0.15f),
                        DarkSurfaceVariant,
                        DarkSurface
                    )
                )
            )
            .border(2.dp, activeColor.copy(alpha = 0.8f), CircleShape)
            .clickable { onTogglePower() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = if (isPowerOn) "Turn Off" else "Turn On",
            tint = activeColor,
            modifier = Modifier.size(38.dp)
        )
    }
}
