package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundManager(context: Context) {

    private var toneGenerator: ToneGenerator? = null
    var isMuted: Boolean = false

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playMoveSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (e: Exception) {
            // Ignore fallback
        }
    }

    fun playAiMoveSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
        } catch (e: Exception) {
            // Ignore fallback
        }
    }

    fun playWinSound(scope: CoroutineScope) {
        if (isMuted) return
        scope.launch(Dispatchers.Default) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_8, 100)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 100)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 250)
            } catch (e: Exception) {
                // Ignore fallback
            }
        }
    }

    fun playDrawSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        } catch (e: Exception) {
            // Ignore fallback
        }
    }

    fun playButtonClick() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 40)
        } catch (e: Exception) {
            // Ignore fallback
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
