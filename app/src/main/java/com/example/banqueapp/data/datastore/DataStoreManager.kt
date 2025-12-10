package com.example.banqueapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    val currentUserFlow: Flow<CurrentUser?> =
        context.dataStore.data
            .catch { e ->
                throw e
            }
            .map { prefs ->
                val isLoggedIn = prefs[UserPreferences.IS_LOGGED_IN] ?: false
                if (!isLoggedIn) null
                else CurrentUser(
                    id = prefs[UserPreferences.USER_ID] ?: 0
                )
            }

    suspend fun saveUser(id: Int, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[UserPreferences.USER_ID] = id
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(UserPreferences.USER_ID)
        }
    }
}

data class CurrentUser(
    val id: Int
)
