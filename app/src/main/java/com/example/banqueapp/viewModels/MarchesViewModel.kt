package com.example.banqueapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazzyghost.alphavantage.AlphaVantage
import com.crazzyghost.alphavantage.Config
import com.crazzyghost.alphavantage.parameters.OutputSize
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class Stock(
    val symbol: String,
    val price: String,
    val change: String,
    val isPositive: Boolean
)

sealed class UiState {
    object Loading : UiState()
    data class Success(val stocks: List<Stock>) : UiState()
    data class Error(val message: String) : UiState()
}

class MarchesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val apiKey: String = "5LVGFH5P11XL0T23"
    private val cfg: Config = Config.builder()
        .key(apiKey)
        .timeOut(10)
        .build()

    init {
        AlphaVantage.api().init(cfg)
        fetchStocks()
    }

    private fun handleSuccess(symbol: String, response: TimeSeriesResponse, currentStocks: MutableList<Stock>) {
        val stockUnits = response.stockUnits ?: return
        val latest = stockUnits.firstOrNull()
        latest?.let { stockUnit ->
            val price = String.format(Locale.US, "%.2f $", stockUnit.close)
            val previous = stockUnits.getOrNull(1)
            val changePercent = if (previous != null) {
                val change = stockUnit.close - previous.close
                val pct = (change / previous.close) * 100
                String.format(Locale.US, "%.2f%% (%.2f)", pct, change)
            } else "+0.00%"

            val isPositive = changePercent.startsWith("+")
            val stock = Stock(symbol, price, changePercent, isPositive)

            // Ajoute à la liste temporaire
            val index = currentStocks.indexOfFirst { it.symbol == symbol }
            if (index >= 0) {
                currentStocks[index] = stock
            } else {
                currentStocks.add(stock)
            }

            _uiState.value = UiState.Success(currentStocks.toList())
        }
    }

    fun fetchStocks() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val symbols = listOf("IBM")
            val tempStocks = mutableListOf<Stock>()

            symbols.forEach { symbol ->
                AlphaVantage.api()
                    .timeSeries()
                    .daily()
                    .forSymbol(symbol)
                    .outputSize(OutputSize.COMPACT)
                    .onSuccess { response ->
                        handleSuccess(symbol, response as TimeSeriesResponse, tempStocks)
                    }
                    .onFailure { error ->
                        _uiState.value = UiState.Error(error.message ?: "Erreur API")
                    }
                    .fetch()
            }
        }
    }
}