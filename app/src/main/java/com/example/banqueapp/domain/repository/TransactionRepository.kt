package com.example.banqueapp.domain.repository

import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.Virement

interface TransactionRepository {
    suspend fun getTransactionsForUser(userId: Int): List<Transaction>
    suspend fun getVirementsForUser(userId: Int): List<Virement>
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun addTransaction(virement: Virement): Long
    suspend fun deleteAllTransactionsForUser(userId: Int)
    suspend fun deleteTransaction(transactionId: Int, userId: Int)
}
