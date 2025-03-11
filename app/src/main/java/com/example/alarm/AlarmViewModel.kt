package com.example.alarm

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmDao = AppDatabase.getDatabase(application).alarmDao()

    suspend fun insert(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm)
    }

    suspend fun allAlarms(): List<Alarm> {
        return alarmDao.getAll()
    }

    suspend fun getAlarm(alarmId: Long): Alarm {
        return alarmDao.getAlarm(alarmId)
    }

    suspend fun getSimilarAlarm(alarmId: Long, alarmTime: String, alarmWeekDaysEnabled: String): Long? {
        return alarmDao.getSimilarAlarm(alarmId, alarmTime, alarmWeekDaysEnabled)
    }

    fun delById(alarmsId: Set<Long>) = viewModelScope.launch {
        alarmDao.deleteById(alarmsId)
    }

    fun delSimilarAlarm(alarmId: Long, alarmTime: String, alarmWeekDaysEnabled: String) = viewModelScope.launch {
        alarmDao.deleteSimilarAlarm(alarmId, alarmTime, alarmWeekDaysEnabled)
    }

    fun changeAlarmState(alarmId: Long, newState: Boolean) = viewModelScope.launch {
        alarmDao.updateAlarmState(alarmId, newState)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmDao.updateAlarm(alarm)
    }
}