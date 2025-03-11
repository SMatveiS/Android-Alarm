package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.sql.Time
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Utils {

    fun parseWeekSetToString(alarm: Alarm) {
        alarm.weekDaysEnabledSet.forEach { el ->
            alarm.weekDaysEnabled += el.value.toString()
        }
    }

    fun parseWeekStringToSet(alarm: Alarm) {
        for (i in 0..<alarm.weekDaysEnabled.length) {
            alarm.weekDaysEnabledSet.add(DayOfWeek.of(alarm.weekDaysEnabled[i].digitToInt()))
        }
    }

    fun getAlarmDate(alarm: Alarm) : LocalDate {
        var alarmDate: LocalDate = LocalDate.now()
        val alarmTime = LocalTime.parse(alarm.time, DateTimeFormatter.ofPattern("HH:mm"))

        if (alarm.weekDaysEnabledSet.size in arrayOf(0, 7)) {
            if (alarmTime < LocalTime.now())
                alarmDate = LocalDate.now().plusDays(1)
        }

        else {
            val today: Int = LocalDate.now().dayOfWeek.value
            var daysToAlarm = 7

            alarm.weekDaysEnabledSet.forEach { el ->
                var daysToWeekDay = ((el.value + 7) - today) % 7
                if (daysToWeekDay == 0 && LocalTime.now() > alarmTime)
                    daysToWeekDay = 7

                if (daysToAlarm > daysToWeekDay)
                    daysToAlarm = daysToWeekDay
            }

            alarmDate = LocalDate.now().plusDays(daysToAlarm.toLong())
        }

        return alarmDate
    }

    fun getDayText(context: Context, date: LocalDate): String {
        var dayText = when (date.dayOfWeek!!) {
            DayOfWeek.MONDAY -> ContextCompat.getString(context, R.string.mon)
            DayOfWeek.TUESDAY -> ContextCompat.getString(context, R.string.tue)
            DayOfWeek.WEDNESDAY -> ContextCompat.getString(context, R.string.wed)
            DayOfWeek.THURSDAY -> ContextCompat.getString(context, R.string.thu)
            DayOfWeek.FRIDAY -> ContextCompat.getString(context, R.string.fri)
            DayOfWeek.SATURDAY -> ContextCompat.getString(context, R.string.sat)
            DayOfWeek.SUNDAY -> ContextCompat.getString(context, R.string.sun)
        }

        dayText += "${date.dayOfMonth} "

        dayText += when (date.month!!) {
            Month.JANUARY -> ContextCompat.getString(context, R.string.january)
            Month.FEBRUARY-> ContextCompat.getString(context, R.string.february)
            Month.MARCH -> ContextCompat.getString(context, R.string.march)
            Month.APRIL -> ContextCompat.getString(context, R.string.april)
            Month.MAY -> ContextCompat.getString(context, R.string.may)
            Month.JUNE -> ContextCompat.getString(context, R.string.june)
            Month.JULY -> ContextCompat.getString(context, R.string.july)
            Month.AUGUST -> ContextCompat.getString(context, R.string.august)
            Month.SEPTEMBER -> ContextCompat.getString(context, R.string.september)
            Month.OCTOBER -> ContextCompat.getString(context, R.string.october)
            Month.NOVEMBER -> ContextCompat.getString(context, R.string.november)
            Month.DECEMBER -> ContextCompat.getString(context, R.string.december)
        }

        return dayText
    }

    private fun getAlarmInfoPendingIntent(context: Context): PendingIntent {
        val alarmInfoIntent = Intent(context, MainActivity::class.java)
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(context, 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun createAlarmReceiver(context: Context, id: Long, alarm: Alarm) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("ALARM_NAME", alarm.name)
            setAction("START_ALARM")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Удаляем AlarmReceiver, если он существует (иначе ничего не произойдёт)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        val alarmDate = getAlarmDate(alarm).atTime(LocalTime.parse(alarm.time, DateTimeFormatter.ofPattern("HH:mm")))
        val instant = alarmDate.atZone(ZoneId.systemDefault()).toInstant()
        val alarmClockInfo = AlarmManager.AlarmClockInfo(instant.toEpochMilli(), getAlarmInfoPendingIntent(context))
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }


    fun delAlarmReceiver(context: Context, alarmId: Long) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            setAction("START_ALARM")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }
}