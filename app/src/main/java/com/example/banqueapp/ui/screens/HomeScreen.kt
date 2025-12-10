package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.navigation.Destinations
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit,
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

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
                                popUpTo(bottomNavController.graph.startDestinationId) {
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
                HomeContent(
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
                    onNavigateToChangePassword = {},
                    viewModel = settingsViewModel
                )
            }
        }
    }
}


@Composable
fun HomeContent(
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel
) {
    val uiState = userViewModel.uiState.collectAsState().value
    val user = (uiState as? UserUiState.LoggedIn)?.user
    val transactions by transactionViewModel.transactions.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            transactionViewModel.loadTransactions(user.id)
        }
    }

    if (user == null) {
        Text("Utilisateur non connecté")
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bonjour, ${user.name}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transactions récentes",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }

            Button(onClick = {
                transactionViewModel.addTransaction(
                    userId = user.id,
                    title = "Dépôt",
                    amount = 100.0
                )
            }) {
                Text("Ajouter transaction")
            }

        }
    }
}


@Composable
fun TransactionItem(transaction: com.example.banqueapp.domain.models.Transaction) {
    val formattedDate = remember(transaction.date) {
        java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(transaction.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(transaction.title, style = MaterialTheme.typography.titleMedium)
            Text("${transaction.amount} €", style = MaterialTheme.typography.bodyLarge)
            Text(formattedDate, style = MaterialTheme.typography.bodySmall)
        }
    }
}
