package com.example.giflightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FLIGHT_SEARCH_PREFERENCES = "flight_search_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = FLIGHT_SEARCH_PREFERENCES
)

open class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SEARCH_QUERY = stringPreferencesKey("search_query")
    }

    open val searchQuery: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SEARCH_QUERY] ?: ""
        }

    open suspend fun saveSearchQuery(searchQuery: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEARCH_QUERY] = searchQuery
        }
    }
}