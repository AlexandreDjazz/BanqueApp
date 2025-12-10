package com.example.banqueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.banqueapp.data.datastore.DataStoreManager
import com.example.banqueapp.data.db.DatabaseProvider
import com.example.banqueapp.data.repository.UserRepositoryImpl
import com.example.banqueapp.navigation.AppNavGraph
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.settings.SettingsViewModel
import com.example.banqueapp.ui.screens.settings.ThemeMode
import com.example.banqueapp.ui.theme.BanqueAppTheme
import com.example.banqueapp.viewModels.UserViewModel

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(applicationContext)

        val userRepository = UserRepositoryImpl(db.userDao())
        val dataStoreManager = DataStoreManager(applicationContext)

        userViewModel = UserViewModel(userRepository, dataStoreManager)
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
        AppNavGraph(
            navController = navController,
            settingsViewModel = settingsViewModel
        )
    }
}