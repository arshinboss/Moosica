package com.example.audio

import android.content.Context
import android.net.Uri
import com.example.R
import com.example.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

object AudioSynthesizer {

    suspend fun getOrGenerateDemoTracks(context: Context): List<Track> = withContext(Dispatchers.IO) {
        val demoDir = File(context.cacheDir, "vintage_demo_tracks")
        if (!demoDir.exists()) demoDir.mkdirs()

        val demos = listOf(
            DemoTrackSpec(
                title = "Dreamscape",
                artist = "Aurora Lights",
                album = "Sunset Reverie",
                filename = "dreamscape_aurora.wav",
                baseFreq = 220.0, // A3
                scale = doubleArrayOf(220.0, 246.94, 277.18, 329.63, 369.99, 440.0),
                tempoBpm = 82,
                durationSeconds = 24,
                displayDurationMs = 236000L, // 3:56 display matching mockup
                coverDrawableRes = R.drawable.dreamscape_cover
            ),
            DemoTrackSpec(
                title = "Golden Sunset",
                artist = "Solstice Ray",
                album = "Coastal Horizons",
                filename = "golden_sunset.wav",
                baseFreq = 196.0, // G3
                scale = doubleArrayOf(196.0, 220.0, 246.94, 293.66, 329.63, 392.0),
                tempoBpm = 95,
                durationSeconds = 20,
                displayDurationMs = 196000L,
                coverDrawableRes = null
            ),
            DemoTrackSpec(
                title = "Nightfall Echoes",
                artist = "Velvet Sound",
                album = "Twilight Ambient",
                filename = "nightfall_echoes.wav",
                baseFreq = 164.81, // E3
                scale = doubleArrayOf(164.81, 196.0, 220.0, 246.94, 329.63, 392.0),
                tempoBpm = 74,
                durationSeconds = 22,
                displayDurationMs = 215000L,
                coverDrawableRes = null
            )
        )

        demos.mapIndexed { index, spec ->
            val file = File(demoDir, spec.filename)
            if (!file.exists() || file.length() < 1000) {
                generateWavFile(file, spec)
            }
            Track(
                id = 900000L + index,
                title = spec.title,
                artist = spec.artist,
                album = spec.album,
                durationMs = spec.displayDurationMs,
                contentUri = Uri.fromFile(file),
                albumArtUri = null,
                coverDrawableRes = spec.coverDrawableRes,
                path = file.absolutePath,
                trackNumber = index + 1,
                year = 2024,
                isDemo = true,
                isFavorite = (index == 0) // Dreamscape is favorite by default
            )
        }
    }

    private data class DemoTrackSpec(
        val title: String,
        val artist: String,
        val album: String,
        val filename: String,
        val baseFreq: Double,
        val scale: DoubleArray,
        val tempoBpm: Int,
        val durationSeconds: Int,
        val displayDurationMs: Long,
        val coverDrawableRes: Int? = null
    )

    private fun generateWavFile(file: File, spec: DemoTrackSpec) {
        val sampleRate = 44100
        val totalSamples = sampleRate * spec.durationSeconds
        val numChannels = 2
        val bytesPerSample = 2 // 16-bit
        val dataSize = totalSamples * numChannels * bytesPerSample

        val buffer = ByteBuffer.allocate(dataSize)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        val beatDurationSec = 60.0 / spec.tempoBpm
        val samplesPerBeat = (sampleRate * beatDurationSec).toInt()

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val currentBeat = (i / samplesPerBeat)
            val noteIndex = (currentBeat + (currentBeat / 2)) % spec.scale.size
            val melodyFreq = spec.scale[noteIndex]

            // Note envelope (fade in and decay per beat)
            val sampleInBeat = i % samplesPerBeat
            val envelope = (1.0 - (sampleInBeat.toDouble() / samplesPerBeat)).coerceIn(0.0, 1.0)

            // Warm atmospheric synth harmonics (lush ambient pads + bell pluck)
            val leftSignal = (
                0.45 * sin(2.0 * PI * melodyFreq * t) +
                0.22 * sin(2.0 * PI * (melodyFreq * 2.0) * t) +
                0.20 * sin(2.0 * PI * (spec.baseFreq * 0.5) * t) // warm sub bass
            ) * envelope

            val rightSignal = (
                0.45 * sin(2.0 * PI * (melodyFreq * 1.5) * t) +
                0.22 * sin(2.0 * PI * (melodyFreq * 2.01) * t) + // lush stereo chorus
                0.20 * sin(2.0 * PI * (spec.baseFreq * 0.5) * t)
            ) * envelope

            val leftShort = (leftSignal.coerceIn(-1.0, 1.0) * 26000).toInt().toShort()
            val rightShort = (rightSignal.coerceIn(-1.0, 1.0) * 26000).toInt().toShort()

            buffer.putShort(leftShort)
            buffer.putShort(rightShort)
        }

        FileOutputStream(file).use { fos ->
            writeWavHeader(fos, totalSamples, sampleRate, numChannels, bytesPerSample)
            fos.write(buffer.array())
        }
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        totalAudioLen: Int,
        sampleRate: Int,
        channels: Int,
        bytesPerSample: Int
    ) {
        val totalDataLen = totalAudioLen * channels * bytesPerSample + 36
        val byteRate = sampleRate * channels * bytesPerSample

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // PCM header length
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // PCM format
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (channels * bytesPerSample).toByte()
        header[33] = 0
        header[34] = (bytesPerSample * 8).toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        val audioDataSize = totalAudioLen * channels * bytesPerSample
        header[40] = (audioDataSize and 0xff).toByte()
        header[41] = (audioDataSize shr 8 and 0xff).toByte()
        header[42] = (audioDataSize shr 16 and 0xff).toByte()
        header[43] = (audioDataSize shr 24 and 0xff).toByte()

        out.write(header, 0, 44)
    }
}
