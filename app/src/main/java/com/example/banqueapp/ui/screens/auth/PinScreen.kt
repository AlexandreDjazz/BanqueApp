package com.example.banqueapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.banqueapp.viewModels.UserViewModel
import kotlin.random.Random

@Composable
fun PinScreen(
    userViewModel: UserViewModel? = null,
    onPinSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Entrez votre code PIN", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(maxPinLength) { index ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (index < pin.length) Color.Black else Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        RandomPinKeyboard(onDigitClick = { digit ->
            if (pin.length < maxPinLength) pin += digit.toString()
        })

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }) {
                Text("⌫")
            }
            Button(
                onClick = {
                    if (userViewModel?.checkPin(pin) == true) {
                        onPinSuccess()
                    } else {
                        pin = ""
                    }
                }
            ) {
                Text("Valider")
            }
        }
        TextButton(onClick = onBack) {
            Text("Retour")
        }
    }
}

@Composable
fun RandomPinKeyboard(
    onDigitClick: (Int) -> Unit
) {
    val digits = remember { (0..9).toList().shuffled(Random(System.currentTimeMillis())) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            for (i in 0..4) PinButton(digits[i], onDigitClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            for (i in 5..9) PinButton(digits[i], onDigitClick)
        }
    }
}

@Composable
fun PinButton(digit: Int, onClick: (Int) -> Unit) {
    Button(
        onClick = { onClick(digit) },
        modifier = Modifier.size(60.dp)
    ) {
        Text(text = digit.toString(), fontSize = 24.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PinScreenPreview() {
    PinScreen(
        onPinSuccess = {},
        onBack = {}
    )
}