package com.example.banqueapp.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.ui.screens.utils.ErrorScreen
import com.example.banqueapp.viewModels.UserViewModel
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.VirementResult
import com.example.banqueapp.viewModels.VirementViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirementsScreen(
    virementViewModel: VirementViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val virements by virementViewModel.transactions.collectAsState()
    val virementResult by virementViewModel.virementResult.collectAsState()
    val user = (uiState as? UserUiState.LoggedIn)?.user
    val users by userViewModel.users.collectAsState()

    if(user == null){
        ErrorScreen("User is null")
        return
    }

    val otherUsers = remember(users, user) {
        users.filter { it.id != user.id }
    }

    var showVirementDialog by remember { mutableStateOf(false) }
    var titre by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(virementResult) {
        virementResult?.let { result ->
            val message = when (result) {
                is VirementResult.Success -> "Virement effectué !"
                is VirementResult.InsufficientFunds ->
                    "Solde insuffisant: ${result.balance}€ disponible"
                is VirementResult.InvalidAmount -> "Montant invalide"
                is VirementResult.UserNotFound -> "Utilisateur introuvable"
                is VirementResult.SameUser -> "Impossible de virer à soi-même"
                is VirementResult.Error -> result.message
            }
            snackbarHostState.showSnackbar(message)
            virementViewModel.clearVirementResult()
        }
    }

    LaunchedEffect(uiState) {
        virementViewModel.loadTransactions(user.id)
    }

    Surface {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Virements") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        val currentMonthVirements = virements.count { virement ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = virement.date
                            val virementMonth = calendar.get(Calendar.MONTH)
                            val virementYear = calendar.get(Calendar.YEAR)
                            val currentCalendar = Calendar.getInstance()
                            val currentMonth = currentCalendar.get(Calendar.MONTH)
                            val currentYear = currentCalendar.get(Calendar.YEAR)
                            virementMonth == currentMonth && virementYear == currentYear
                        }

                        Text(
                            "$currentMonthVirements Virements ce mois ci",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Total : ${virements.size} virements",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Button(
                    onClick = { showVirementDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Faire un virement")
                    }
                }

                if (virements.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucun virement",
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
                        items(virements) { virement ->
                            TransactionItem(transaction = virement)
                        }
                    }
                }
            }
        }
    }

    VirementDialog(
        showDialog = showVirementDialog,
        titre = titre,
        amount = amount,
        selectedUserId = selectedUserId,
        users = otherUsers,
        uiState = uiState,
        onDismiss = {
            showVirementDialog = false
            titre = ""
            amount = ""
            selectedUserId = null
        },
        onTitreChange = { titre = it },
        onAmountChange = { amount = it },
        onUserSelected = { selectedUserId = it },
        onConfirm = { toUserId, title, montant ->
            virementViewModel.makeVirement(user.id, toUserId, title, montant)
            showVirementDialog = false
            titre = ""
            amount = ""
            selectedUserId = null
        }
    )
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VirementDialog(
    showDialog: Boolean,
    titre: String,
    amount: String,
    selectedUserId: Int?,
    users: List<User>,
    uiState: UserUiState,
    onDismiss: () -> Unit,
    onTitreChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onUserSelected: (Int?) -> Unit,
    onConfirm: (Int, String, Double) -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau virement") },
        text = {
            Column {
                OutlinedTextField(
                    value = titre,
                    onValueChange = onTitreChange,
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Montant") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = "Sélectionner un utilisateur",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Destinataire") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (users.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Aucun utilisateur",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                enabled = false,
                                onClick = { }
                            )
                        } else {
                            users.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.name) },
                                    onClick = {
                                        onUserSelected(user.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val currentUserId = (uiState as? UserUiState.LoggedIn)?.user?.id ?: return@TextButton
                    val montant = amount.toDoubleOrNull() ?: return@TextButton

                    if (selectedUserId != null && titre.isNotBlank()) {
                        onConfirm(selectedUserId, titre, montant)
                    }
                }
            ) {
                Text("Envoyer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
