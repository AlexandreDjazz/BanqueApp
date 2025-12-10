package com.example.banqueapp.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object UserPreferences {
    val USER_ID = intPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}
