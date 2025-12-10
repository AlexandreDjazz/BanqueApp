package com.example.banqueapp.domain.models

data class Transaction(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val amount: Double,
    val date: Long
)