package com.josetoanto.subastas.core.hardware.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.josetoanto.subastas.core.hardware.domain.VibrationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidVibrationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : VibrationManager {

    override fun vibrateOnOutbid() {
        val pattern = longArrayOf(0, 200, 100, 200, 100, 400)
        vibrate(pattern)
    }

    override fun vibrateOnWin() {
        val pattern = longArrayOf(0, 100, 50, 100, 50, 100, 50, 600)
        vibrate(pattern)
    }

    private fun vibrate(pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            }
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        }
    }
}
