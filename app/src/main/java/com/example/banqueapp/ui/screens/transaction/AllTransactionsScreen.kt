package com.example.banqueapp.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.ui.components.SearchBar
import com.example.banqueapp.ui.components.TransactionItem
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.UserUiState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val currentState = uiState

    var searchQuery by remember { mutableStateOf("") }

    val filteredTransactions = remember(searchQuery, transactions) {
        if (searchQuery.isBlank()) {
            transactions
        } else {
            transactions.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.amount.toString().contains(searchQuery)
            }
        }
    }

    LaunchedEffect(currentState) {
        if (currentState is UserUiState.LoggedIn) {
            transactionViewModel.loadTransactions(currentState.user.id)
        }
    }

    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Toutes les transactions") },
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
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = { searchQuery = it },
                )
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
                        val currentMonthTransactions = filteredTransactions.count { transaction ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = transaction.date
                            val transactionMonth = calendar.get(Calendar.MONTH)
                            val transactionYear = calendar.get(Calendar.YEAR)

                            val currentCalendar = Calendar.getInstance()
                            val currentMonth = currentCalendar.get(Calendar.MONTH)
                            val currentYear = currentCalendar.get(Calendar.YEAR)

                            transactionMonth == currentMonth && transactionYear == currentYear
                        }

                        Text(
                            "$currentMonthTransactions Transactions ce mois ci",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            "Total : ${filteredTransactions.size} transactions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                if (filteredTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucune transaction",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredTransactions) { transaction ->
                            TransactionItem(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}