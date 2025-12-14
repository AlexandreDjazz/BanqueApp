package com.example.banqueapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.banqueapp.domain.models.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val amount: Double,
    val date: Long,
    val type: TransactionType,
    val virement: Boolean = false
)