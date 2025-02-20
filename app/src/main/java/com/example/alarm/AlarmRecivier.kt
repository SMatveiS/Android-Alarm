package com.example.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {

    private fun createNotification(context: Context, alarmName: String?) {
        val channelId = "alarm_channel"
        val channelName = "Alarm Channel"

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val fullScreenIntent = Intent(context, FullScreenNotification::class.java).apply {
            putExtra("ALARM_NAME", alarmName)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val stopAlarmIntent = Intent(context, AlarmReceiver::class.java).setAction("STOP_ALARM")
        val stopAlarmPendingIntent = PendingIntent.getBroadcast(context, 3, stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.access_alarm)
            .setContentTitle("Будильник")
            .setContentText(alarmName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.cancel_alarm, "Выключить", stopAlarmPendingIntent)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("kasmflsvb")
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }
        NotificationManagerCompat.from(context).notify(2, builder.build())
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "START_ALARM") {
            // Если какой-то будильник работал в этот момент, то отключаем
            AlarmMusic.stop()
            NotificationManagerCompat.from(context).cancelAll()

            AlarmMusic.start(context)
            createNotification(context, intent.getStringExtra("ALARM_NAME"))
        }

        else if (intent.action == "STOP_ALARM") {
            AlarmMusic.stop()
            NotificationManagerCompat.from(context).cancel(2)
        }


    }
}