package com.example.banqueapp.ui.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.Transaction
import java.util.Date
import java.util.Locale

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