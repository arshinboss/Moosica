package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioSynthesizer
import com.example.audio.EqBand
import com.example.audio.EqPreset
import com.example.audio.EqualizerManager
import com.example.audio.LocalMediaScanner
import com.example.audio.MusicPlaybackController
import com.example.data.PlaylistRepository
import com.example.model.Playlist
import com.example.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    PLAYER,
    LIBRARY,
    EQUALIZER,
    PLAYLISTS,
    PLAYLIST_DETAIL
}

enum class LibraryTab {
    ALL_SONGS,
    FAVORITES,
    PLAYLISTS,
    DEMO_SHOWCASE
}

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {

    val equalizerManager = EqualizerManager()
    val playbackController = MusicPlaybackController(application, equalizerManager)
    val playlistRepository = PlaylistRepository(application)

    private val _currentScreen = MutableStateFlow(AppScreen.PLAYER)
    val currentScreen = _currentScreen.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist = _selectedPlaylist.asStateFlow()

    private val _rawTracks = MutableStateFlow<List<Track>>(emptyList())
    private val _demoTracks = MutableStateFlow<List<Track>>(emptyList())
    val demoTracks = _demoTracks.asStateFlow()

    val playlists: StateFlow<List<Playlist>> = playlistRepository.playlists
    val favoriteTrackIds: StateFlow<Set<Long>> = playlistRepository.favoriteTrackIds

    // Combine raw tracks with favorites set to produce current tracks with isFavorite flag updated
    val allTracks: StateFlow<List<Track>> = combine(_rawTracks, favoriteTrackIds) { tracks, favs ->
        tracks.map { track ->
            track.copy(isFavorite = favs.contains(track.id))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _libraryTab = MutableStateFlow(LibraryTab.ALL_SONGS)
    val libraryTab = _libraryTab.asStateFlow()

    private val _isLoadingTracks = MutableStateFlow(false)
    val isLoadingTracks = _isLoadingTracks.asStateFlow()

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission = _hasPermission.asStateFlow()

    private val _isVisualizerVisible = MutableStateFlow(false)
    val isVisualizerVisible = _isVisualizerVisible.asStateFlow()

    // Add to playlist modal state: holds track to add or null when closed
    private val _trackForPlaylistDialog = MutableStateFlow<Track?>(null)
    val trackForPlaylistDialog = _trackForPlaylistDialog.asStateFlow()

    // Create playlist dialog state
    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog = _showCreatePlaylistDialog.asStateFlow()

    // Equalizer UI states
    private val _eqBands = MutableStateFlow<List<EqBand>>(emptyList())
    val eqBands = _eqBands.asStateFlow()

    private val _activePresetName = MutableStateFlow("Warm Vinyl")
    val activePresetName = _activePresetName.asStateFlow()

    private val _allPresets = MutableStateFlow<List<EqPreset>>(equalizerManager.builtInPresets)
    val allPresets = _allPresets.asStateFlow()

    private val _isEqEnabled = MutableStateFlow(equalizerManager.isEnabled)
    val isEqEnabled = _isEqEnabled.asStateFlow()

    private val _bassBoostLevel = MutableStateFlow<Short>(350)
    val bassBoostLevel = _bassBoostLevel.asStateFlow()

    init {
        checkPermissionAndLoadTracks()
        loadEqualizerState()
    }

    fun checkPermissionAndLoadTracks() {
        val granted = LocalMediaScanner.hasAudioPermission(getApplication())
        _hasPermission.value = granted
        refreshTracks()
    }

    fun onPermissionResult(granted: Boolean) {
        _hasPermission.value = granted
        refreshTracks()
    }

    fun refreshTracks() {
        viewModelScope.launch {
            _isLoadingTracks.value = true
            val demos = AudioSynthesizer.getOrGenerateDemoTracks(getApplication())
            _demoTracks.value = demos

            val localTracks = if (_hasPermission.value) {
                LocalMediaScanner.queryDeviceAudio(getApplication())
            } else {
                emptyList()
            }

            // Put Dreamscape and demos first if local tracks is empty or combine them
            val combined = if (localTracks.isNotEmpty()) {
                demos + localTracks
            } else {
                demos
            }
            _rawTracks.value = combined

            // If nothing is playing yet, initialize playlist with the tracks starting with Dreamscape
            if (playbackController.currentTrack.value == null && combined.isNotEmpty()) {
                playbackController.setPlaylist(combined, 0)
            }
            _isLoadingTracks.value = false
        }
    }

    fun addCustomUris(uris: List<Uri>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val parsedTracks = uris.map { uri ->
                LocalMediaScanner.parseTrackFromUri(context, uri)
            }
            _rawTracks.value = parsedTracks + _rawTracks.value
            playbackController.setPlaylist(_rawTracks.value, 0)
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openPlaylist(playlist: Playlist) {
        _selectedPlaylist.value = playlist
        _currentScreen.value = AppScreen.PLAYLIST_DETAIL
    }

    fun playPlaylist(playlist: Playlist) {
        val tracks = allTracks.value.filter { playlist.trackIds.contains(it.id) }
        if (tracks.isNotEmpty()) {
            playbackController.setPlaylist(tracks, 0)
            _currentScreen.value = AppScreen.PLAYER
        }
    }

    fun toggleVisualizer() {
        _isVisualizerVisible.value = !_isVisualizerVisible.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLibraryTab(tab: LibraryTab) {
        _libraryTab.value = tab
    }

    // Playlist actions
    fun openAddToPlaylistDialog(track: Track) {
        _trackForPlaylistDialog.value = track
    }

    fun closeAddToPlaylistDialog() {
        _trackForPlaylistDialog.value = null
    }

    fun openCreatePlaylistDialog() {
        _showCreatePlaylistDialog.value = true
    }

    fun closeCreatePlaylistDialog() {
        _showCreatePlaylistDialog.value = false
    }

    fun createPlaylist(name: String, description: String = "", initialTrackId: Long? = null) {
        viewModelScope.launch {
            val playlist = playlistRepository.createPlaylist(name, description)
            if (initialTrackId != null) {
                playlistRepository.addTrackToPlaylist(playlist.id, initialTrackId)
            }
            closeCreatePlaylistDialog()
        }
    }

    fun addTrackToPlaylist(playlistId: String, trackId: Long) {
        viewModelScope.launch {
            playlistRepository.addTrackToPlaylist(playlistId, trackId)
            // Update selected playlist if currently open
            if (_selectedPlaylist.value?.id == playlistId) {
                _selectedPlaylist.value = playlists.value.firstOrNull { it.id == playlistId }
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: Long) {
        viewModelScope.launch {
            playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
            if (_selectedPlaylist.value?.id == playlistId) {
                _selectedPlaylist.value = playlists.value.firstOrNull { it.id == playlistId }
            }
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
            if (_selectedPlaylist.value?.id == playlistId) {
                _selectedPlaylist.value = null
                _currentScreen.value = AppScreen.LIBRARY
            }
        }
    }

    fun toggleFavorite(trackId: Long) {
        viewModelScope.launch {
            playlistRepository.toggleFavorite(trackId)
        }
    }

    // Equalizer controls
    private fun loadEqualizerState() {
        _eqBands.value = equalizerManager.getBands()
        _activePresetName.value = equalizerManager.getActivePresetName()
        _isEqEnabled.value = equalizerManager.isEnabled
        _bassBoostLevel.value = equalizerManager.getBassBoost()
    }

    fun setEqEnabled(enabled: Boolean) {
        equalizerManager.setEnabled(enabled)
        _isEqEnabled.value = enabled
    }

    fun applyPreset(preset: EqPreset) {
        equalizerManager.applyPreset(preset)
        _activePresetName.value = preset.name
        _eqBands.value = equalizerManager.getBands()
        _bassBoostLevel.value = equalizerManager.getBassBoost()
    }

    fun setBandLevel(bandIndex: Int, levelMb: Short) {
        equalizerManager.setBandLevel(bandIndex, levelMb)
        _activePresetName.value = equalizerManager.getActivePresetName()
        _eqBands.value = equalizerManager.getBands()
    }

    fun setBassBoost(strength: Short) {
        equalizerManager.setBassBoost(strength)
        _bassBoostLevel.value = strength
    }

    fun saveCustomPreset(presetName: String) {
        val bands = _eqBands.value
        val levels = bands.map { it.currentLevelMb }
        val newPreset = EqPreset(
            name = presetName.ifBlank { "Custom Preset" },
            bandLevelsMb = levels,
            bassBoostStrength = _bassBoostLevel.value,
            isCustom = true
        )
        val updated = _allPresets.value.filter { it.name != newPreset.name } + newPreset
        _allPresets.value = updated
        applyPreset(newPreset)
    }

    override fun onCleared() {
        super.onCleared()
        playbackController.release()
    }
}
