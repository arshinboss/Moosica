package com.example.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.CreatePlaylistDialog
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
import com.example.viewmodel.LibraryTab
import com.example.viewmodel.MusicPlayerViewModel

@Composable
fun LibraryScreen(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val allTracks by viewModel.allTracks.collectAsState()
    val demoTracks by viewModel.demoTracks.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentTab by viewModel.libraryTab.collectAsState()
    val currentTrack by viewModel.playbackController.currentTrack.collectAsState()
    val isPlaying by viewModel.playbackController.isPlaying.collectAsState()
    val hasPermission by viewModel.hasPermission.collectAsState()
    val isLoading by viewModel.isLoadingTracks.collectAsState()

    val trackForPlaylistDialog by viewModel.trackForPlaylistDialog.collectAsState()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()

    // Permission launcher for scanning device audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    // Document file picker launcher for selecting local audio files directly (SAF)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addCustomUris(uris)
        }
    }

    val audioPermissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Filter tracks based on tab and search
    val filteredTracks = allTracks.filter { track ->
        val query = searchQuery.trim().lowercase()
        val matchesQuery = query.isEmpty() ||
            track.title.lowercase().contains(query) ||
            track.artist.lowercase().contains(query) ||
            track.album.lowercase().contains(query)

        val matchesTab = when (currentTab) {
            LibraryTab.ALL_SONGS -> true
            LibraryTab.FAVORITES -> track.isFavorite
            LibraryTab.PLAYLISTS -> true
            LibraryTab.DEMO_SHOWCASE -> track.isDemo
        }

        matchesQuery && matchesTab
    }

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

        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.PLAYER) },
                    modifier = Modifier.testTag("library_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Player",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "AUDIO LIBRARY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuskSunsetOrange,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Music & Playlists",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Open SAF File Picker to pick any music file from system
                IconButton(
                    onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                    modifier = Modifier.testTag("import_audio_files_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileOpen,
                        contentDescription = "Import Audio Files",
                        tint = DuskSunsetOrange
                    )
                }

                // Rescan device audio button
                IconButton(
                    onClick = { viewModel.refreshTracks() },
                    modifier = Modifier.testTag("rescan_library_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rescan Audio",
                        tint = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextLavenderMuted
                )
            },
            placeholder = {
                Text(
                    "Search songs, artists, albums...",
                    fontSize = 13.sp,
                    color = TextLavenderMuted.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DuskSunsetOrange,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = TextPureWhite,
                unfocusedTextColor = TextPureWhite,
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("library_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Device Audio Permission Request Banner (if permission not yet granted)
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, DuskSunsetOrange.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Storage Access",
                            tint = DuskSunsetOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACCESS SYSTEM AUDIO FILES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = DuskSunsetOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Grant storage permission to automatically discover all music tracks saved on this device.",
                        fontSize = 12.sp,
                        color = TextLavenderMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { permissionLauncher.launch(audioPermissionToRequest) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DuskSunsetOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("grant_permission_button")
                    ) {
                        Text(
                            "SCAN SYSTEM MUSIC",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Library Navigation Tabs: ALL SONGS, PLAYLISTS, FAVORITES, DEMO VAULT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LibraryTabPill(
                label = "ALL SONGS",
                isSelected = currentTab == LibraryTab.ALL_SONGS,
                onClick = { viewModel.setLibraryTab(LibraryTab.ALL_SONGS) },
                modifier = Modifier.weight(1f)
            )
            LibraryTabPill(
                label = "PLAYLISTS",
                isSelected = currentTab == LibraryTab.PLAYLISTS,
                onClick = { viewModel.setLibraryTab(LibraryTab.PLAYLISTS) },
                modifier = Modifier.weight(1f)
            )
            LibraryTabPill(
                label = "FAVORITES",
                isSelected = currentTab == LibraryTab.FAVORITES,
                onClick = { viewModel.setLibraryTab(LibraryTab.FAVORITES) },
                modifier = Modifier.weight(1f)
            )
            LibraryTabPill(
                label = "DEMOS",
                isSelected = currentTab == LibraryTab.DEMO_SHOWCASE,
                onClick = { viewModel.setLibraryTab(LibraryTab.DEMO_SHOWCASE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area based on tab
        if (currentTab == LibraryTab.PLAYLISTS) {
            // Playlists view with "+ Add Playlist" button
            PlaylistsTabView(
                playlists = playlists,
                onSelectPlaylist = { viewModel.openPlaylist(it) },
                onPlayPlaylist = { viewModel.playPlaylist(it) },
                onCreatePlaylistClick = { viewModel.openCreatePlaylistDialog() }
            )
        } else {
            // Track list view
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DuskSunsetOrange)
                }
            } else if (filteredTracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Empty",
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (currentTab == LibraryTab.FAVORITES) "No favorite tracks yet" else "No audio tracks found",
                            fontSize = 14.sp,
                            color = TextLavenderMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap Import to select music files from storage",
                            fontSize = 12.sp,
                            color = TextLavenderMuted.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTracks) { track ->
                        val isCurrent = currentTrack?.id == track.id
                        LibraryTrackRow(
                            track = track,
                            isCurrent = isCurrent,
                            isPlaying = isPlaying,
                            onPlay = {
                                viewModel.playbackController.playTrack(track)
                                viewModel.navigateTo(AppScreen.PLAYER)
                            },
                            onToggleFavorite = {
                                viewModel.toggleFavorite(track.id)
                            },
                            onAddToPlaylist = {
                                viewModel.openAddToPlaylistDialog(track)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
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
            onCreate = { name, desc ->
                viewModel.createPlaylist(name, desc)
            },
            onDismiss = { viewModel.closeCreatePlaylistDialog() }
        )
    }
}

@Composable
fun PlaylistsTabView(
    playlists: List<Playlist>,
    onSelectPlaylist: (Playlist) -> Unit,
    onPlayPlaylist: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // "+ Add Playlist" Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.2.dp, DuskSunsetOrange.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .clickable(onClick = onCreatePlaylistClick)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("add_playlist_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DuskSunsetOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Playlist",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Add New Playlist",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPureWhite
                )
                Text(
                    text = "Create custom playlist for your songs",
                    fontSize = 12.sp,
                    color = TextLavenderMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (playlists.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No playlists created yet",
                    fontSize = 14.sp,
                    color = TextLavenderMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlists) { playlist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .clickable { onSelectPlaylist(playlist) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("playlist_item_${playlist.id}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(DuskMagenta, DuskPurpleMid)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                    contentDescription = "Playlist",
                                    tint = DuskSunsetOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = playlist.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPureWhite
                                )
                                Text(
                                    text = "${playlist.trackIds.size} songs",
                                    fontSize = 12.sp,
                                    color = TextLavenderMuted
                                )
                            }
                        }

                        // Play Button
                        IconButton(
                            onClick = { onPlayPlaylist(playlist) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Playlist",
                                tint = DuskSunsetOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun LibraryTrackRow(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isCurrent) Color.White.copy(alpha = 0.16f)
                else Color.White.copy(alpha = 0.07f)
            )
            .border(
                1.dp,
                if (isCurrent) DuskHeartPink else Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onPlay)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("track_${track.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail with fallback Music Icon Logo
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.3f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (track.coverDrawableRes != null) {
                Image(
                    painter = painterResource(id = track.coverDrawableRes),
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (track.embeddedPictureBytes != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(track.embeddedPictureBytes)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        SmallMusicLogo(isCurrent = isCurrent, isPlaying = isPlaying)
                    }
                )
            } else if (track.albumArtUri != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(track.albumArtUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = {
                        SmallMusicLogo(isCurrent = isCurrent, isPlaying = isPlaying)
                    }
                )
            } else {
                // If music file does not have thumbnail show a music icon logo
                SmallMusicLogo(isCurrent = isCurrent, isPlaying = isPlaying)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Artist
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCurrent) DuskSunsetOrange else TextPureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artist} • ${track.album}",
                fontSize = 11.sp,
                color = TextLavenderMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duration
        Text(
            text = track.formattedDuration,
            fontSize = 11.sp,
            color = TextLavenderMuted,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Favorite Toggle
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (track.isFavorite) DuskHeartPink else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }

        // Add to Playlist
        IconButton(
            onClick = onAddToPlaylist,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = "Add to Playlist",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SmallMusicLogo(isCurrent: Boolean, isPlaying: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(DuskMagenta, DuskPurpleDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isCurrent && isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
            contentDescription = "Music Logo",
            tint = if (isCurrent) DuskSunsetOrange else Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun LibraryTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) DuskSunsetOrange else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextLavenderMuted
        )
    }
}
