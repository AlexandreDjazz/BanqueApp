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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.banqueapp.ui.screens.transaction.TransactionItem
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.VirementViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirementDebugScreen(
    virementViewModel: VirementViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val transactions by virementViewModel.transactions.collectAsState()
    val currentState = uiState

    LaunchedEffect(currentState) {
        if (currentState is UserUiState.LoggedIn) {
            virementViewModel.loadTransactions(currentState.user.id)
        }
    }

    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Debug Virement") },
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
                            "Virement : ${transactions.size}",
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val isAdd = Random.nextBoolean()
                            val userId = (uiState as? UserUiState.LoggedIn)?.user?.id ?: 0
                            val amount = if (isAdd) 100.0 else -100.0
                            virementViewModel.addTransaction(
                                userId = userId,
                                title = if (isAdd) "Virement +" else "Virement -",
                                amount = amount
                            )
                            userViewModel.updateBalance(userId, amount)
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
                            virementViewModel.clearUserTransactions(userId)
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { transactionId ->
                                val userId = (uiState as? UserUiState.LoggedIn)?.user?.id ?: 0
                                virementViewModel.deleteTransaction(
                                    transactionId = transactionId,
                                    userId = userId
                                )
                            },
                            isAdminView = true
                        )
                    }
                }
            }
        }
    }
}