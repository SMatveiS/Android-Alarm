package com.example.alarm

import android.content.Context
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter

object Utils {
    fun weekSetToString(alarm: Alarm) {
        alarm.weekDaysEnabledSet.forEach { el ->
            alarm.weekDaysEnabled += el.value.toString()
        }
    }

    fun weekStringToSet(alarm: Alarm) {
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
}