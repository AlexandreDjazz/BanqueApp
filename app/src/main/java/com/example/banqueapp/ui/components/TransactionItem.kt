package com.example.banqueapp.ui.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.TransactionColor
import com.example.banqueapp.domain.models.TransactionType
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: (() -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    isAdminView: Boolean = false
) {
    val formattedDate = remember(transaction.date) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(transaction.date))
    }

    val typeIcon = when (transaction.type) {
        TransactionType.ADD -> Icons.Default.KeyboardArrowUp
        TransactionType.WITHDRAW -> Icons.Default.KeyboardArrowDown
    }

    val amountColor = when (transaction.type) {
        TransactionType.ADD -> TransactionColor.GREEN.color
        TransactionType.WITHDRAW -> TransactionColor.RED.color
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = typeIcon,
                    contentDescription = null,
                    tint = amountColor,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Column {
                    Text(
                        transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${transaction.amount} €",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            if(isAdminView && onDelete != null) {
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
}
