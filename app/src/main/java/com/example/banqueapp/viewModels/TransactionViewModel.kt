package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.TransactionType
import com.example.banqueapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class TransactionViewModel(
    val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    open val transactions: StateFlow<List<Transaction>> = _transactions

    open fun loadTransactions(userId: Int) {
        viewModelScope.launch {
            _transactions.value = transactionRepository.getTransactionsForUser(userId)
        }
    }

    open fun addTransaction(userId: Int, title: String, amount: Double) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = 0,
                userId = userId,
                title = title,
                amount = amount,
                date = System.currentTimeMillis(),
                type = if (amount > 0) TransactionType.ADD else TransactionType.WITHDRAW
            )
            transactionRepository.addTransaction(transaction)
            loadTransactions(userId)
        }
    }

    open fun deleteTransaction(userId: Int, transactionId: Int) {

        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId, userId)
            loadTransactions(userId)
        }
    }



    open fun clearUserTransactions(userId: Int) {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactionsForUser(userId)
            loadTransactions(userId)
        }
    }
}
