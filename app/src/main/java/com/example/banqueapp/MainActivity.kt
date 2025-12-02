package com.example.banqueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.data.ThemePreferences
import com.example.banqueapp.navigation.NavGraph
import com.example.banqueapp.ui.screens.settings.SettingsViewModel
import com.example.banqueapp.ui.screens.settings.ThemeMode
import com.example.banqueapp.ui.theme.BanqueAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanqueApp()
        }
    }
}

@Composable
fun BanqueApp() {
    val settingsViewModel: SettingsViewModel = viewModel()
    val uiState by settingsViewModel.uiState.collectAsState()
    val systemInDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (uiState.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
    }

    BanqueAppTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        NavGraph(
            navController = navController,
            settingsViewModel = settingsViewModel
        )
    }
}