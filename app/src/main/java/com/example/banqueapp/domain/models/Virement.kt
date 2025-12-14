package com.example.banqueapp.domain.models

data class Virement(
    override val id: Int = 0,
    override val userId: Int,
    override val title: String,
    override val amount: Double,
    override val date: Long,
    override val virement: Boolean = true,
    override val type: TransactionType
) : Transaction(
    id = id,
    userId = userId,
    title = title,
    amount = amount,
    date = date,
    virement = true,
    type = type
)