package com.example.nq.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class DataStoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "YourDataStoreName")
    }

    suspend fun saveStringToDataStore(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getStringFromDataStore(key: Preferences.Key<String>, defaultValue: String): String {
        val preferences = context.dataStore.data.first()
        return preferences[key] ?: defaultValue
    }
}
