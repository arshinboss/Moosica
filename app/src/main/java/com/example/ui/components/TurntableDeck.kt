package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Track
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.BakeliteBlack
import com.example.ui.theme.BrassDark
import com.example.ui.theme.BrassGold
import com.example.ui.theme.BrassGoldLight
import com.example.ui.theme.CreamParchment
import com.example.ui.theme.MahoganyDark
import com.example.ui.theme.MahoganyLight
import com.example.ui.theme.MahoganyMedium
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.SpaceMonoFontFamily

@Composable
fun TurntableDeck(
    track: Track?,
    isPlaying: Boolean,
    rpmSpeed: Int,
    isCrackleEnabled: Boolean,
    onToggleRpm: () -> Unit,
    onToggleCrackle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotation duration in ms: 33 RPM = ~1818ms per rev; 45 RPM = ~1333ms per rev
    val rotationPeriodMs = if (rpmSpeed == 33) 1818 else 1333

    val infiniteTransition = rememberInfiniteTransition(label = "turntable_rotation")
    val spinningRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = rotationPeriodMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_spin"
    )

    val currentRotation = if (isPlaying) spinningRotation else 0f

    // Tonearm rotation angle: resting = 0 deg, on record = ~28 deg
    val tonearmAngle by animateFloatAsState(
        targetValue = if (isPlaying) 27f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "tonearm_angle"
    )

    Box(
        modifier = modifier
            .testTag("turntable_deck")
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(MahoganyMedium, MahoganyDark, Color(0xFF150D08))
                )
            )
            .border(2.dp, BrassDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Turntable Platter and Tonearm Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f),
                contentAlignment = Alignment.Center
            ) {
                // Platter recess shadow
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.92f)
                        .clip(CircleShape)
                        .background(Color(0xFF0D0B0A))
                        .border(3.dp, Color(0xFF3B2E24), CircleShape)
                )

                // Strobe rim dots around platter
                Canvas(modifier = Modifier.fillMaxSize(0.88f)) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dotsCount = 48
                    for (i in 0 until dotsCount) {
                        val angle = Math.toRadians((i * (360.0 / dotsCount)))
                        val dx = center.x + (radius - 4f) * kotlin.math.cos(angle).toFloat()
                        val dy = center.y + (radius - 4f) * kotlin.math.sin(angle).toFloat()
                        drawCircle(
                            color = BrassDark,
                            radius = 2f,
                            center = Offset(dx, dy)
                        )
                    }
                }

                // Spinning Vinyl Record
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.82f)
                        .rotate(currentRotation)
                        .clip(CircleShape)
                        .background(BakeliteBlack)
                        .border(1.5.dp, Color(0xFF2E2620), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Vinyl grooves drawing
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxR = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val grooveCount = 18
                        for (i in 4..grooveCount) {
                            val r = (i / grooveCount.toFloat()) * (maxR - 20f)
                            drawCircle(
                                color = Color(0xFF1E1C1A),
                                radius = r,
                                center = center,
                                style = Stroke(width = 1f)
                            )
                        }
                    }

                    // Center Label
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.42f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(AmberGlow, Color(0xFFC78200), Color(0xFF7A4A00))
                                )
                            )
                            .border(2.dp, BrassGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (track?.albumArtUri != null) {
                            AsyncImage(
                                model = track.albumArtUri,
                                contentDescription = "Album Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            // Authentic vintage center record label typography
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Text(
                                    text = "PRESTIGE HI-FI",
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    color = BakeliteBlack,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "$rpmSpeed RPM",
                                    fontFamily = SpaceMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 7.sp,
                                    color = Color(0xFF2B1D11)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = track?.title ?: "No Track",
                                    fontFamily = SpaceMonoFontFamily,
                                    fontSize = 7.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = BakeliteBlack,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Center metal spindle
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(BrassGoldLight)
                                .border(1.dp, BrassDark, CircleShape)
                        )
                    }
                }

                // Tonearm Overlay (Pivot at top right, needle swings over vinyl)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val pivot = Offset(size.width * 0.88f, size.height * 0.16f)

                    // Tonearm base gimbal
                    drawCircle(
                        color = Color(0xFF33271D),
                        radius = 22f,
                        center = pivot
                    )
                    drawCircle(
                        color = BrassGold,
                        radius = 12f,
                        center = pivot
                    )
                    drawCircle(
                        color = BrassDark,
                        radius = 6f,
                        center = pivot
                    )

                    // Calculate tonearm angle
                    val rad = Math.toRadians((120.0 + tonearmAngle.toDouble()))
                    val armLength = size.height * 0.65f
                    val elbowX = pivot.x - (armLength * 0.7f) * kotlin.math.cos(rad).toFloat()
                    val elbowY = pivot.y + (armLength * 0.7f) * kotlin.math.sin(rad).toFloat()

                    val headX = elbowX - (armLength * 0.3f) * kotlin.math.cos(rad + 0.15).toFloat()
                    val headY = elbowY + (armLength * 0.3f) * kotlin.math.sin(rad + 0.15).toFloat()

                    // Arm shadow
                    drawLine(
                        color = Color(0x55000000),
                        start = Offset(pivot.x + 3f, pivot.y + 4f),
                        end = Offset(headX + 3f, headY + 4f),
                        strokeWidth = 5f,
                        cap = StrokeCap.Round
                    )

                    // Silver/Brass Tonearm tube
                    drawLine(
                        color = BrassGold,
                        start = pivot,
                        end = Offset(elbowX, elbowY),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = BrassGoldLight,
                        start = Offset(elbowX, elbowY),
                        end = Offset(headX, headY),
                        strokeWidth = 3.5f,
                        cap = StrokeCap.Round
                    )

                    // Headshell / Cartridge (Shure style wooden/brass cartridge)
                    val headRad = rad + 0.35
                    val cartWidth = 14f
                    val cartLength = 26f
                    val cartCornerX = headX - cartLength * kotlin.math.cos(headRad).toFloat()
                    val cartCornerY = headY + cartLength * kotlin.math.sin(headRad).toFloat()

                    drawLine(
                        color = Color(0xFF261912),
                        start = Offset(headX, headY),
                        end = Offset(cartCornerX, cartCornerY),
                        strokeWidth = 8f,
                        cap = StrokeCap.Square
                    )

                    // Stylus tip
                    drawCircle(
                        color = AmberGlow,
                        radius = 2.5f,
                        center = Offset(cartCornerX, cartCornerY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lower Turntable Control Bar (Speed switch, Crackle filter toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 33 / 45 RPM Toggle
                Row(
                    modifier = Modifier
                        .testTag("rpm_toggle_button")
                        .clip(RoundedCornerShape(8.dp))
                        .background(MahoganyLight)
                        .border(1.dp, BrassDark, RoundedCornerShape(8.dp))
                        .clickable { onToggleRpm() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = BrassGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SPEED: $rpmSpeed RPM",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrassGoldLight
                    )
                }

                // Vinyl Crackle Noise Toggle
                Row(
                    modifier = Modifier
                        .testTag("crackle_toggle_button")
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCrackleEnabled) Color(0xFF332015) else MahoganyLight)
                        .border(1.dp, if (isCrackleEnabled) AmberGlow else BrassDark, RoundedCornerShape(8.dp))
                        .clickable { onToggleCrackle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Crackle Warmth",
                        tint = if (isCrackleEnabled) AmberGlow else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCrackleEnabled) "VINYL WARMTH ON" else "WARMTH OFF",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCrackleEnabled) AmberGlow else Color.Gray
                    )
                }
            }
        }
    }
}
