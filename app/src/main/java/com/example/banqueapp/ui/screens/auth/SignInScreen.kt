package com.example.banqueapp.ui.screens.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel
import androidx.compose.ui.res.painterResource
import com.example.banqueapp.R

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onSignInSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var pinVisible by remember { mutableStateOf(false) }
    val uiState by userViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UserUiState.SignUpSuccess) {
            onSignInSuccess()
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
            Text("Inscription", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                singleLine = true
            )
            Text(
                text = "2 à 20 lettres, pas d'espaces ni chiffres",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
            )
            Text(
                text = "Exemple : exemple@mail.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible)
                                    R.drawable.baseline_visibility_off_24
                                else
                                    R.drawable.baseline_visibility_24
                            ),
                            contentDescription = null
                        )
                    }
                }

            )
            Text(
                text = "6 à 16 caractères, au moins une majuscule et un caractère spécial",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Code PIN") },
                singleLine = true,
                visualTransformation =
                    if (pinVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { pinVisible = !pinVisible }) {
                        Icon(
                            painter = painterResource(
                                if (pinVisible)
                                    R.drawable.baseline_visibility_off_24
                                else
                                    R.drawable.baseline_visibility_24
                            ),
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )


            Text(
                text = "6 chiffres",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Téléphone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Text(
                text = "10 chiffres",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    userViewModel.onSignUp(
                        name,
                        email,
                        phone,
                        password,
                        pin
                    ) { _, _ -> }
                },
                enabled = uiState !is UserUiState.Loading
            ) {
                Text("S'inscrire")
            }

            if (uiState is UserUiState.Error) {
                Text(
                    text = (uiState as UserUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            TextButton(onClick = onBack) {
                Text("Retour")
            }
        }
    }
}

