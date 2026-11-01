package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.theme.DuskHeartPink
import com.example.ui.theme.DuskMagenta
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.DuskPurpleDark
import com.example.ui.theme.DuskPurpleMid
import com.example.ui.theme.DuskSunsetOrange
import com.example.ui.theme.DuskSunsetRose
import com.example.ui.theme.TextLavenderMuted
import com.example.ui.theme.TextPureWhite
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicPlayerViewModel

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val allTracks by viewModel.allTracks.collectAsState()
    val currentTrack by viewModel.playbackController.currentTrack.collectAsState()
    val isPlaying by viewModel.playbackController.isPlaying.collectAsState()

    val playlistTracks = allTracks.filter { playlist.trackIds.contains(it.id) }
    val totalDurationMs = playlistTracks.sumOf { it.durationMs }
    val totalMinutes = (totalDurationMs / 1000) / 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DuskPurpleDark,
                        DuskNightDeep
                    )
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.LIBRARY) },
                modifier = Modifier.testTag("playlist_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { viewModel.deletePlaylist(playlist.id) },
                modifier = Modifier.testTag("delete_playlist_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Playlist",
                    tint = DuskSunsetOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Playlist Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            DuskMagenta.copy(alpha = 0.5f),
                            DuskSunsetRose.copy(alpha = 0.3f),
                            DuskPurpleMid.copy(alpha = 0.5f)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Playlist Icon",
                            tint = DuskSunsetOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = playlist.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite
                        )
                        if (playlist.description.isNotBlank()) {
                            Text(
                                text = playlist.description,
                                fontSize = 12.sp,
                                color = TextLavenderMuted
                            )
                        }
                        Text(
                            text = "${playlistTracks.size} tracks • $totalMinutes mins",
                            fontSize = 11.sp,
                            color = DuskSunsetOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.playPlaylist(playlist) },
                    enabled = playlistTracks.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DuskSunsetOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("play_playlist_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play All",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY ALL TRACKS",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tracks in Playlist
        if (playlistTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Empty",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This playlist has no songs yet",
                        fontSize = 14.sp,
                        color = TextLavenderMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Go to Library or Now Playing to add songs",
                        fontSize = 12.sp,
                        color = TextLavenderMuted.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlistTracks) { track ->
                    val isCurrent = currentTrack?.id == track.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isCurrent) Color.White.copy(alpha = 0.18f)
                                else Color.White.copy(alpha = 0.08f)
                            )
                            .border(
                                1.dp,
                                if (isCurrent) DuskHeartPink else Color.White.copy(alpha = 0.12f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.playbackController.playTrack(track)
                                viewModel.navigateTo(AppScreen.PLAYER)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isCurrent && isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                contentDescription = "Note",
                                tint = if (isCurrent) DuskHeartPink else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCurrent) DuskHeartPink else TextPureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                fontSize = 11.sp,
                                color = TextLavenderMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = track.formattedDuration,
                            fontSize = 12.sp,
                            color = TextLavenderMuted,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Remove from playlist
                        IconButton(
                            onClick = { viewModel.removeTrackFromPlaylist(playlist.id, track.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
