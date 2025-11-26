package com.example.banqueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenue sur Bank",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onLoginClick) {
                Text("Se connecter")
            }
            Button(onClick = onSignInClick) {
                Text("S'inscrire")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onLoginClick = {},
        onSignInClick = {}
    )
}