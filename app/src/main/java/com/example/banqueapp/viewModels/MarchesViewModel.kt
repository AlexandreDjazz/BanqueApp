package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class Stock(
    val symbol: String,
    val price: String,
    val change: String,
    val isPositive: Boolean
)

class MarchesViewModel() : ViewModel() {
    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks.asStateFlow()

    fun fetchStocks() {
        viewModelScope.launch {
            // Simulation API (remplacez par Retrofit + vraie API)
            delay(1000)
            _stocks.value = listOf(
                Stock("AAPL", "150.25 $", "+2.5% (+3.75)", true),
                Stock("GOOGL", "2750.80 $", "-1.2% (-33.20)", false),
                Stock("TSLA", "245.60 $", "+5.8% (+13.45)", true)
            )
        }
    }
}