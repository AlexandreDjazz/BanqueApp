package com.example.banqueapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.ui.screens.profile.ProfileScreen
import com.example.banqueapp.ui.screens.settings.SettingsScreen
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.navigation.Destinations
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.TransactionViewModel

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun MainOverlay(
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel,
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
            CustomBottomBar(
                items = bottomNavItems,
                currentDestinationRoute = currentDestination?.route,
                onItemClick = { route ->
                    bottomNavController.navigate(route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
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

@Composable
fun CustomBottomBar(
    items: List<BottomNavItem>,
    currentDestinationRoute: String?,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 30.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFFE6D3F8)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = currentDestinationRoute == item.route
                val colorSelected = if (isSelected) Color(0xFF4A0CF8) else Color(0xFFAF97FF)

                if (item.route == Destinations.HOME) {
                    // Central circular button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorSelected),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { onItemClick(item.route) }) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                } else {
                    // Regular buttons
                    IconButton(onClick = { onItemClick(item.route) }) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(35.dp),
                            tint = colorSelected
                        )
                    }
                }
            }
        }
    }
}