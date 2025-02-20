package com.example.alarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.alarm.databinding.FullScreenNotificationBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class FullScreenNotification : AppCompatActivity() {
    private var binding: FullScreenNotificationBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FullScreenNotificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.time.text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        binding!!.day.text = Utils.getDayText(this, LocalDate.now())
        binding!!.name.text = intent.getStringExtra("ALARM_NAME")

        binding!!.dismiss.setOnClickListener {
            AlarmMusic.stop()
            NotificationManagerCompat.from(this).cancel(2)
        }
    }
}