package com.example.banqueapp.domain.models

import androidx.compose.ui.graphics.Color

enum class TransactionType {
    ADD,
    WITHDRAW
}

enum class TransactionColor(val color: Color, val gradientColors: List<Color>) {
    GREEN(
        Color(0xFF2E7D32),
        listOf(
            Color(0xFF2E7D32),
            Color(0xFF4CAF50),
            Color(0xFF66BB6A)
        )
    ),
    RED(
        Color(0xFFC62828),
        listOf(
            Color(0xFFC62828),
            Color(0xFFEF5350),
            Color(0xFFE57373)
        )
    )
}


open class Transaction(
    open val id: Int = 0,
    open val userId: Int,
    open val title: String,
    open val amount: Double,
    open val date: Long,
    open val virement: Boolean = false,
    open val type: TransactionType
)