package com.xiaomi.acremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaomi.acremote.model.AcMode
import com.xiaomi.acremote.ui.theme.*

@Composable
fun AcModeSelector(
    selectedMode: AcMode,
    isPowerOn: Boolean,
    onModeSelected: (AcMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "OPERATION MODE",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                letterSpacing = 1.2.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AcMode.values().forEach { mode ->
                val isSelected = selectedMode == mode
                val icon: ImageVector = when (mode) {
                    AcMode.COOL -> Icons.Default.AcUnit
                    AcMode.HEAT -> Icons.Default.WbSunny
                    AcMode.AUTO -> Icons.Default.Autorenew
                    AcMode.DRY -> Icons.Default.WaterDrop
                    AcMode.FAN -> Icons.Default.Air
                }
                val modeColor: Color = when (mode) {
                    AcMode.COOL -> CyanAccent
                    AcMode.HEAT -> OrangeWarm
                    AcMode.AUTO -> CyanNeon
                    AcMode.DRY -> Color(0xFF38BDF8)
                    AcMode.FAN -> Color(0xFFA78BFA)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected && isPowerOn) modeColor.copy(alpha = 0.22f)
                            else Color.Transparent
                        )
                        .clickable(enabled = isPowerOn) { onModeSelected(mode) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mode.displayName,
                            tint = when {
                                !isPowerOn -> TextMuted
                                isSelected -> modeColor
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mode.displayName,
                            fontSize = 11.sp,
                            color = when {
                                !isPowerOn -> TextMuted
                                isSelected -> modeColor
                                else -> TextSecondary
                            }
                        )
                    }
                }
            }
        }
    }
}
