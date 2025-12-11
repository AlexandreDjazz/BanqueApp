package com.example.banqueapp.domain.models

enum class TransactionType {
    ADD,
    WITHDRAW
}

data class Transaction(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val amount: Double,
    val date: Long,
    val type: TransactionType
)