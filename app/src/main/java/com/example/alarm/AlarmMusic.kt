package com.example.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object AlarmMusic {
    private var music: MediaPlayer? = null

    fun start(context: Context) {
        music?.release()
        if (music == null) {
            music = MediaPlayer.create(context, R.raw.linkin_park_numb)
        }
//        // Для воспроизведения поверх других звуков
//        music?.setAudioAttributes(
//            AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_ALARM)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build()
//        )
        music?.isLooping = true
        music?.start()
    }

    fun stop() {
        music?.let {
            if (music!!.isPlaying) {
                music!!.stop()
            }

            music!!.release()
            music = null
        }
    }
}