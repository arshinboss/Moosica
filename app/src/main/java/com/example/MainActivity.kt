package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.EqualizerScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicPlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MusicAppRoot()
            }
        }
    }
}

@Composable
fun MusicAppRoot(
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()

    // Handle back button for sub-screens
    when (currentScreen) {
        AppScreen.PLAYLIST_DETAIL -> {
            BackHandler {
                viewModel.navigateTo(AppScreen.LIBRARY)
            }
        }
        AppScreen.LIBRARY, AppScreen.EQUALIZER, AppScreen.PLAYLISTS -> {
            BackHandler {
                viewModel.navigateTo(AppScreen.PLAYER)
            }
        }
        AppScreen.PLAYER -> {
            // Default system back behavior
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DuskNightDeep)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            when (currentScreen) {
                AppScreen.PLAYER -> PlayerScreen(viewModel = viewModel)
                AppScreen.LIBRARY, AppScreen.PLAYLISTS -> LibraryScreen(viewModel = viewModel)
                AppScreen.EQUALIZER -> EqualizerScreen(viewModel = viewModel)
                AppScreen.PLAYLIST_DETAIL -> {
                    selectedPlaylist?.let { playlist ->
                        PlaylistDetailScreen(playlist = playlist, viewModel = viewModel)
                    } ?: LibraryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
