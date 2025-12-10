package com.example.banqueapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel? = null,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

            Button(onClick = {
                userViewModel?.login(email, password) { success ->
                    if (success) onLoginSuccess()
                }
            }) {
                Text("Se connecter")
            }

            TextButton(onClick = onBack) {
                Text("Retour")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onBack = {}
    )
}