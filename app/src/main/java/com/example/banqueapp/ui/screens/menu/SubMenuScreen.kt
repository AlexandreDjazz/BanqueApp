package com.example.banqueapp.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.banqueapp.navigation.SubMenuDestinations
import com.example.banqueapp.ui.components.ButtonNewPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubMenuScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Menu") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ButtonNewPage(
                title = "Carte",
                subtitle = "Accéder à la carte",
                icon = Icons.Default.Place,
                onClick = {
                    navController.navigate(SubMenuDestinations.MAP)
                }
            )

            ButtonNewPage(
                title = "Debug",
                subtitle = "Outils de debug",
                icon = Icons.Default.PlayArrow,
                onClick = {
                    navController.navigate(SubMenuDestinations.DEBUG)
                }
            )
        }
    }
}
