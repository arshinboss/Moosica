package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RepeatMode
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.MusicCoverArt
import com.example.ui.components.TrackOptionsDialog
import com.example.ui.theme.DuskHeartPink
import com.example.ui.theme.DuskMagenta
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.DuskPurpleDark
import com.example.ui.theme.DuskPurpleGlow
import com.example.ui.theme.DuskPurpleMid
import com.example.ui.theme.DuskSunsetGold
import com.example.ui.theme.DuskSunsetOrange
import com.example.ui.theme.DuskSunsetRose
import com.example.ui.theme.TextLavenderMuted
import com.example.ui.theme.TextPureWhite
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val currentTrack by viewModel.playbackController.currentTrack.collectAsState()
    val isPlaying by viewModel.playbackController.isPlaying.collectAsState()
    val positionMs by viewModel.playbackController.currentPositionMs.collectAsState()
    val durationMs by viewModel.playbackController.durationMs.collectAsState()
    val repeatMode by viewModel.playbackController.repeatMode.collectAsState()
    val isShuffle by viewModel.playbackController.isShuffle.collectAsState()
    val volume by viewModel.playbackController.volume.collectAsState()
    val isVisualizerVisible by viewModel.isVisualizerVisible.collectAsState()
    val visualizerAmplitudes by viewModel.playbackController.visualizerAmplitudes.collectAsState()

    val playlists by viewModel.playlists.collectAsState()
    val trackForPlaylistDialog by viewModel.trackForPlaylistDialog.collectAsState()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()

    var showOptionsDialog by remember { mutableStateOf(false) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var draggedPosition by remember { mutableFloatStateOf(0f) }

    val effectivePosition = if (isDraggingSlider) {
        draggedPosition.toLong()
    } else {
        positionMs
    }

    val maxSliderValue = if (durationMs > 0) durationMs.toFloat() else 1f

    val scrollState = rememberScrollState()

    // Full-screen atmospheric dusk/twilight sunset background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        DuskNightDeep,
                        DuskPurpleDark,
                        DuskPurpleMid,
                        DuskPurpleGlow,
                        DuskMagenta,
                        DuskSunsetRose,
                        DuskSunsetOrange,
                        DuskSunsetGold,
                        DuskPurpleDark,
                        DuskNightDeep
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1080f, 2200f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Frosted Glass Main Card Container (matching reference image)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.08f),
                            Color(0xFF281C3D).copy(alpha = 0.40f)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(32.dp))
                .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = DuskPurpleGlow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row: Chevron Down, "Now Playing", "..." options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.LIBRARY) },
                        modifier = Modifier.testTag("nav_library_chevron")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize / Library",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "Now Playing",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPureWhite,
                        letterSpacing = 0.5.sp
                    )

                    IconButton(
                        onClick = { showOptionsDialog = true },
                        modifier = Modifier.testTag("track_more_options")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More Options",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cover Artwork Card with floating heart button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 330.dp)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    MusicCoverArt(
                        track = currentTrack,
                        isFavorite = currentTrack?.isFavorite == true,
                        onToggleFavorite = {
                            currentTrack?.let { viewModel.toggleFavorite(it.id) }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Song Title & Artist
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentTrack?.title ?: "No Track Selected",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentTrack?.artist ?: "Unknown Artist",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextLavenderMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Audio Visualizer Spectrum (optional expandable display)
                AnimatedVisibility(
                    visible = isVisualizerVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.20f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        visualizerAmplitudes.forEach { amp ->
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height((30 * amp).dp.coerceAtLeast(4.dp))
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(DuskSunsetOrange, DuskHeartPink)
                                        )
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Scrubber: Progress Slider & Timestamps
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = effectivePosition.toFloat().coerceIn(0f, maxSliderValue),
                        onValueChange = {
                            isDraggingSlider = true
                            draggedPosition = it
                        },
                        onValueChangeFinished = {
                            viewModel.playbackController.seekTo(draggedPosition.toLong())
                            isDraggingSlider = false
                        },
                        valueRange = 0f..maxSliderValue,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .testTag("scrubber_slider")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(effectivePosition),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = formatTime(durationMs),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Transport Controls: Shuffle, Previous, Play/Pause, Next, Repeat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    IconButton(
                        onClick = { viewModel.playbackController.toggleShuffle() },
                        modifier = Modifier.testTag("shuffle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) DuskSunsetOrange else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Previous Track
                    IconButton(
                        onClick = { viewModel.playbackController.skipToPrevious() },
                        modifier = Modifier.testTag("previous_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Main Center Play / Pause Button with Frosted Glow
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.50f), CircleShape)
                            .shadow(16.dp, CircleShape, spotColor = Color.White.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { viewModel.playbackController.togglePlayPause() },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Next Track
                    IconButton(
                        onClick = { viewModel.playbackController.skipToNext() },
                        modifier = Modifier.testTag("next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Repeat Mode
                    IconButton(
                        onClick = { viewModel.playbackController.toggleRepeatMode() },
                        modifier = Modifier.testTag("repeat_button")
                    ) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repeat Mode",
                            tint = if (repeatMode != RepeatMode.OFF) DuskSunsetOrange else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Quick Action Pill Bar (Queue/Playlist, Equalizer, Waveform, Settings)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(30.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Playlists / Queue
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.LIBRARY) },
                            modifier = Modifier.testTag("pill_playlists_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = "Playlists & Library",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Equalizer
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.EQUALIZER) },
                            modifier = Modifier.testTag("pill_equalizer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = "Equalizer",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Waveform Visualizer
                        IconButton(
                            onClick = { viewModel.toggleVisualizer() },
                            modifier = Modifier.testTag("pill_visualizer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Live Audio Waveform",
                                tint = if (isVisualizerVisible) DuskSunsetOrange else Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Settings / More
                        IconButton(
                            onClick = { showOptionsDialog = true },
                            modifier = Modifier.testTag("pill_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Track Settings",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Volume Slider Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (volume <= 0.05f) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeDown,
                        contentDescription = "Volume Down",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )

                    Slider(
                        value = volume,
                        onValueChange = { viewModel.playbackController.setVolume(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(24.dp)
                            .testTag("volume_slider")
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Volume Up",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

    // Options Dialog
    if (showOptionsDialog) {
        TrackOptionsDialog(
            track = currentTrack,
            onAddToPlaylist = {
                currentTrack?.let { viewModel.openAddToPlaylistDialog(it) }
            },
            onOpenEqualizer = {
                viewModel.navigateTo(AppScreen.EQUALIZER)
            },
            onToggleVisualizer = {
                viewModel.toggleVisualizer()
            },
            isVisualizerActive = isVisualizerVisible,
            onDismiss = { showOptionsDialog = false }
        )
    }

    // Add To Playlist Dialog
    trackForPlaylistDialog?.let { track ->
        AddToPlaylistDialog(
            track = track,
            playlists = playlists,
            onAddToPlaylist = { playlistId ->
                viewModel.addTrackToPlaylist(playlistId, track.id)
                viewModel.closeAddToPlaylistDialog()
            },
            onCreateNewPlaylistClick = {
                viewModel.closeAddToPlaylistDialog()
                viewModel.openCreatePlaylistDialog()
            },
            onDismiss = { viewModel.closeAddToPlaylistDialog() }
        )
    }

    // Create Playlist Dialog
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            initialTrackId = currentTrack?.id,
            onCreate = { name, desc ->
                viewModel.createPlaylist(name, desc, currentTrack?.id)
            },
            onDismiss = { viewModel.closeCreatePlaylistDialog() }
        )
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
