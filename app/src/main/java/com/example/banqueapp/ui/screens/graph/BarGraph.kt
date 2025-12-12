import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.TransactionColor
import com.example.banqueapp.ui.screens.graph.DailyData
import com.example.banqueapp.ui.screens.graph.LegendItem
import com.example.banqueapp.ui.screens.graph.calculateDailyTransactions

@Composable
fun BarGraph(transactions: List<Transaction>) {
    val dailyData by remember(transactions) {
        derivedStateOf { calculateDailyTransactions(transactions) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Transactions par jour",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (dailyData.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune transaction")
            }
        } else {
            BarChart(data = dailyData)
        }
    }
}

@Composable
private fun BarChart(data: List<DailyData>) {
    val barWidth = 40.dp.value
    val maxValue = data.maxOfOrNull { it.total }?.toFloat() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val spacing = (size.width / data.size) * 0.9f
                val startX = spacing * 0.05f

                // On réserve une zone pour : haut (titre déjà en dehors), bas = dates + légende
                val legendHeight = 56.dp.toPx()          // hauteur de la Row de légende
                val datesHeight = 40.dp.toPx()           // hauteur de la bande pour les dates
                val bottomPadding = legendHeight + datesHeight + 16.dp.toPx() // marge bas

                val chartTopY = 32.dp.toPx()             // un peu de marge haut
                val chartBottomY = size.height - bottomPadding
                val chartHeight = chartBottomY - chartTopY

                // Échelle Y
                val steps = 5
                for (i in 0..steps) {
                    val y = chartTopY + (chartHeight * i / steps)
                    val value = ((steps - i) * maxValue / steps).toInt()

                    drawLine(
                        color = Color.Black.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.5f
                    )

                    drawTextLabel(
                        text = value.toString(),
                        x = 30f,
                        y = y + 8f,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontSize = 40f,
                        background = false
                    )
                }

                data.forEachIndexed { index, dayData ->
                    val x = startX + index * spacing

                    // Dépôts
                    val depositHeight = (dayData.deposits / maxValue) * chartHeight
                    drawRect(
                        color = TransactionColor.GREEN.color,
                        topLeft = Offset(x, chartBottomY - depositHeight),
                        size = Size(barWidth, depositHeight)
                    )
                    drawTextLabel(
                        text = dayData.deposits.toString(),
                        x = x + barWidth / 2,
                        y = chartBottomY - depositHeight - 12f,
                        color = TransactionColor.GREEN.color,
                        fontSize = 40f
                    )

                    // Retraits
                    val withdrawalHeight = (dayData.withdrawals / maxValue) * chartHeight
                    drawRect(
                        color = TransactionColor.RED.color,
                        topLeft = Offset(x + barWidth + 12f, chartBottomY - withdrawalHeight),
                        size = Size(barWidth, withdrawalHeight)
                    )
                    drawTextLabel(
                        text = dayData.withdrawals.toString(),
                        x = x + barWidth + 12f + barWidth / 2,
                        y = chartBottomY - withdrawalHeight - 12f,
                        color = TransactionColor.RED.color,
                        fontSize = 40f
                    )

                    val dateY = size.height - legendHeight - 8.dp.toPx()
                    drawVerticalText(
                        text = dayData.date,
                        x = x + barWidth + 6f + barWidth / 2,
                        y = dateY,
                        fontSize = 60f
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = TransactionColor.GREEN.color, label = "Dépôts")
                LegendItem(color = TransactionColor.RED.color, label = "Retraits")
            }
        }
    }
}

private fun DrawScope.drawVerticalText(
    text: String,
    x: Float,
    y: Float,
    fontSize: Float = 18f
) {
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.BLACK
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
        }
        canvas.nativeCanvas.save()
        canvas.nativeCanvas.translate(x, y)
        canvas.nativeCanvas.rotate(-90f)
        canvas.nativeCanvas.drawText(text, 0f, 0f, paint)
        canvas.nativeCanvas.restore()
    }
}


private fun DrawScope.drawTextLabel(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    fontSize: Float = 20f,
    background: Boolean = true
) {
    val textWidth = text.length * 10f
    val textHeight = fontSize + 8f

    if (background) {
        drawRoundRect(
            color = color.copy(alpha = 0.85f),
            topLeft = Offset(x - textWidth/2 - 10f, y - textHeight/2),
            size = Size(textWidth + 20f, textHeight + 4f),
            cornerRadius = CornerRadius(8f)
        )
    }

    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.WHITE
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
        canvas.nativeCanvas.drawText(text, x, y + fontSize/3, paint)
    }
}
