package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun PinScreen(
    navController: NavController,
    onPinEntered: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Entrez votre code PIN", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Code PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = { onPinEntered(pin) }) {
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
    PinScreen(navController = navController, onPinEntered = {})
}
