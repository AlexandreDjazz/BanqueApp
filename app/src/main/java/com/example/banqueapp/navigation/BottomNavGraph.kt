package com.example.banqueapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.banqueapp.ui.screens.HomeScreen
import com.example.banqueapp.ui.screens.debug.DebugMenuScreen
import com.example.banqueapp.ui.screens.debug.TransactionDebugScreen
import com.example.banqueapp.ui.screens.menu.MapScreen
import com.example.banqueapp.ui.screens.menu.SubMenuScreen
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.profile.SettingsScreen
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun BottomNavGraph(
    bottomNavController: NavHostController,
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel,
    innerPadding: PaddingValues,
    onLogout: () -> Unit
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
                onLogout = onLogout,
                userViewModel = userViewModel
            )
        }


        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { bottomNavController.popBackStack() },
                onNavigateToChangePassword = {},
                viewModel = settingsViewModel
            )
        }

        composable(Destinations.SUB_MENU) {
            SubMenuScreen(
                navController = bottomNavController,
            )
        }

        composable(SubMenuDestinations.MAP) {
            MapScreen(
                onNavigateBack = { bottomNavController.navigateUp() },
                showBackButton = true
            )
        }






        // ------------------------- // DEBUG SYSTEM

        composable(SubMenuDestinations.DEBUG) {
            DebugMenuScreen(
                navController = bottomNavController,
                onBack = { bottomNavController.navigateUp() }
            )
        }

        composable(DebugMenuDestinations.TRANSACTION) {
            TransactionDebugScreen(
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel,
                onBack = { bottomNavController.navigateUp() }
            )
        }
    }
}
