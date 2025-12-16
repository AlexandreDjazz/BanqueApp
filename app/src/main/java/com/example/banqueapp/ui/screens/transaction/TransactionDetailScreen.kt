package com.example.banqueapp.ui.screens.transaction


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.banqueapp.viewModels.TransactionViewModel
import com.example.banqueapp.domain.models.TransactionType
import com.example.banqueapp.domain.models.TransactionColor
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    transactionViewModel: TransactionViewModel,
    onBackClick: () -> Unit
) {
    val transaction by transactionViewModel.transactions
        .map { list -> list.find { it.id == transactionId } }
        .collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail d'une transaction") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (transaction == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Transaction introuvable")
                Button(onClick = onBackClick) {
                    Text("Retour")
                }
            }
        } else {
            val bgColor = when (transaction!!.type) {
                TransactionType.ADD -> TransactionColor.GREEN.color
                TransactionType.WITHDRAW -> TransactionColor.RED.color
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {


                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = transaction!!.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Montant : ${transaction!!.amount} €",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text("Type : ${transaction!!.type}", fontSize = 16.sp, color = Color.White)
                        Text(
                            "Date : ${
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                                    transaction!!.date
                                )
                            }",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text("User ID : ${transaction!!.userId}", fontSize = 14.sp, color = Color.White)
                        Text(
                            "Virement : ${transaction!!.virement}",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Button(onClick = onBackClick) {
                            Text("Retour")
                        }
                    }
                }
            }
        }
    }
}
