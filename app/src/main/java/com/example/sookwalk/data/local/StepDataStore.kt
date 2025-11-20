package com.example.sookwalk.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object StepCounterDataStore {

    private val Context.dataStore by preferencesDataStore("step_counter")

    private val KEY_LAST_COUNTER = floatPreferencesKey("last_counter")
    private val KEY_TOTAL_STEPS = intPreferencesKey("total_steps")

    suspend fun saveLastCounter(context: Context, value: Float) {
        context.dataStore.edit { it[KEY_LAST_COUNTER] = value }
    }

    suspend fun readLastCounter(context: Context): Float? {
        return context.dataStore.data.first()[KEY_LAST_COUNTER]
    }

    suspend fun saveTotalSteps(context: Context, value: Int) {
        context.dataStore.edit { it[KEY_TOTAL_STEPS] = value }
    }

    suspend fun readTotalSteps(context: Context): Int {
        return context.dataStore.data.first()[KEY_TOTAL_STEPS] ?: 0
    }
}

