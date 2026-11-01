package com.example

import com.example.audio.EqualizerManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `test equalizer presets have valid configurations`() {
        val eqManager = EqualizerManager()
        val presets = eqManager.builtInPresets
        assertTrue("Presets should not be empty", presets.isNotEmpty())

        val warmVinyl = presets.find { it.name == "Warm Vinyl" }
        assertNotNull("Warm vinyl preset should exist", warmVinyl)
        assertEquals(5, warmVinyl!!.bandLevelsMb.size)

        // Verify Warm Vinyl characteristics
        assertTrue("Sub bass should be boosted", warmVinyl.bandLevelsMb[0] > 0)
        assertTrue("High frequencies should be rolled off", warmVinyl.bandLevelsMb[4] < 0)
    }
}
