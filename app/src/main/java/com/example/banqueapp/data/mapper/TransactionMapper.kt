package com.example.banqueapp.data.mapper

import com.example.banqueapp.data.db.entities.TransactionEntity
import com.example.banqueapp.domain.models.Transaction


object TransactionMapper {
    fun toDomain(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            amount = entity.amount,
            date = entity.date
        )
    }

    fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            id = transaction.id,
            userId = transaction.userId,
            title = transaction.title,
            amount = transaction.amount,
            date = transaction.date
        )
    }
}
