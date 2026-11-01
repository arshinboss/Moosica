package com.example

import com.example.audio.EqPreset
import com.example.model.Playlist
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testEqPresetCreation() {
        val preset = EqPreset(
            name = "Warm Sunset",
            bandLevelsMb = listOf(100, 200, 0, -100, -200),
            bassBoostStrength = 150,
            isCustom = true
        )
        assertEquals("Warm Sunset", preset.name)
        assertTrue(preset.isCustom)
        assertEquals(5, preset.bandLevelsMb.size)
        assertEquals(150.toShort(), preset.bassBoostStrength)
    }

    @Test
    fun testDurationCalculationMath() {
        val durationMs = 236000L // 3 min 56 sec matching mockup
        val totalSec = durationMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        assertEquals("03:56", String.format("%02d:%02d", min, sec))
    }

    @Test
    fun testPlaylistTrackManagement() {
        val playlist = Playlist(
            id = "test_pl_1",
            name = "Dreamy Sunset Vibes",
            trackIds = listOf(101L, 102L)
        )
        assertEquals(2, playlist.trackIds.size)
        assertTrue(playlist.trackIds.contains(101L))

        // Add track
        val updated = playlist.copy(trackIds = playlist.trackIds + 103L)
        assertEquals(3, updated.trackIds.size)
        assertTrue(updated.trackIds.contains(103L))

        // Remove track
        val removed = updated.copy(trackIds = updated.trackIds.filter { it != 101L })
        assertEquals(2, removed.trackIds.size)
        assertFalse(removed.trackIds.contains(101L))
    }
}
