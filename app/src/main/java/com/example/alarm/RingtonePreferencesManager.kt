package com.example.alarm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.ringtoneDataStore: DataStore<Preferences> by preferencesDataStore(name = "ringtone_settings")

class RingtonePreferencesManager(private val context: Context) {
    private val ringtoneUriKey = stringPreferencesKey("selected_ringtone_uri")

    // Получение сохраненного URI рингтона
    val ringtoneUri: Flow<String?> = context.ringtoneDataStore.data
        .map { preferences ->
            preferences[ringtoneUriKey]
        }

    // Сохранение URI рингтона
    suspend fun saveRingtoneUri(uri: String) {
        context.ringtoneDataStore.edit { preferences ->
            preferences[ringtoneUriKey] = uri
        }
    }
}