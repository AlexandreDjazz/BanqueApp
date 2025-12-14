package com.example.banqueapp.data.mapper

import com.example.banqueapp.data.db.entities.TransactionEntity
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.Virement


object VirementMapper {
    fun toDomain(entity: TransactionEntity): Virement {
        return Virement(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            amount = entity.amount,
            date = entity.date,
            virement = entity.virement,
            type = entity.type
        )
    }

    fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            id = transaction.id,
            userId = transaction.userId,
            title = transaction.title,
            amount = transaction.amount,
            date = transaction.date,
            virement = transaction.virement,
            type = transaction.type
        )
    }
}
