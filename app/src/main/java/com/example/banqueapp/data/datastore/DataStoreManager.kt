package com.example.banqueapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {


    val userTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[UserPreferences.USER_TOKEN] }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[UserPreferences.IS_LOGGED_IN] ?: false }

    suspend fun saveUserToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferences.USER_TOKEN] = token
            preferences[UserPreferences.IS_LOGGED_IN] = true
        }
    }


    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(UserPreferences.USER_TOKEN)
            preferences[UserPreferences.IS_LOGGED_IN] = false
        }
    }
}
