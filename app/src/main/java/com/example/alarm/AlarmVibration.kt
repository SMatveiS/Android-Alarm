package com.example.alarm

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object AlarmVibration {
    private var vibrator: Vibrator? = null

    private fun getVibrator(context: Context) {
        // Если API 26 или выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }


    fun start(context: Context) {
        if (vibrator == null)
            getVibrator(context)

        // Проверяем, поддерживает ли устройство вибрацию
        if (vibrator!!.hasVibrator()) {
            // 1,5 сек вибрация, 1,5 сек пауза, будет повторяться бесконечно
            val pattern = longArrayOf(0, 1500, 1500)

            // Создаем эффект вибрации (только для API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val effect = VibrationEffect.createWaveform(pattern, 0)
                vibrator!!.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator!!.vibrate(pattern, 0)
            }
        }
    }

    fun stop() {
        vibrator?.let { vibrator!!.cancel() }
    }
}