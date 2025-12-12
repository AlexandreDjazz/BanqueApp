package com.example.banqueapp.ui.screens.profile

import com.example.banqueapp.viewModels.UserViewModel
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.User
import com.example.banqueapp.ui.screens.utils.ErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: User?,
    onCancel: () -> Unit,
    userViewModel: UserViewModel
) {

    if(user == null){
        ErrorScreen("User is null")
        return
    }

    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier le profil") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Annuler")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ProfileHeader(user = user.copy(name = name, email = email))

            Spacer(modifier = Modifier.height(32.dp))

            ProfileSection(title = "Informations personnelles")

            EditProfileField(
                icon = Icons.Default.Person,
                label = "Nom complet",
                value = name,
                onValueChange = { name = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditProfileField(
                icon = Icons.Default.Email,
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditProfileField(
                icon = Icons.Default.Phone,
                label = "Téléphone",
                value = phone,
                onValueChange = { phone = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileActionButton(
                icon = Icons.Default.Done,
                text = "Enregistrer les modifications",
                onClick = {
                    scope.launch {
                        val success = userViewModel.updateProfile(name, email, phone)
                        snackbarHostState.showSnackbar(
                            if (success) "Profil mis à jour !" else "Erreur mise à jour"
                        )
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank()
            )
        }
    }
}

@Composable
private fun EditProfileField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}