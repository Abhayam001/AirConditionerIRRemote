package com.xiaomi.acremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaomi.acremote.model.AcFanSpeed
import com.xiaomi.acremote.ui.theme.*

@Composable
fun FanSpeedSelector(
    selectedFanSpeed: AcFanSpeed,
    isPowerOn: Boolean,
    onFanSpeedSelected: (AcFanSpeed) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BLOWER SPEED",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    letterSpacing = 1.2.sp
                )
            )
            Text(
                text = if (isPowerOn) selectedFanSpeed.displayName else "--",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isPowerOn) CyanAccent else TextMuted
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AcFanSpeed.values().forEach { speed ->
                val isSelected = selectedFanSpeed == speed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected && isPowerOn) CyanAccent.copy(alpha = 0.22f)
                            else Color.Transparent
                        )
                        .clickable(enabled = isPowerOn) { onFanSpeedSelected(speed) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Visual speed bars (1 to 4 bars)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(16.dp)
                        ) {
                            val totalBars = when (speed) {
                                AcFanSpeed.AUTO -> 1
                                AcFanSpeed.LOW -> 2
                                AcFanSpeed.MEDIUM -> 3
                                AcFanSpeed.HIGH -> 4
                                AcFanSpeed.TURBO -> 5
                            }
                            for (b in 1..totalBars) {
                                val barHeight = (4 + b * 2.5).dp
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(
                                            when {
                                                !isPowerOn -> TextMuted
                                                isSelected -> CyanAccent
                                                else -> TextSecondary.copy(alpha = 0.4f)
                                            }
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = speed.displayName,
                            fontSize = 11.sp,
                            color = when {
                                !isPowerOn -> TextMuted
                                isSelected -> CyanAccent
                                else -> TextSecondary
                            }
                        )
                    }
                }
            }
        }
    }
}
