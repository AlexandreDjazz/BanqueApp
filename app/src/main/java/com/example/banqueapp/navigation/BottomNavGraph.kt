package com.example.banqueapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.banqueapp.ui.screens.HomeScreen
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.settings.SettingsScreen
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun BottomNavGraph(
    bottomNavController: NavHostController,
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = bottomNavController,
        startDestination = Destinations.HOME,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                userViewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        composable(Destinations.PROFILE) {
            ProfileScreen(
                onNavigateToSettings = {
                    bottomNavController.navigate(Destinations.SETTINGS)
                },
                userViewModel = userViewModel
            )
        }

        composable(Destinations.SUB_MENU) {
            SubMenuNavGraph(navController = bottomNavController)
        }

        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { bottomNavController.popBackStack() },
                onNavigateToChangePassword = {},
                viewModel = settingsViewModel
            )
        }
    }
}
