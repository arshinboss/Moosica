package com.example.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.util.Log

data class EqBand(
    val index: Int,
    val centerFreqHz: Int,
    val minLevelMb: Short, // millibels, e.g. -1500 = -15dB
    val maxLevelMb: Short, // e.g. +1500 = +15dB
    val currentLevelMb: Short
) {
    val currentLevelDb: Float
        get() = currentLevelMb / 100f

    val formattedFreq: String
        get() = if (centerFreqHz >= 1000) "${centerFreqHz / 1000} kHz" else "$centerFreqHz Hz"
}

data class EqPreset(
    val name: String,
    val bandLevelsMb: List<Short>,
    val bassBoostStrength: Short = 0,
    val isCustom: Boolean = false
)

class EqualizerManager {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var currentAudioSessionId: Int = 0

    var isEnabled: Boolean = true
        private set

    // Default fallback 5-band configuration (-15dB to +15dB)
    private val defaultFrequencies = intArrayOf(60, 230, 910, 3600, 14000)
    private var currentBandLevels = mutableListOf<Short>(0, 0, 0, 0, 0)
    private var currentBassBoost: Short = 0

    val builtInPresets: List<EqPreset> = listOf(
        EqPreset("Warm Vinyl", listOf(400, 250, 0, -200, -450), bassBoostStrength = 350),
        EqPreset("Cassette Tape", listOf(200, 450, 150, -100, -300), bassBoostStrength = 200),
        EqPreset("Tube Amp Hi-Fi", listOf(450, 150, 200, 300, 400), bassBoostStrength = 300),
        EqPreset("Jazz Master", listOf(300, 100, 350, 150, 250), bassBoostStrength = 150),
        EqPreset("Classic Rock", listOf(500, 200, -150, 300, 500), bassBoostStrength = 400),
        EqPreset("Acoustic Vocal", listOf(0, 100, 400, 300, 150), bassBoostStrength = 0),
        EqPreset("Deep Bass", listOf(800, 500, 0, -100, -200), bassBoostStrength = 750),
        EqPreset("Audiophile Flat", listOf(0, 0, 0, 0, 0), bassBoostStrength = 0)
    )

    private var activePresetName: String = "Warm Vinyl"

    fun attachAudioSession(audioSessionId: Int) {
        if (audioSessionId == 0 || audioSessionId == currentAudioSessionId) return
        release()
        currentAudioSessionId = audioSessionId

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = this@EqualizerManager.isEnabled
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = this@EqualizerManager.isEnabled
                if (strengthSupported) {
                    setStrength(currentBassBoost)
                }
            }

            // Sync with current band settings
            applyPreset(builtInPresets.firstOrNull { it.name == activePresetName } ?: builtInPresets[0])
        } catch (e: Exception) {
            Log.w("EqualizerManager", "Could not initialize hardware equalizer: ${e.message}")
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBands(): List<EqBand> {
        val eq = equalizer
        if (eq != null) {
            try {
                val numBands = eq.numberOfBands.toInt()
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]

                return (0 until numBands).map { i ->
                    val bandIndex = i.toShort()
                    val freqMhz = eq.getCenterFreq(bandIndex)
                    val freqHz = freqMhz / 1000
                    val currentLevel = eq.getBandLevel(bandIndex)
                    EqBand(
                        index = i,
                        centerFreqHz = freqHz,
                        minLevelMb = minLevel,
                        maxLevelMb = maxLevel,
                        currentLevelMb = currentLevel
                    )
                }
            } catch (e: Exception) {
                Log.w("EqualizerManager", "Failed reading hardware bands, using fallback: ${e.message}")
            }
        }

        // Fallback representation
        return defaultFrequencies.mapIndexed { index, freq ->
            val level = currentBandLevels.getOrElse(index) { 0 }
            EqBand(
                index = index,
                centerFreqHz = freq,
                minLevelMb = -1500,
                maxLevelMb = 1500,
                currentLevelMb = level
            )
        }
    }

    fun setBandLevel(bandIndex: Int, levelMb: Short) {
        if (bandIndex in 0 until currentBandLevels.size) {
            currentBandLevels[bandIndex] = levelMb
        }
        activePresetName = "Custom"
        try {
            equalizer?.setBandLevel(bandIndex.toShort(), levelMb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(strength: Short) {
        currentBassBoost = strength
        try {
            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(strength)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBassBoost(): Short = currentBassBoost

    fun applyPreset(preset: EqPreset) {
        activePresetName = preset.name
        preset.bandLevelsMb.forEachIndexed { index, level ->
            setBandLevel(index, level)
        }
        setBassBoost(preset.bassBoostStrength)
        activePresetName = preset.name
    }

    fun getActivePresetName(): String = activePresetName

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            equalizer = null
            bassBoost = null
            currentAudioSessionId = 0
        }
    }
}
