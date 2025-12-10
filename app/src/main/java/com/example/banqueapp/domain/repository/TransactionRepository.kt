package com.example.banqueapp.domain.repository

import com.example.banqueapp.domain.models.Transaction

interface TransactionRepository {
    suspend fun getTransactionsForUser(userId: Int): List<Transaction>
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun deleteAllTransactionsForUser(userId: Int)
    suspend fun deleteTransaction(transactionId: Int)
}
