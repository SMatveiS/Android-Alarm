package com.example.alarm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(newAlarm: Alarm)

    @Delete
    suspend fun delAlarm(alarm: Alarm)

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Query("UPDATE alarm SET alarmIsEnabled = :newState WHERE id = :alarmId")
    suspend fun updateAlarmState(alarmId: Int, newState: Boolean)

    @Query("SELECT * FROM alarm ORDER BY time")
    suspend fun getAll(): List<Alarm>

    @Query("SELECT * FROM alarm WHERE id = :alarmId")
    suspend fun getAlarm(alarmId: Int): Alarm
}