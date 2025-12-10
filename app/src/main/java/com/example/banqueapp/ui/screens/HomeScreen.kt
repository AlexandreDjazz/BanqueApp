package com.example.banqueapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    userViewModel: UserViewModel? = null,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bonjour $userName", style = MaterialTheme.typography.headlineSmall)

        }

        Spacer(modifier = Modifier.height(24.dp))

        // Solde
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Text("Solde du compte", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Text(balance, fontSize = 28.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text("Historique des transactions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF2F2F2))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(transaction.description, fontSize = 16.sp)
                        Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(transaction.amount, fontSize = 16.sp)
                }
            }

        }
        Button(onClick = {
            userViewModel?.logout { onLogout() }
        }) {
            Text("Déconnexion")
        }
    }
}

@Preview (showBackground = true)
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
        balance = "1992,81€",
        transactions = sampleTransactions,
        onLogout = {}
    )
}

