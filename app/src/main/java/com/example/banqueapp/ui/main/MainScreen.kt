package com.example.banqueapp.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.map.MapScreen
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.settings.SettingsScreen
import com.example.banqueapp.ui.screens.settings.SettingsViewModel

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Profile : BottomNavScreen("profile", "Profil", Icons.Default.AccountCircle)
    data object Map : BottomNavScreen("map", "ATM", Icons.Default.Place)
    data object Settings : BottomNavScreen("settings", "Paramètres", Icons.Default.Settings)
}

@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateToChangePassword: () -> Unit
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavScreen.Profile,
        BottomNavScreen.Map,
        BottomNavScreen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(BottomNavScreen.Settings.route)
                    }
                )
            }

            composable(BottomNavScreen.Map.route) {
                MapScreen(
                    onNavigateBack = {
                    }
                )
            }

            composable(BottomNavScreen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    viewModel = settingsViewModel
                )
            }
        }
    }
}
