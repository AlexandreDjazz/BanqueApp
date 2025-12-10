package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.settings.SettingsScreen
import com.example.banqueapp.ui.screens.settings.SettingsViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.navigation.Destinations

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun MainOverlay(
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit,
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Destinations.PROFILE, Icons.Default.AccountCircle, "Profil"),
        BottomNavItem(Destinations.HOME, Icons.Default.Home, "Accueil"),
        BottomNavItem(Destinations.SETTINGS, Icons.Default.Settings, "Paramètres")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Destinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.HOME) {
                HomeScreen(
                    userViewModel = userViewModel
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
            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { bottomNavController.navigateUp() },
                    onNavigateToChangePassword = { /* TODO */ },
                    viewModel = settingsViewModel
                )
            }
        }
    }
}