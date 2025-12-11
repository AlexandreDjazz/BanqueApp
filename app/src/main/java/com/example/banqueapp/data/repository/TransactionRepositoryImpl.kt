package com.example.banqueapp.data.repository

import com.example.banqueapp.data.db.dao.TransactionDao
import com.example.banqueapp.data.db.entities.TransactionEntity
import com.example.banqueapp.data.mapper.TransactionMapper
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.repository.TransactionRepository



class TransactionRepositoryImpl(private val transactionDao: TransactionDao) : TransactionRepository {

    override suspend fun getTransactionsForUser(userId: Int): List<Transaction> {
        return transactionDao.getTransactions(userId).map { TransactionMapper.toDomain(it) }
    }

    override suspend fun addTransaction(transaction: Transaction): Long {
        val entity = TransactionMapper.toEntity(transaction)
        transactionDao.insertTransaction(entity)
        return entity.id.toLong()
    }

    override suspend fun deleteTransaction(transactionId: Int, userId: Int) = transactionDao.deleteTransactionById(transactionId, userId)

    override suspend fun deleteAllTransactionsForUser(userId: Int) {
        val transactions = transactionDao.getTransactions(userId)
        transactions.forEach { transactionDao.deleteTransaction(it) }
    }
}