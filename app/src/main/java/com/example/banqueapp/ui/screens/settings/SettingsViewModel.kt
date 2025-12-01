package com.example.banqueapp.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application.applicationContext)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadThemePreference()
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            themePreferences.isDarkMode.collect { isDark ->
                _uiState.value = _uiState.value.copy(
                    themeMode = when (isDark) {
                        true -> ThemeMode.DARK
                        false -> ThemeMode.LIGHT
                        null -> ThemeMode.SYSTEM
                    }
                )
            }
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            when (themeMode) {
                ThemeMode.LIGHT -> themePreferences.setDarkMode(false)
                ThemeMode.DARK -> themePreferences.setDarkMode(true)
                ThemeMode.SYSTEM -> themePreferences.clearPreference()
            }
            _uiState.value = _uiState.value.copy(themeMode = themeMode)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }
}