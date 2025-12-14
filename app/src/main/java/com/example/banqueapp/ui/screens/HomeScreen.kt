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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.ui.components.BalanceCard
import com.example.banqueapp.ui.screens.transaction.TransactionItem
import com.example.banqueapp.ui.screens.transaction.TransactionsSection
import com.example.banqueapp.ui.screens.utils.ErrorScreen
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel,
    onSeeAllTransaction: () -> Unit,
    openGraph: () -> Unit,
) {
    val uiState by userViewModel.uiState.collectAsState()

    when (uiState) {
        is UserUiState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }

        is UserUiState.Error -> {
            ErrorScreen((uiState as UserUiState.Error).message)
        }

        is UserUiState.LoggedOut -> {
            Text("Non connecté")
        }

        is UserUiState.LoggedIn -> {
            HomeContent(
                modifier = modifier,
                user = (uiState as UserUiState.LoggedIn).user,
                transactionViewModel = transactionViewModel,
                onSeeAllTransaction = onSeeAllTransaction,
                openGraph = openGraph
            )
        }
        is UserUiState.SignUpSuccess -> {
        }
    }
}


@Composable
private fun HomeContent(
    modifier: Modifier,
    user: User,
    transactionViewModel: TransactionViewModel,
    onSeeAllTransaction: () -> Unit,
    openGraph: () -> Unit,
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
            IconButton(onClick = openGraph) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Graph",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        BalanceCard(balance = user.balance)

        Spacer(modifier = Modifier.height(32.dp))

        TransactionsSection(
            transactions = transactions,
            transactionViewModel = transactionViewModel,
            onSeeAllClick = onSeeAllTransaction
        )
    }
}
