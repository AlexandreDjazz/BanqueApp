package com.example.banqueapp.ui.screens.debug

import android.icu.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.NotificationHelper
import com.example.banqueapp.ui.components.TransactionItem
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.VirementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirementDebugScreen(
    virementViewModel: VirementViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }

    val uiState by userViewModel.uiState.collectAsState()
    val transactions by virementViewModel.transactions.collectAsState()
    val currentState = uiState

    val context = LocalContext.current

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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Date des transactions",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                selectedDate.formatDate(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Changer date"
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
                                title = if (isAdd) "Virement : Ajout" else "Virement : Retrait",
                                amount = amount,
                                date = selectedDate
                            )
                            userViewModel.updateBalance(userId, amount)
                            NotificationHelper.send(context, "Virement", "Nouveau virement de $amount sur votre compte")
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
                            userViewModel.reload()
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

    DatePickerDialog(
        showDialog = showDatePicker,
        initialDate = selectedDate,
        onDismiss = { showDatePicker = false },
        onDateSelected = { newDate ->
            selectedDate = newDate
            showDatePicker = false
        }
    )
}

fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

// ✅ Composant DatePickerDialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    showDialog: Boolean,
    initialDate: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    if (!showDialog) return

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner une date") },
        text = { DatePicker(state = datePickerState) },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { dateMillis ->
                    onDateSelected(dateMillis)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}