package com.example.banqueapp.ui.screens.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.UserUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDebugScreen(
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val currentState = uiState

    LaunchedEffect(currentState) {
        if (currentState is UserUiState.LoggedIn) {
            transactionViewModel.loadTransactions(currentState.user.id)
        }
    }

    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Debug Transactions") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Retour"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header avec stats
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Transactions : ${transactions.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "User ID : ${(uiState as? UserUiState.LoggedIn)?.user?.id ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                // Boutons d'action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val isAdd = kotlin.random.Random.nextBoolean()
                            val userId = (uiState as? UserUiState.LoggedIn)?.user?.id ?: 0
                            transactionViewModel.addTransaction(
                                userId = userId,
                                title = if (isAdd) "Dépôt" else "Retrait",
                                amount = if (isAdd) 100.0 else -100.0
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter")
                    }

                    Button(
                        onClick = {
                            val userId = (uiState as? UserUiState.LoggedIn)?.user?.id ?: 0
                            transactionViewModel.clearUserTransactions(userId)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vider tout")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Liste des transactions
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        transaction.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "ID: ${transaction.id} | ${transaction.amount}€",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val userId = (uiState as? UserUiState.LoggedIn)?.user?.id ?: 0
                                        transactionViewModel.deleteTransaction(
                                            transactionId = transaction.id,
                                            userId = userId
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
