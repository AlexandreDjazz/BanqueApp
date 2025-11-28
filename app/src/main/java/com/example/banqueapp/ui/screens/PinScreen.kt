package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Composable
fun PinScreen(
    navController: NavHostController,
    userId: String,
    onLoginPin: (String, String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Définir votre code PIN", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = { onLoginPin(userId, pin) }) {
                Text("Valider")
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Retour")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PinScreenPreview() {
    val navController = rememberNavController()
    PinScreen(
        navController = navController,
        userId = "",
        onLoginPin = { _, _-> }
    )
}
