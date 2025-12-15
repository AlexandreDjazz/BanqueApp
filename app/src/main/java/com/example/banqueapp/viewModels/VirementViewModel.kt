package com.example.banqueapp.viewModels

import androidx.lifecycle.viewModelScope
import com.example.banqueapp.domain.models.Transaction
import com.example.banqueapp.domain.models.TransactionType
import com.example.banqueapp.domain.models.Virement
import com.example.banqueapp.domain.repository.TransactionRepository
import com.example.banqueapp.domain.repository.UserRepository
import com.example.banqueapp.ui.events.BalanceEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VirementResult {
    object Success : VirementResult()
    data class InsufficientFunds(val balance: Double, val amount: Double) : VirementResult()
    object InvalidAmount : VirementResult()
    object UserNotFound : VirementResult()
    object SameUser : VirementResult()
    data class Error(val message: String) : VirementResult()
}

class VirementViewModel(
    val virementRepository: TransactionRepository,
    private val userRepository: UserRepository,
) : TransactionViewModel(virementRepository) {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    override val transactions: StateFlow<List<Transaction>> = _transactions

    private val _virementResult = MutableStateFlow<VirementResult?>(null)
    val virementResult: StateFlow<VirementResult?> = _virementResult.asStateFlow()

    override fun loadTransactions(userId: Int) {
        viewModelScope.launch {
            _transactions.value = transactionRepository.getVirementsForUser(userId)
        }
    }

    override fun addTransaction(userId: Int, title: String, amount: Double, date: Long?) {
        viewModelScope.launch {
            val virement = Virement(
                id = 0,
                userId = userId,
                title = title,
                amount = amount,
                date = date ?: System.currentTimeMillis(),
                type = if (amount > 0) TransactionType.ADD else TransactionType.WITHDRAW
            )
            transactionRepository.addTransaction(virement)
            loadTransactions(userId)
        }
    }

    fun makeVirement(fromUserId: Int, toUserId: Int, title: String, amount: Double) {
        if (fromUserId == toUserId) {
            _virementResult.value = VirementResult.SameUser
            return
        }

        if (amount <= 0) {
            _virementResult.value = VirementResult.InvalidAmount
            return
        }

        viewModelScope.launch {
            try {
                val fromUser = userRepository.getUserById(fromUserId)
                if (fromUser == null) {
                    _virementResult.value = VirementResult.UserNotFound
                    return@launch
                }

                if (fromUser.balance < amount) {
                    _virementResult.value = VirementResult.InsufficientFunds(fromUser.balance, amount)
                    return@launch
                }

                val toUser = userRepository.getUserById(toUserId)
                if (toUser == null) {
                    _virementResult.value = VirementResult.UserNotFound
                    return@launch
                }

                val now = System.currentTimeMillis()

                val debitTransaction = Virement(
                    id = 0,
                    userId = fromUserId,
                    title = "Virement : $title",
                    amount = -amount,
                    date = now,
                    type = TransactionType.WITHDRAW
                )

                val creditTransaction = Virement(
                    id = 0,
                    userId = toUserId,
                    title = "Virement : $title",
                    amount = amount,
                    date = now,
                    type = TransactionType.ADD
                )

                virementRepository.addTransaction(debitTransaction)
                virementRepository.addTransaction(creditTransaction)

                userRepository.updateBalance(fromUserId, -amount)
                userRepository.updateBalance(toUserId, amount)

                _virementResult.value = VirementResult.Success
                loadTransactions(fromUserId)

                BalanceEventBus.emitBalanceUpdated(fromUserId)

            } catch (e: Exception) {
                _virementResult.value = VirementResult.Error("Erreur virement")
            }
        }
    }

    fun clearVirementResult() {
        _virementResult.value = null
    }
}