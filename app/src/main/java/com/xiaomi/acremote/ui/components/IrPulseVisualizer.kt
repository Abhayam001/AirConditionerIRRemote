package com.xiaomi.acremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaomi.acremote.ir.IrLogEntry
import com.xiaomi.acremote.ui.theme.*

@Composable
fun IrPulseVisualizer(
    hasHardwareEmitter: Boolean,
    lastEntry: IrLogEntry?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (hasHardwareEmitter) EmeraldPower else OrangeWarm)
                )

                Icon(
                    imageVector = if (hasHardwareEmitter) Icons.Default.Sensors else Icons.Default.Warning,
                    contentDescription = "IR Status",
                    tint = if (hasHardwareEmitter) CyanAccent else OrangeWarm,
                    modifier = Modifier.size(18.dp)
                )

                Column {
                    Text(
                        text = if (hasHardwareEmitter) "Xiaomi IR Blaster Active" else "Simulation / Virtual IR",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = if (lastEntry != null) {
                            "${lastEntry.brand} • ${lastEntry.pulseCount} pulses • ${String.format("%.1f", lastEntry.totalDurationMs)}ms @ ${lastEntry.frequencyHz / 1000}kHz"
                        } else {
                            "Ready to transmit (Carrier 38 kHz)"
                        },
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            // Mini visualizer pulse bar graph
            if (lastEntry != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sample = lastEntry.patternSample.take(8)
                    sample.forEachIndexed { index, pulse ->
                        val isMark = index % 2 == 0
                        val barHeight = ((pulse / 1000).coerceIn(4, 18)).dp
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(barHeight)
                                .background(if (isMark) CyanAccent else TextMuted.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}
