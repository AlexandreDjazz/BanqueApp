package com.example.banqueapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.banqueapp.ui.main.MainScreen
import com.example.banqueapp.ui.screens.changepassword.ChangePasswordScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object ChangePassword : Screen("change_password")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    settingsViewModel: com.example.banqueapp.ui.screens.settings.SettingsViewModel,
    startDestination: String = Screen.Main.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                settingsViewModel = settingsViewModel,
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
