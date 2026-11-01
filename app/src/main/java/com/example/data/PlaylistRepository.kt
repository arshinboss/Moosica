package com.example.data

import android.content.Context
import com.example.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class PlaylistRepository(private val context: Context) {

    private val playlistsFile = File(context.filesDir, "user_playlists.json")
    private val favoritesFile = File(context.filesDir, "favorite_tracks.json")

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _favoriteTrackIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteTrackIds: StateFlow<Set<Long>> = _favoriteTrackIds.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Load favorites
        try {
            if (favoritesFile.exists()) {
                val jsonStr = favoritesFile.readText()
                val jsonArray = JSONArray(jsonStr)
                val favs = mutableSetOf<Long>()
                for (i in 0 until jsonArray.length()) {
                    favs.add(jsonArray.getLong(i))
                }
                _favoriteTrackIds.value = favs
            } else {
                // Default favorite track (e.g. Dreamscape demo 900000L)
                val initialFavs = setOf(900000L)
                _favoriteTrackIds.value = initialFavs
                saveFavorites(initialFavs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Load playlists
        try {
            if (playlistsFile.exists()) {
                val jsonStr = playlistsFile.readText()
                val jsonArray = JSONArray(jsonStr)
                val list = mutableListOf<Playlist>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val desc = obj.optString("description", "")
                    val createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    val tracksArray = obj.getJSONArray("trackIds")
                    val trackIds = mutableListOf<Long>()
                    for (t in 0 until tracksArray.length()) {
                        trackIds.add(tracksArray.getLong(t))
                    }
                    list.add(Playlist(id = id, name = name, description = desc, trackIds = trackIds, createdAt = createdAt))
                }
                _playlists.value = list
            } else {
                // Seed initial curated playlists
                val initialPlaylists = listOf(
                    Playlist(
                        id = "playlist_sunset_vibes",
                        name = "Sunset Vibes",
                        description = "Dreamy evening melodies & chill soundscapes",
                        trackIds = listOf(900000L, 900001L)
                    ),
                    Playlist(
                        id = "playlist_night_drive",
                        name = "Night Drive",
                        description = "Atmospheric late night cruising playlist",
                        trackIds = listOf(900000L, 900002L)
                    )
                )
                _playlists.value = initialPlaylists
                savePlaylists(initialPlaylists)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createPlaylist(name: String, description: String = ""): Playlist = withContext(Dispatchers.IO) {
        val newPlaylist = Playlist(
            name = name.trim().ifEmpty { "My Playlist" },
            description = description.trim(),
            trackIds = emptyList()
        )
        val updated = _playlists.value + newPlaylist
        _playlists.value = updated
        savePlaylists(updated)
        newPlaylist
    }

    suspend fun deletePlaylist(playlistId: String) = withContext(Dispatchers.IO) {
        val updated = _playlists.value.filter { it.id != playlistId }
        _playlists.value = updated
        savePlaylists(updated)
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackId: Long) = withContext(Dispatchers.IO) {
        val updated = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                if (playlist.trackIds.contains(trackId)) {
                    playlist
                } else {
                    playlist.copy(trackIds = playlist.trackIds + trackId)
                }
            } else {
                playlist
            }
        }
        _playlists.value = updated
        savePlaylists(updated)
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: Long) = withContext(Dispatchers.IO) {
        val updated = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(trackIds = playlist.trackIds.filter { it != trackId })
            } else {
                playlist
            }
        }
        _playlists.value = updated
        savePlaylists(updated)
    }

    suspend fun toggleFavorite(trackId: Long): Boolean = withContext(Dispatchers.IO) {
        val currentFavs = _favoriteTrackIds.value.toMutableSet()
        val isNowFavorite = if (currentFavs.contains(trackId)) {
            currentFavs.remove(trackId)
            false
        } else {
            currentFavs.add(trackId)
            true
        }
        _favoriteTrackIds.value = currentFavs
        saveFavorites(currentFavs)
        isNowFavorite
    }

    private fun savePlaylists(list: List<Playlist>) {
        try {
            val array = JSONArray()
            list.forEach { pl ->
                val obj = JSONObject().apply {
                    put("id", pl.id)
                    put("name", pl.name)
                    put("description", pl.description)
                    put("createdAt", pl.createdAt)
                    val tracksArr = JSONArray()
                    pl.trackIds.forEach { tracksArr.put(it) }
                    put("trackIds", tracksArr)
                }
                array.put(obj)
            }
            playlistsFile.writeText(array.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveFavorites(favs: Set<Long>) {
        try {
            val array = JSONArray()
            favs.forEach { array.put(it) }
            favoritesFile.writeText(array.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
