package com.josetoanto.subastas.core.hardware.data

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.josetoanto.subastas.R
import com.josetoanto.subastas.core.hardware.domain.SoundManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidSoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SoundManager {

    private var soundPool: SoundPool? = null
    private var soundId: Int = 0
    private var loaded = false
    private var pendingPlay = false

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attrs)
            .build()
        soundPool?.setOnLoadCompleteListener { _, _, status ->
            loaded = status == 0
            if (loaded && pendingPlay) {
                pendingPlay = false
                soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        }
        soundId = soundPool?.load(context, R.raw.winner, 1) ?: 0
    }

    override fun playWinSound() {
        if (loaded) {
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            pendingPlay = true
        }
    }

    override fun release() {
        soundPool?.release()
        soundPool = null
        loaded = false
        pendingPlay = false
    }
}
