package com.example.alarm

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity
data class Alarm(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var alarmIsEnabled: Boolean = true,
    var name: String = "",
    var time: String = "06:00",
    //var date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    var soundIsEnabled: Boolean = true,
    var soundName: String = "default",
    var vibrationIsEnabled: Boolean = true,
    var vibrationName: String = "default",
    var delAfterUseIsEnabled: Boolean = false,

    var weekDaysEnabled: String = "",
)
{
    @Ignore var weekDaysEnabledSet: MutableSet<DayOfWeek> = mutableSetOf()
}