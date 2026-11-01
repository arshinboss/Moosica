package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.BakeliteBlack
import com.example.ui.theme.BrassDark
import com.example.ui.theme.BrassGold
import com.example.ui.theme.MahoganyDark
import com.example.ui.theme.MahoganyMedium
import com.example.ui.theme.VuMeterNeedle
import com.example.ui.theme.VuMeterParchment
import com.example.ui.theme.VuMeterParchmentDark
import com.example.ui.theme.VuMeterRedZone
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DualAnalogVuMeter(
    levelLeft: Float,
    levelRight: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("dual_vu_meter")
            .clip(RoundedCornerShape(10.dp))
            .background(MahoganyDark)
            .border(2.dp, BrassDark, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ANALOG VU LEVEL MONITOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "PEAK LEVEL 0 dB",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberGlow
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SingleVuMeterUnit(
                    channelLabel = "L - CH",
                    level = levelLeft,
                    modifier = Modifier.weight(1f)
                )
                SingleVuMeterUnit(
                    channelLabel = "R - CH",
                    level = levelRight,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SingleVuMeterUnit(
    channelLabel: String,
    level: Float,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(
        targetValue = level.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 65),
        label = "vu_level"
    )

    val isPeaking = animatedLevel > 0.85f

    Box(
        modifier = modifier
            .height(95.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(VuMeterParchment, VuMeterParchmentDark)
                )
            )
            .border(1.dp, Color(0xFF6B583E), RoundedCornerShape(6.dp))
    ) {
        // Backlight amber glow overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x40FFBE3D), Color.Transparent),
                        radius = 200f
                    )
                )
        )

        // Canvas for arc markings and swinging pointer needle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val pivotX = width / 2f
            val pivotY = height + 10f
            val needleLength = height * 0.95f

            // Draw calibrated scale arc
            val arcRadius = height * 0.72f
            val startAngle = 210f
            val sweepAngle = 120f

            // Black scale (left section)
            drawArc(
                color = Color(0xFF2A2015),
                startAngle = startAngle,
                sweepAngle = sweepAngle * 0.75f,
                useCenter = false,
                topLeft = Offset(pivotX - arcRadius, pivotY - arcRadius),
                size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // Red overload scale (right section)
            drawArc(
                color = VuMeterRedZone,
                startAngle = startAngle + sweepAngle * 0.75f,
                sweepAngle = sweepAngle * 0.25f,
                useCenter = false,
                topLeft = Offset(pivotX - arcRadius, pivotY - arcRadius),
                size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // Tick marks
            val tickCount = 9
            for (i in 0..tickCount) {
                val fraction = i / tickCount.toFloat()
                val angleDeg = startAngle + fraction * sweepAngle
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val tickLen = if (i % 2 == 0) 8f else 5f
                val isRed = fraction >= 0.75f
                val tickColor = if (isRed) VuMeterRedZone else Color(0xFF2E2419)

                val x1 = pivotX + (arcRadius - tickLen) * cos(angleRad).toFloat()
                val y1 = pivotY + (arcRadius - tickLen) * sin(angleRad).toFloat()
                val x2 = pivotX + arcRadius * cos(angleRad).toFloat()
                val y2 = pivotY + arcRadius * sin(angleRad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = if (i % 2 == 0) 2f else 1.2f
                )
            }

            // Needle angle calculation (-45 deg to +45 deg)
            val needleAngleDeg = (startAngle + animatedLevel * sweepAngle)
            val needleRad = Math.toRadians(needleAngleDeg.toDouble())

            val tipX = pivotX + needleLength * cos(needleRad).toFloat()
            val tipY = pivotY + needleLength * sin(needleRad).toFloat()

            // Needle shadow for vintage realism
            drawLine(
                color = Color(0x33000000),
                start = Offset(pivotX + 2f, pivotY + 2f),
                end = Offset(tipX + 2f, tipY + 2f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            // Needle line
            drawLine(
                color = VuMeterNeedle,
                start = Offset(pivotX, pivotY),
                end = Offset(tipX, tipY),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )

            // Bottom pivot casing
            drawCircle(
                color = Color(0xFF1E1712),
                radius = 16f,
                center = Offset(pivotX, pivotY)
            )
            drawCircle(
                color = BrassGold,
                radius = 7f,
                center = Offset(pivotX, pivotY)
            )
        }

        // Channel and peak LED indicator at top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channelLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF382C1E)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "VU",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4A3B2A),
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Peak indicator LED
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isPeaking) VuMeterRedZone else Color(0xFF6B2B28))
                        .border(0.5.dp, Color(0xFF381412), CircleShape)
                )
            }
        }
    }
}
