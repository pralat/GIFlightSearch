package com.example.giflightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(
    private val context: Context
) {
    private val searchQueryKey = stringPreferencesKey("search_query")

    val searchQuery: Flow<String> = context.dataStore.data
        .map {
            it[searchQueryKey] ?: ""
        }

    suspend fun saveSearchQuery(searchQuery: String) {
        context.dataStore.edit {
            it[searchQueryKey] = searchQuery
        }
    }
}
