package com.example.banqueapp.ui.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// EventBus.kt
object BalanceEventBus {
    private val _events = MutableSharedFlow<BalanceUpdatedEvent>()
    val events = _events.asSharedFlow()

    suspend fun emitBalanceUpdated(userId: Int) {
        _events.emit(BalanceUpdatedEvent(userId))
    }
}

data class BalanceUpdatedEvent(val userId: Int)
