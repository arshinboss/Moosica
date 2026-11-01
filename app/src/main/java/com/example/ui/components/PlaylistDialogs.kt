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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.theme.DuskHeartPink
import com.example.ui.theme.DuskMagenta
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.DuskPurpleMid
import com.example.ui.theme.DuskSunsetOrange
import com.example.ui.theme.TextLavenderMuted
import com.example.ui.theme.TextPureWhite

@Composable
fun AddToPlaylistDialog(
    track: Track,
    playlists: List<Playlist>,
    onAddToPlaylist: (playlistId: String) -> Unit,
    onCreateNewPlaylistClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DuskPurpleMid.copy(alpha = 0.95f),
                            DuskNightDeep.copy(alpha = 0.98f)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ADD TO PLAYLIST",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuskSunsetOrange,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = track.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite,
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

                Spacer(modifier = Modifier.height(14.dp))

                // Create New Playlist quick button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .clickable {
                            onCreateNewPlaylistClick()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("create_new_playlist_quick_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DuskSunsetOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Create New Playlist",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPureWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (playlists.isEmpty()) {
                    Text(
                        text = "No custom playlists yet. Create your first playlist above!",
                        fontSize = 13.sp,
                        color = TextLavenderMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Text(
                        text = "Choose playlist:",
                        fontSize = 12.sp,
                        color = TextLavenderMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(playlists) { playlist ->
                            val isAlreadyIn = playlist.trackIds.contains(track.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isAlreadyIn) Color.White.copy(alpha = 0.18f)
                                        else Color.White.copy(alpha = 0.08f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isAlreadyIn) DuskHeartPink else Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onAddToPlaylist(playlist.id)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                        contentDescription = "Playlist",
                                        tint = if (isAlreadyIn) DuskHeartPink else Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = playlist.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPureWhite
                                        )
                                        Text(
                                            text = "${playlist.trackIds.size} songs",
                                            fontSize = 11.sp,
                                            color = TextLavenderMuted
                                        )
                                    }
                                }

                                if (isAlreadyIn) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Added",
                                            tint = DuskHeartPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Added",
                                            fontSize = 11.sp,
                                            color = DuskHeartPink,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    initialTrackId: Long? = null,
    onCreate: (name: String, desc: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DuskPurpleMid.copy(alpha = 0.98f),
                            DuskNightDeep
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "NEW PLAYLIST",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DuskSunsetOrange,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Name your playlist",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPureWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Midnight Chill, Roadtrip Vibes", color = TextLavenderMuted.copy(alpha = 0.6f)) },
                    label = { Text("Playlist Title", color = TextLavenderMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        focusedBorderColor = DuskSunsetOrange,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    placeholder = { Text("Optional description", color = TextLavenderMuted.copy(alpha = 0.6f)) },
                    label = { Text("Description", color = TextLavenderMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        focusedBorderColor = DuskSunsetOrange,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_desc_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextLavenderMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onCreate(name, desc)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DuskSunsetOrange,
                            contentColor = Color.White
                        ),
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("confirm_create_playlist_button")
                    ) {
                        Text("Create Playlist")
                    }
                }
            }
        }
    }
}
