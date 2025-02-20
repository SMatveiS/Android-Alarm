package com.example.alarm

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity
data class Alarm(
    @PrimaryKey (autoGenerate = true) var id: Int = 0,
    var alarmIsEnabled: Boolean = true,
    var name: String = "",
    var time: String = "06:00",
    var date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    var soundIsEnabled: Boolean = true,
    var soundName: String = "default",
    var vibrationIsEnabled: Boolean = true,
    var vibrationName: String = "default",
    var delAfterUseIsEnabled: Boolean = true,

    var weekDaysEnabled: String = "",
)
{
    @Ignore var weekDaysEnabledSet: MutableSet<DayOfWeek> = mutableSetOf()
}

//    var everyMon: Boolean = false,
//    var everyTue: Boolean = false,
//    var everyWed: Boolean = false,
//    var everyThu: Boolean = false,
//    var everyFri: Boolean = false,
//    var everySat: Boolean = false,
//    var everySun: Boolean = false,
//    var countWeekDays: Int = 0