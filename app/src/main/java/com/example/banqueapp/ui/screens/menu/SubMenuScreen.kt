package com.example.banqueapp.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.banqueapp.navigation.SubMenuDestinations
import com.example.banqueapp.ui.components.ButtonNewPage

@Composable
fun SubMenuScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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

        /*ButtonNewPage(
            title = "Debug",
            subtitle = "Outils de debug",
            icon = Icons.Default.PlayArrow,
            onClick = {
                navController.navigate(SubMenuDestinations.DEBUG)
            }
        )*/
    }
}
