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
import com.example.banqueapp.viewModels.UserViewModel

@Composable
fun PinScreen(
    navController: NavHostController,
    onPinSet: (String) -> Unit
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
            Button(onClick = { onPinSet(pin) }) {
                Text("Valider")
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
        onPinSet = {}
    )
}
