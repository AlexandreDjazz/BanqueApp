package com.example.banqueapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.banqueapp.ui.screens.changepassword.ChangePasswordScreen
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object ChangePassword : Screen("change_password")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    settingsViewModel: com.example.banqueapp.ui.screens.settings.SettingsViewModel,
    startDestination: String = Screen.Profile.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                viewModel = settingsViewModel
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
