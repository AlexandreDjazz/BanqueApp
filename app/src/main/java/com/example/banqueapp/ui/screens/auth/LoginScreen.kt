package com.example.banqueapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by userViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UserUiState.LoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Connexion", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    userViewModel.onLogin(email, password) {}
                },
                enabled = uiState !is UserUiState.Loading
            ) {
                Text("Se connecter")
            }

            if (uiState is UserUiState.Error) {
                Text(
                    text = (uiState as UserUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            TextButton(onClick = onBack) {
                Text("Retour")
            }
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onBack = {}
    )
}*/