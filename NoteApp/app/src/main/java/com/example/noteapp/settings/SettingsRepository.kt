package com.example.noteapp.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "noteapp_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val USER_NAME: Preferences.Key<String> = stringPreferencesKey("user_name")
        val SORT_MODE: Preferences.Key<String> = stringPreferencesKey("sort_mode")
        val MARKDOWN_ENABLED: Preferences.Key<Boolean> = booleanPreferencesKey("markdown_enabled")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            userName = prefs[Keys.USER_NAME].orEmpty(),
            sortMode = prefs[Keys.SORT_MODE]?.let { runCatching { SortMode.valueOf(it) }.getOrNull() }
                ?: SortMode.PRIORITY_ASC,
            markdownEnabled = prefs[Keys.MARKDOWN_ENABLED] ?: true
        )
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs -> prefs[Keys.USER_NAME] = name.trim() }
    }

    suspend fun setSortMode(mode: SortMode) {
        context.dataStore.edit { prefs -> prefs[Keys.SORT_MODE] = mode.name }
    }

    suspend fun setMarkdownEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.MARKDOWN_ENABLED] = enabled }
    }
}

