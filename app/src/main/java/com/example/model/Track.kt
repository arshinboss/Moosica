package com.example.model

import android.net.Uri
import java.util.UUID

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val contentUri: Uri,
    val albumArtUri: Uri? = null,
    val coverDrawableRes: Int? = null,
    val embeddedPictureBytes: ByteArray? = null,
    val path: String = "",
    val trackNumber: Int = 0,
    val year: Int = 0,
    val isDemo: Boolean = false,
    val isFavorite: Boolean = false
) {
    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val trackIds: List<Long> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}
