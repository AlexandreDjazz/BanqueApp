package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banqueapp.ui.screens.utils.ErrorScreen
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel

data class Transaction(
    val id: Int,
    val description: String,
    val amount: String,
    val date: String,
    val category: String = ""
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    balance: String = "0,00 €",
    transactions: List<Transaction> = emptyList(),
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
                userName = currentState.user.name,
                balance = balance,
                transactions = transactions
            )
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    userName: String,
    balance: String,
    transactions: List<Transaction>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header personnalisé
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = userName,
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

        // Carte solde principale - Design amélioré
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

        // Quick actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionCard(
                icon = Icons.Default.Info,
                title = "Carte",
                subtitle = "Gérer mes cartes",
                modifier = Modifier.weight(1f)
            )
            ActionCard(
                icon = Icons.Default.Info,
                title = "Virements",
                subtitle = "Envoyer de l'argent",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Transactions récentes
        Column {
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
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Montant avec couleur dynamique
            val isPositive = transaction.amount.contains("+") || transaction.amount.startsWith("Remb")
            Text(
                text = transaction.amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.error,
                textAlign = TextAlign.End
            )
        }
    }
}

/*
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    val sampleTransactions = listOf(
        Transaction(1, "Carrefour Market", "-45,20 €", "Aujourd'hui 14:32"),
        Transaction(2, "Salaire Octobre", "+2 000,00 €", "25/11/2025 10:15"),
        Transaction(3, "Netflix Premium", "-12,99 €", "24/11/2025 02:00"),
        Transaction(4, "Remboursement Amazon", "+50,00 €", "23/11/2025 16:45"),
        Transaction(5, "McDonald's", "-18,50 €", "22/11/2025 19:20")
    )

    MaterialTheme {
        HomeScreen(
            balance = "1 992,81",
            transactions = sampleTransactions,
            userViewModel = null
        )
    }
}
*/
