package com.example.audio

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object SoundEffects {

    suspend fun playHackerBeep(freq: Float = 880f, durationMs: Int = 40) = withContext(Dispatchers.Default) {
        generateTone(freq, durationMs, 0.25f)
    }

    suspend fun playDataPacketChirp() = withContext(Dispatchers.Default) {
        val freqs = floatArrayOf(600f, 900f, 1200f)
        for (f in freqs) {
            generateTone(f, 25, 0.2f)
        }
    }

    suspend fun playAlarmBuzzer() = withContext(Dispatchers.Default) {
        for (i in 0..2) {
            generateTone(440f, 80, 0.35f)
            generateTone(330f, 80, 0.35f)
        }
    }

    suspend fun playRoastFanfare() = withContext(Dispatchers.Default) {
        val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f, 987.77f, 1046.50f)
        val durations = intArrayOf(90, 90, 90, 200, 100, 300)
        for (i in notes.indices) {
            generateTone(notes[i], durations[i], 0.4f)
        }
    }

    private fun generateTone(freq: Float, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            val angularFreq = 2.0 * Math.PI * freq / sampleRate
            for (i in 0 until numSamples) {
                // Envelope decay to prevent clicks
                val envelope = when {
                    i < 80 -> i / 80.0
                    i > numSamples - 80 -> (numSamples - i) / 80.0
                    else -> 1.0
                }
                val sample = (sin(angularFreq * i) * 32767.0 * volume * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 10)
            track.release()
        } catch (_: Exception) {
            // Audio error recovery
        }
    }
}

class AudioWaveDetector(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startListening() {
        if (!hasPermission()) return
        try {
            val sampleRate = 16000
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            if (bufferSize <= 0) return

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord?.startRecording()
                isRecording = true
            }
        } catch (_: Exception) {
            isRecording = false
        }
    }

    fun readCurrentAmplitude(): Float {
        if (!isRecording || audioRecord == null) return 0f
        return try {
            val buffer = ShortArray(256)
            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (read > 0) {
                var max = 0
                for (i in 0 until read) {
                    val abs = kotlin.math.abs(buffer[i].toInt())
                    if (abs > max) max = abs
                }
                (max / 32767f).coerceIn(0f, 1f)
            } else {
                0f
            }
        } catch (_: Exception) {
            0f
        }
    }

    fun stop() {
        isRecording = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {
        }
        audioRecord = null
    }
}
