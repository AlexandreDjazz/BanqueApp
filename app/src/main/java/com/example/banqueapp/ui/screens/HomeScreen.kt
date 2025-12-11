package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.ui.components.TransactionItem
import com.example.banqueapp.ui.screens.utils.ErrorScreen
import com.example.banqueapp.ui.theme.BanqueAppTheme
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState
import kotlin.random.Random


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    balance: String = "0,00 €",
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel
) {
    val uiState by userViewModel.uiState.collectAsState()

    when (val currentState = uiState) {
        is UserUiState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        is UserUiState.LoggedOut -> {
            Text("Non connecté")
        }
        is UserUiState.Error -> {
            ErrorScreen(currentState.message)
        }
        is UserUiState.LoggedIn -> {
            HomeContent(
                modifier = modifier,
                user = currentState.user,
                balance = balance,
                transactionViewModel= transactionViewModel
            )
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    user: User,
    balance: String,
    transactionViewModel: TransactionViewModel,
) {
    val transactions by transactionViewModel.transactions.collectAsState()

    LaunchedEffect(user.id) {
        transactionViewModel.loadTransactions(user.id)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                Icons.Default.Person,
                contentDescription = "Compte",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Solde disponible",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = balance,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "€",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.offset(y = (-8).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions récentes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {}) {
                        Text("Voir tout")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions.take(5)) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = {
                                transactionViewModel.deleteTransaction(transaction.id, transaction.userId)
                            }
                        )
                    }
                }
            }
        }
    }
}