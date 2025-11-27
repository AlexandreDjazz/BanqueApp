package com.example.banqueapp.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferences {
    val USER_TOKEN = stringPreferencesKey("user_token")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}
