package com.example.giflightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserPreferencesRepository {
    val searchQuery: Flow<String>
    suspend fun saveSearchQuery(searchQuery: String)
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class DataStoreUserPreferencesRepository(
    private val context: Context
) : UserPreferencesRepository {
    private val searchQueryKey = stringPreferencesKey("search_query")

    override val searchQuery: Flow<String> = context.dataStore.data
        .map {
            it[searchQueryKey] ?: ""
        }

    override suspend fun saveSearchQuery(searchQuery: String) {
        context.dataStore.edit {
            it[searchQueryKey] = searchQuery
        }
    }
}
