package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RepeatMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.BakeliteBlack
import com.example.ui.theme.BrassDark
import com.example.ui.theme.BrassGold
import com.example.ui.theme.BrassGoldLight
import com.example.ui.theme.CreamMuted
import com.example.ui.theme.CreamParchment
import com.example.ui.theme.LedGreen
import com.example.ui.theme.MahoganyDark
import com.example.ui.theme.MahoganyLight
import com.example.ui.theme.MahoganyMedium
import com.example.ui.theme.SpaceMonoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VintageProgressBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val formatTime: (Long) -> String = { ms ->
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        String.format("%02d:%02d", m, s)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(currentPositionMs),
                fontFamily = SpaceMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AmberGlow
            )
            Text(
                text = "TAPE HEAD POSITION",
                style = MaterialTheme.typography.labelSmall,
                color = BrassDark,
                fontSize = 9.sp
            )
            Text(
                text = formatTime(durationMs),
                fontFamily = SpaceMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CreamMuted
            )
        }

        Slider(
            value = progressFraction,
            onValueChange = { frac ->
                onSeek((frac * durationMs).toLong())
            },
            colors = SliderDefaults.colors(
                thumbColor = BrassGold,
                activeTrackColor = AmberGlow,
                inactiveTrackColor = Color(0xFF382B1E)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("progress_slider")
        )
    }
}

@Composable
fun VintageTransportControls(
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    isShuffle: Boolean,
    isGapless: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleGapless: () -> Unit,
    onOpenEqualizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(MahoganyMedium, MahoganyDark)
                )
            )
            .border(2.dp, BrassDark, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 14.dp)
    ) {
        // Upper row: Gapless LED & Equalizer quick access
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gapless Mode Toggle with Vintage LED
            Row(
                modifier = Modifier
                    .testTag("gapless_toggle")
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF19130F))
                    .border(1.dp, if (isGapless) AmberGlow else Color(0xFF453526), RoundedCornerShape(6.dp))
                    .clickable { onToggleGapless() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isGapless) LedGreen else Color(0xFF263327))
                        .border(1.dp, if (isGapless) Color(0xFF81C784) else Color(0xFF1E241E), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isGapless) "GAPLESS: ACTIVE" else "GAPLESS: OFF",
                    fontFamily = SpaceMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isGapless) BrassGoldLight else Color.Gray
                )
            }

            // Quick Equalizer button
            Row(
                modifier = Modifier
                    .testTag("eq_quick_button")
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF19130F))
                    .border(1.dp, BrassDark, RoundedCornerShape(6.dp))
                    .clickable { onOpenEqualizer() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Equalizer",
                    tint = BrassGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "EQ RACK",
                    fontFamily = SpaceMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = BrassGold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Mechanical Chrome Transport Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle
            MechanicalSquareButton(
                icon = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                isActive = isShuffle,
                testTag = "shuffle_button",
                onClick = onToggleShuffle
            )

            // Previous
            MechanicalRoundButton(
                icon = Icons.Default.SkipPrevious,
                contentDescription = "Previous Track",
                sizeDp = 48,
                testTag = "previous_button",
                onClick = onPrevious
            )

            // Play / Pause Master Button
            MechanicalMasterPlayButton(
                isPlaying = isPlaying,
                testTag = "play_pause_button",
                onClick = onPlayPause
            )

            // Next
            MechanicalRoundButton(
                icon = Icons.Default.SkipNext,
                contentDescription = "Next Track",
                sizeDp = 48,
                testTag = "next_button",
                onClick = onNext
            )

            // Repeat Mode
            val repeatIcon = when (repeatMode) {
                RepeatMode.OFF -> Icons.Default.Repeat
                RepeatMode.ALL -> Icons.Default.Repeat
                RepeatMode.ONE -> Icons.Default.RepeatOne
            }
            MechanicalSquareButton(
                icon = repeatIcon,
                contentDescription = "Repeat",
                isActive = repeatMode != RepeatMode.OFF,
                testTag = "repeat_button",
                onClick = onToggleRepeat
            )
        }
    }
}

@Composable
fun MechanicalMasterPlayButton(
    isPlaying: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .size(68.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(BrassGoldLight, BrassGold, BrassDark)
                )
            )
            .border(2.5.dp, Color(0xFFF7E9C4), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF261912), BakeliteBlack)
                    )
                )
                .border(1.dp, BrassDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = AmberGlow,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
fun MechanicalRoundButton(
    icon: ImageVector,
    contentDescription: String,
    sizeDp: Int,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF453629), Color(0xFF1F1711))
                )
            )
            .border(1.5.dp, BrassDark, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = CreamParchment,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun MechanicalSquareButton(
    icon: ImageVector,
    contentDescription: String,
    isActive: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) Color(0xFF3B2516) else Color(0xFF1E1712)
            )
            .border(
                1.5.dp,
                if (isActive) AmberGlow else BrassDark,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) AmberGlow else CreamMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
