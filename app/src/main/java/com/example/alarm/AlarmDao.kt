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
    suspend fun insertAlarm(alarm: Alarm): Long

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Query("DELETE FROM alarm WHERE id IN (:alarmsId)")
    suspend fun deleteById(alarmsId: Set<Long>)

    @Query("DELETE FROM alarm WHERE id != :alarmId AND time = :alarmTime AND weekDaysEnabled = :alarmWeekDaysEnabled")
    suspend fun deleteSimilarAlarm(alarmId: Long, alarmTime: String, alarmWeekDaysEnabled: String)

    @Query("UPDATE alarm SET alarmIsEnabled = :newState WHERE id = :alarmId")
    suspend fun updateAlarmState(alarmId: Long, newState: Boolean)

    @Query("SELECT * FROM alarm ORDER BY time")
    suspend fun getAll(): List<Alarm>

    @Query("SELECT * FROM alarm WHERE id = :alarmId")
    suspend fun getAlarm(alarmId: Long): Alarm

    @Query("SELECT id FROM Alarm WHERE id != :alarmId AND time = :alarmTime AND weekDaysEnabled = :alarmWeekDaysEnabled")
    suspend fun getSimilarAlarm(alarmId: Long, alarmTime: String, alarmWeekDaysEnabled: String): Long?
}