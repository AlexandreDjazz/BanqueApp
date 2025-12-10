package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.navigation.Destinations
import com.example.banqueapp.ui.components.BottomBar
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
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
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
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
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Transactions récentes",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onDelete = { transactionId ->
                        transactionViewModel.deleteTransaction(
                            userId = user.id,
                            transactionId = transactionId,
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        transactionViewModel.addTransaction(
                            userId = user.id,
                            title = "Dépôt",
                            amount = 100.0
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ajouter transaction")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}



@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: (Int) -> Unit
) {
    val formattedDate = remember(transaction.date) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(transaction.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.title, style = MaterialTheme.typography.titleMedium)
                Text("${transaction.amount} €", style = MaterialTheme.typography.bodyLarge)
                Text(formattedDate, style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { onDelete(transaction.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer transaction",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
