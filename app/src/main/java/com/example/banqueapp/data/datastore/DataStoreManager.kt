package com.example.banqueapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore



private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {


}
