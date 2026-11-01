package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Track
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.BakeliteBlack
import com.example.ui.theme.BrassDark
import com.example.ui.theme.BrassGold
import com.example.ui.theme.BrassGoldLight
import com.example.ui.theme.CreamParchment
import com.example.ui.theme.MahoganyDark
import com.example.ui.theme.MahoganyMedium
import com.example.ui.theme.SpaceMonoFontFamily

@Composable
fun CassetteDeck(
    track: Track?,
    isPlaying: Boolean,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cassette_spin")
    val spoolRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spool_rotation"
    )

    val currentSpoolRotation = if (isPlaying) spoolRotation else 0f

    Box(
        modifier = modifier
            .testTag("cassette_deck")
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(MahoganyMedium, MahoganyDark, Color(0xFF130E0A))
                )
            )
            .border(2.dp, BrassDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Bay Label & Tape Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STUDIO CASSETTE DECK • CrO2",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold,
                    letterSpacing = 1.2.sp
                )

                // Mechanical 3-digit counter box
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F0D0C))
                        .border(1.dp, BrassDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val counterVal = ((progressFraction * 480).toInt()).coerceAtLeast(0)
                    Text(
                        text = String.format("%03d", counterVal),
                        fontFamily = SpaceMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AmberGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cassette Window Bay with Warm Incandescent Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.75f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF120E0B))
                    .border(2.dp, Color(0xFF453628), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Cassette Body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2B2520), Color(0xFF1C1814))
                            )
                        )
                        .border(1.dp, Color(0xFF574636), RoundedCornerShape(8.dp))
                ) {
                    // Cassette Label (Paper insert)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF2ECE1))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "SIDE A • 90 MIN",
                                fontFamily = SpaceMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = Color(0xFF8A1E14)
                            )
                            Text(
                                text = "DOLBY B-NR",
                                fontFamily = SpaceMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = Color(0xFF241C15)
                            )
                        }

                        Text(
                            text = track?.title ?: "Vintage Audio Cassette",
                            fontFamily = SpaceMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF1A1510)
                        )
                        Text(
                            text = track?.artist ?: "Pure Analog Sound",
                            fontFamily = SpaceMonoFontFamily,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF5E4F3F)
                        )
                    }

                    // Transparent Center Window with Dual Tape Spools
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(55.dp)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xBB0D0A08))
                            .border(1.dp, Color(0xFF6B553E), RoundedCornerShape(6.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Magnetic Tape bridge between spools
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val midY = size.height / 2f
                            drawLine(
                                color = Color(0xFF332015),
                                start = Offset(size.width * 0.2f, midY),
                                end = Offset(size.width * 0.8f, midY),
                                strokeWidth = 14f,
                                cap = StrokeCap.Round
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Spool (shrinks as tape plays)
                            val leftTapeRadius = (16.dp * (1f - progressFraction * 0.6f)).coerceAtLeast(8.dp)
                            TapeSpool(
                                rotation = currentSpoolRotation,
                                tapeRadius = leftTapeRadius
                            )

                            // Center Tape progress window ticks
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "◄ 50 ►",
                                    fontFamily = SpaceMonoFontFamily,
                                    fontSize = 7.sp,
                                    color = Color(0xFF877461)
                                )
                            }

                            // Right Spool (grows as tape plays)
                            val rightTapeRadius = (8.dp + (16.dp * progressFraction * 0.6f)).coerceAtMost(18.dp)
                            TapeSpool(
                                rotation = currentSpoolRotation,
                                tapeRadius = rightTapeRadius
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Tape Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPlaying) "STATUS: RUNNING" else "STATUS: STOPPED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPlaying) AmberGlow else Color.Gray
                )
                Text(
                    text = "EQ: 70µs TAPE TYPE II",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold
                )
            }
        }
    }
}

@Composable
fun TapeSpool(
    rotation: Float,
    tapeRadius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Brown Magnetic Tape roll around hub
        Box(
            modifier = Modifier
                .size(tapeRadius * 2)
                .clip(CircleShape)
                .background(Color(0xFF3B2516))
                .border(1.dp, Color(0xFF26150B), CircleShape)
        )

        // White plastic rotating gear teeth hub
        Box(
            modifier = Modifier
                .size(20.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(Color(0xFFEBE3D3))
                .border(1.dp, Color(0xFF9C8E76), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val teeth = 6
                val center = Offset(size.width / 2f, size.height / 2f)
                val r = size.minDimension / 2f
                for (i in 0 until teeth) {
                    val angle = Math.toRadians((i * (360.0 / teeth)))
                    val x = center.x + (r * 0.65f) * kotlin.math.cos(angle).toFloat()
                    val y = center.y + (r * 0.65f) * kotlin.math.sin(angle).toFloat()
                    drawCircle(
                        color = Color(0xFF1E1712),
                        radius = 2.5f,
                        center = Offset(x, y)
                    )
                }
                // Center hole
                drawCircle(
                    color = Color(0xFF0F0B08),
                    radius = 4f,
                    center = center
                )
            }
        }
    }
}
