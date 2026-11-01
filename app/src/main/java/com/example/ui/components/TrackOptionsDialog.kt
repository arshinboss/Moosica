package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Track
import com.example.ui.theme.DuskHeartPink
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.DuskPurpleMid
import com.example.ui.theme.DuskSunsetOrange
import com.example.ui.theme.TextLavenderMuted
import com.example.ui.theme.TextPureWhite

@Composable
fun TrackOptionsDialog(
    track: Track?,
    onAddToPlaylist: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onToggleVisualizer: () -> Unit,
    isVisualizerActive: Boolean,
    onDismiss: () -> Unit
) {
    if (track == null) return

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DuskPurpleMid.copy(alpha = 0.98f),
                            DuskNightDeep.copy(alpha = 0.98f)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NOW PLAYING OPTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuskSunsetOrange,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = track.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite,
                            maxLines = 1
                        )
                        Text(
                            text = track.artist,
                            fontSize = 13.sp,
                            color = TextLavenderMuted,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OptionsItem(
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    title = "Add to Playlist",
                    subtitle = "Save to your custom playlists",
                    onClick = {
                        onDismiss()
                        onAddToPlaylist()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OptionsItem(
                    icon = Icons.Default.Equalizer,
                    title = "Equalizer & Sound Effects",
                    subtitle = "Warm Vinyl, Tube Amp, and Bass Boost",
                    onClick = {
                        onDismiss()
                        onOpenEqualizer()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OptionsItem(
                    icon = Icons.Default.GraphicEq,
                    title = if (isVisualizerActive) "Hide Live Audio Spectrum" else "Show Live Audio Spectrum",
                    subtitle = "Animated frequency visualization",
                    onClick = {
                        onDismiss()
                        onToggleVisualizer()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // File specs
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Specs",
                                tint = DuskSunsetOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUDIO FILE SPECIFICATIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPureWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Format: ${if (track.isDemo) "WAV (Synthesized 44.1kHz)" else "Local Audio Stream"}",
                            fontSize = 11.sp,
                            color = TextLavenderMuted
                        )
                        Text(
                            text = "Duration: ${track.formattedDuration}",
                            fontSize = 11.sp,
                            color = TextLavenderMuted
                        )
                        if (track.path.isNotBlank()) {
                            Text(
                                text = "Source: ${track.path.takeLast(40)}",
                                fontSize = 10.sp,
                                color = TextLavenderMuted.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TextPureWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPureWhite
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextLavenderMuted
            )
        }
    }
}
