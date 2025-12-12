package com.example.banqueapp.ui.screens.marches

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.banqueapp.domain.models.TransactionColor
import com.example.banqueapp.viewModels.MarchesViewModel

// MarchesScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarchesScreen(
    viewModel: MarchesViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val stocks by viewModel.stocks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStocks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cours du Marché") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn (
            modifier = Modifier.padding(innerPadding)
        ) {
            items(stocks) { stock ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stock.symbol, style = MaterialTheme.typography.headlineSmall)
                            Text(stock.price, style = MaterialTheme.typography.bodyLarge)
                        }
                        Text(
                            stock.change,
                            color = if (stock.isPositive) TransactionColor.GREEN.color else TransactionColor.RED.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
