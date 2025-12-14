package com.example.banqueapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banqueapp.navigation.BottomNavGraph
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.navigation.Destinations
import com.example.banqueapp.ui.theme.BanqueAppTheme
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.VirementViewModel

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavOverlay(
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel,
    virementViewModel: VirementViewModel,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Destinations.PROFILE, Icons.Default.AccountCircle, "Profil"),
        BottomNavItem(Destinations.HOME, Icons.Default.Home, "Accueil"),
        BottomNavItem(Destinations.SUB_MENU, Icons.Default.Menu, "Menu")
    )

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                items = bottomNavItems,
                currentDestinationRoute = currentDestination?.route,
                onItemClick = { route ->
                    bottomNavController.navigate(route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {}
                        launchSingleTop = true
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
    ) { innerPadding ->

        BottomNavGraph(
            bottomNavController = bottomNavController,
            userViewModel = userViewModel,
            settingsViewModel = settingsViewModel,
            transactionViewModel = transactionViewModel,
            virementViewModel = virementViewModel,
            innerPadding = innerPadding,
            onLogout = onLogout
        )
    }
}

@Composable
fun CustomBottomBar(
    items: List<BottomNavItem>,
    currentDestinationRoute: String?,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.padding(bottom = 23.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 30.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = currentDestinationRoute == item.route
                val tintColor = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

                if (item.route == Destinations.HOME) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(tintColor),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { onItemClick(item.route) }) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                } else {
                    IconButton(onClick = { onItemClick(item.route) }) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(35.dp),
                            tint = tintColor
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomBottomBarPreview() {
    val items = listOf(
        BottomNavItem(Destinations.PROFILE, Icons.Default.AccountCircle, "Profil"),
        BottomNavItem(Destinations.HOME, Icons.Default.Home, "Accueil"),
        BottomNavItem(Destinations.SETTINGS, Icons.Default.Settings, "Paramètres")
    )

    BanqueAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            CustomBottomBar(
                items = items,
                currentDestinationRoute = Destinations.HOME,
                onItemClick = {}
            )
        }
    }
}
