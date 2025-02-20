package com.example.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    val alarmDao = AppDatabase.getDatabase(application).alarmDao()


    suspend fun allAlarms(): List<Alarm> {
        return alarmDao.getAll()
    }

    suspend fun getAlarm(alarmId: Int): Alarm {
        return alarmDao.getAlarm(alarmId)
    }

    fun insert(alarm: Alarm) = viewModelScope.launch {
        alarmDao.addAlarm(alarm)
    }

    fun changeAlarmState(alarmId: Int, newState: Boolean) = viewModelScope.launch {
        alarmDao.updateAlarmState(alarmId, newState)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmDao.updateAlarm(alarm)
    }

}