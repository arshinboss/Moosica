package com.example.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.model.RepeatMode
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class MusicPlaybackController(
    private val context: Context,
    val equalizerManager: EqualizerManager
) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var exoPlayer: ExoPlayer? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs = _durationMs.asStateFlow()

    private val _playlist = MutableStateFlow<List<Track>>(emptyList())
    val playlist = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle = _isShuffle.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume = _volume.asStateFlow()

    private val _isGaplessEnabled = MutableStateFlow(true)
    val isGaplessEnabled = _isGaplessEnabled.asStateFlow()

    // Audio Visualizer waveform levels (for live animation)
    private val _visualizerAmplitudes = MutableStateFlow(List(32) { 0.2f })
    val visualizerAmplitudes = _visualizerAmplitudes.asStateFlow()

    private val _vuLevelLeft = MutableStateFlow(0.0f)
    val vuLevelLeft = _vuLevelLeft.asStateFlow()

    private val _vuLevelRight = MutableStateFlow(0.0f)
    val vuLevelRight = _vuLevelRight.asStateFlow()

    private var progressTrackingJob: Job? = null

    init {
        initPlayer()
    }

    private fun initPlayer() {
        val player = ExoPlayer.Builder(context)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ALL
                shuffleModeEnabled = false
                volume = _volume.value

                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                        if (playing) {
                            startProgressTracking()
                        } else {
                            stopProgressTracking()
                            _vuLevelLeft.value = 0f
                            _vuLevelRight.value = 0f
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        val index = currentMediaItemIndex
                        _currentIndex.value = index
                        val tracks = _playlist.value
                        if (index in tracks.indices) {
                            _currentTrack.value = tracks[index]
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            _durationMs.value = duration.coerceAtLeast(0L)
                            equalizerManager.attachAudioSession(audioSessionId)
                        } else if (playbackState == Player.STATE_ENDED) {
                            if (_repeatMode.value == RepeatMode.OFF && currentMediaItemIndex == _playlist.value.size - 1) {
                                _isPlaying.value = false
                            }
                        }
                    }
                })
            }
        exoPlayer = player
    }

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        _playlist.value = tracks
        _currentIndex.value = startIndex.coerceIn(0, tracks.size - 1)
        _currentTrack.value = tracks[_currentIndex.value]

        val mediaItems = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.contentUri)
                .setMediaId(track.id.toString())
                .build()
        }

        exoPlayer?.apply {
            setMediaItems(mediaItems, _currentIndex.value, 0L)
            prepare()
            play()
        }
    }

    fun playTrack(track: Track) {
        val list = _playlist.value
        val index = list.indexOfFirst { it.id == track.id }
        if (index != -1) {
            exoPlayer?.seekTo(index, 0L)
            exoPlayer?.play()
        } else {
            val newList = listOf(track) + list
            setPlaylist(newList, 0)
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                if (player.playbackState == Player.STATE_IDLE) {
                    player.prepare()
                }
                player.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0.0f, 1.0f)
        _volume.value = clamped
        exoPlayer?.volume = clamped
    }

    fun skipToNext() {
        exoPlayer?.let { player ->
            if (player.hasNextMediaItem()) {
                player.seekToNextMediaItem()
                player.play()
            } else if (_repeatMode.value == RepeatMode.ALL && _playlist.value.isNotEmpty()) {
                player.seekTo(0, 0L)
                player.play()
            }
        }
    }

    fun skipToPrevious() {
        exoPlayer?.let { player ->
            if (player.currentPosition > 3000) {
                player.seekTo(0L)
            } else if (player.hasPreviousMediaItem()) {
                player.seekToPreviousMediaItem()
                player.play()
            } else {
                player.seekTo(0L)
            }
        }
    }

    fun toggleRepeatMode() {
        val next = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _repeatMode.value = next
        exoPlayer?.repeatMode = when (next) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
    }

    fun toggleShuffle() {
        val newShuffle = !_isShuffle.value
        _isShuffle.value = newShuffle
        exoPlayer?.shuffleModeEnabled = newShuffle
    }

    fun toggleGapless() {
        _isGaplessEnabled.value = !_isGaplessEnabled.value
    }

    private fun startProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = scope.launch {
            var phase = 0.0
            while (isActive) {
                exoPlayer?.let { player ->
                    _currentPositionMs.value = player.currentPosition
                    if (player.duration > 0) {
                        _durationMs.value = player.duration
                    }

                    if (player.isPlaying) {
                        phase += 0.25
                        val base = 0.55f + 0.35f * sin(phase).toFloat()
                        val noiseL = (Random.nextFloat() - 0.5f) * 0.2f
                        val noiseR = (Random.nextFloat() - 0.5f) * 0.2f
                        _vuLevelLeft.value = (base + noiseL).coerceIn(0.1f, 0.98f)
                        _vuLevelRight.value = (base + noiseR).coerceIn(0.1f, 0.98f)

                        // Generate wave visualizer amplitudes
                        _visualizerAmplitudes.value = (0 until 32).map { i ->
                            val freq = 0.15 * (i + 1)
                            val amp = 0.3f + 0.5f * sin(phase + freq).toFloat().coerceIn(0f, 1f)
                            (amp + (Random.nextFloat() * 0.2f)).coerceIn(0.1f, 1.0f)
                        }
                    } else {
                        _vuLevelLeft.value = 0f
                        _vuLevelRight.value = 0f
                    }
                }
                delay(60)
            }
        }
    }

    private fun stopProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = null
    }

    fun release() {
        stopProgressTracking()
        equalizerManager.release()
        exoPlayer?.release()
        exoPlayer = null
    }
}
