package com.example.banqueapp.ui.screens.graph

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.TransactionColor
import com.example.banqueapp.domain.models.TransactionType
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun CamembertChart(transactions: List<Transaction>) {
    val monthlyData by remember(transactions) {
        derivedStateOf { calculateMonthlyDepositsWithdrawals(transactions) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Répartition mensuelles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (monthlyData.totalTransactions == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune transaction ce mois")
            }
        } else {
            CamembertChartGraph(data = monthlyData)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = TransactionColor.GREEN.color,
                    label = "Dépôts (${monthlyData.deposits})"
                )
                LegendItem(
                    color = TransactionColor.RED.color,
                    label = "Retraits (${monthlyData.withdrawals})"
                )
            }
        }
    }
}

data class DailyData(val date: String, val deposits: Int, val withdrawals: Int, val total: Int)
data class MonthlyData(val deposits: Int, val withdrawals: Int, val totalTransactions: Int)

fun calculateDailyTransactions(transactions: List<Transaction>): List<DailyData> {
    return transactions
        .groupBy {
            SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(it.date))
        }
        .map { (date, txs) ->
            val deposits = txs.count { it.type == TransactionType.ADD }
            val withdrawals = txs.count { it.type == TransactionType.WITHDRAW }
            DailyData(date, deposits, withdrawals, txs.size)
        }
        .sortedBy { SimpleDateFormat("dd/MM", Locale.getDefault()).parse(it.date)?.time ?: 0 }
        .takeLast(7)
}

fun calculateMonthlyDepositsWithdrawals(transactions: List<Transaction>): MonthlyData {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val monthTransactions = transactions.filter { transaction ->
        val txCalendar = Calendar.getInstance().apply { timeInMillis = transaction.date }
        txCalendar.get(Calendar.MONTH) == currentMonth &&
                txCalendar.get(Calendar.YEAR) == currentYear
    }

    return MonthlyData(
        deposits = monthTransactions.count { it.type == TransactionType.ADD },
        withdrawals = monthTransactions.count { it.type == TransactionType.WITHDRAW },
        totalTransactions = monthTransactions.size
    )
}

@Composable
private fun CamembertChartGraph(data: MonthlyData) {

    Card(
        modifier = Modifier.size(250.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 40f

            val depositsAngle = (data.deposits.toFloat() / data.totalTransactions) * 360f
            val withdrawalsAngle = 360f - depositsAngle

            // Dépôts (vert)
            drawArc(
                brush = Brush.radialGradient(TransactionColor.GREEN.gradientColors),
                startAngle = 0f,
                sweepAngle = depositsAngle,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                useCenter = true
            )

            // Retraits (rouge)
            drawArc(
                brush = Brush.radialGradient(TransactionColor.RED.gradientColors),
                startAngle = 0f + depositsAngle,
                sweepAngle = withdrawalsAngle,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                useCenter = true
            )
        }
    }
}