package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.banqueapp.viewModels.SettingsViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.navigation.Destinations
import com.example.banqueapp.ui.components.BottomBar
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState



@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel,
    onProfile: () -> Unit,
    onHome: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    rootNavController: NavHostController
) {

    Scaffold(
        bottomBar = {
            BottomBar(
                onProfile = onProfile,
                onHome = onHome,
                onSettings = onSettings
            )
        }
    ) { padding ->
        HomeContent(
            modifier = Modifier.padding(padding),
            userViewModel = userViewModel,
            transactionViewModel = transactionViewModel,
            onNavigateToMap = { rootNavController.navigate(Destinations.MAP) }
        )
    }
}


@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel,
    onNavigateToMap: () -> Unit
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
            Button(
                onClick = onNavigateToMap,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voir les ATM à proximité")
            }

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
