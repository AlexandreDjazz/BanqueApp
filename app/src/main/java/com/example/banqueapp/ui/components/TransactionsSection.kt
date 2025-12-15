package com.example.banqueapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.Transaction

@Composable
fun TransactionsSection(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    maxTransactionView: Int? = 5,
    onOpenTransaction: (Int) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
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
            TextButton(onClick = onSeeAllClick) {
                Text("Voir tout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if(maxTransactionView != null) {
                items(transactions.take(maxTransactionView)) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            onOpenTransaction(transaction.id)
                        }
                    )
                }
            } else {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            onOpenTransaction(transaction.id)
                        }
                    )
                }
            }
        }
    }
}
