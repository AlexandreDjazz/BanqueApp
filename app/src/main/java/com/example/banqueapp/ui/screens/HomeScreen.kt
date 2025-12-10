package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banqueapp.viewModels.UserViewModel

data class Transaction(
    val id: Int,
    val description: String,
    val amount: String,
    val date: String
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String = "",
    balance: String = "0,00 €",
    transactions: List<Transaction> = emptyList(),
    userViewModel: UserViewModel? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // En-tête
        Text(
            text = "Bonjour, $userName",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Carte solde
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Solde du compte", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    balance,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Historique des transactions 💳",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight(0.75f)
        ) {
            items(transactions) { transaction ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(transaction.description, fontSize = 16.sp)
                            Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(
                            transaction.amount,
                            fontSize = 16.sp,
                            color = if (transaction.amount.contains("+")) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BankHomeScreenPreview() {
    val sampleTransactions = listOf(
        Transaction(1, "Paiement supermarché", "-45,20 €", "27/11/2025"),
        Transaction(2, "Salaire", "+2000,00 €", "25/11/2025"),
        Transaction(3, "Abonnement Netflix", "-12,99 €", "24/11/2025"),
        Transaction(4, "Cadeau", "+50,00 €", "23/11/2025")
    )

    HomeScreen(
        userName = "Alice Dupont",
        balance = "1992,81 €",
        transactions = sampleTransactions
    )
}
