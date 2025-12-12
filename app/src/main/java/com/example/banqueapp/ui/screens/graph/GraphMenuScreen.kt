package com.example.banqueapp.ui.screens.graph

import BarGraph
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.TransactionType
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.viewModels.UserUiState
import com.example.banqueapp.viewModels.UserViewModel
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphMenuScreen(
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val uiState by userViewModel.uiState.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val user = (uiState as? UserUiState.LoggedIn)?.user

    LaunchedEffect(user?.id) {
        user?.let { transactionViewModel.loadTransactions(it.id) }
    }

    val tabs = listOf("Transactions par jour", "Répartition mensuelle")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Graphiques") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(label) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> BarGraph(transactions = transactions)
                1 -> CamembertChart(transactions = transactions)
                else -> Text("Graphique non implémenté")
            }
        }
    }
}