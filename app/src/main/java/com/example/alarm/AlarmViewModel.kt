package com.example.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmDao = AppDatabase.getDatabase(application).alarmDao()

    suspend fun insert(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm)
    }

    fun allAlarms(): LiveData<List<Alarm>> {
        return alarmDao.getAll()
    }

    suspend fun getAlarm(alarmId: Long): Alarm {
        return alarmDao.getAlarm(alarmId)
    }

    suspend fun getSimilarAlarm(alarmId: Long, alarmTime: String, alarmWeekDaysEnabled: String): Long? {
        return alarmDao.getSimilarAlarm(alarmId, alarmTime, alarmWeekDaysEnabled)
    }

    fun delById(alarmsId: Long) = viewModelScope.launch {
        alarmDao.deleteById(alarmsId)
    }

    fun changeAlarmState(alarmId: Long, newState: Boolean) = viewModelScope.launch {
        alarmDao.updateAlarmState(alarmId, newState)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmDao.updateAlarm(alarm)
    }
}