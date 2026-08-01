package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.PaperTrade
import com.example.data.local.entities.SignalLog
import com.example.data.local.entities.WatchlistItem
import com.example.data.repository.TradingRepository
import com.example.domain.model.Asset
import com.example.domain.model.AssetCategory
import com.example.domain.model.ConfluenceAnalysis
import com.example.domain.model.TimeFrame
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradingRepository(application)

    val allAssets: StateFlow<List<Asset>> = repository.assetsState

    private val _selectedAsset = MutableStateFlow(repository.assetsState.value.first())
    val selectedAsset: StateFlow<Asset> = _selectedAsset.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow(TimeFrame.H1)
    val selectedTimeframe: StateFlow<TimeFrame> = _selectedTimeframe.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<AssetCategory?>(null)
    val selectedCategory: StateFlow<AssetCategory?> = _selectedCategory.asStateFlow()

    private val _notificationMessage = MutableSharedFlow<String>()
    val notificationMessage: SharedFlow<String> = _notificationMessage.asSharedFlow()

    val watchlist: StateFlow<List<WatchlistItem>> = repository.watchlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val signalHistory: StateFlow<List<SignalLog>> = repository.signalLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paperTrades: StateFlow<List<PaperTrade>> = repository.paperTrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paperBalance: StateFlow<Double> = repository.paperBalance

    val filteredAssets: StateFlow<List<Asset>> = combine(
        allAssets,
        searchQuery,
        selectedCategory,
        watchlist
    ) { assets, query, category, favList ->
        val favSymbols = favList.map { it.symbol }.toSet()
        assets.map { asset ->
            asset.copy(isFavorite = favSymbols.contains(asset.symbol))
        }.filter { asset ->
            val matchesQuery = query.isBlank() ||
                    asset.symbol.contains(query, ignoreCase = true) ||
                    asset.name.contains(query, ignoreCase = true) ||
                    asset.nameFa.contains(query, ignoreCase = true)
            val matchesCategory = category == null || asset.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentAnalysis = MutableStateFlow(
        repository.getAnalysis(repository.assetsState.value.first(), TimeFrame.H1)
    )
    val currentAnalysis: StateFlow<ConfluenceAnalysis> = _currentAnalysis.asStateFlow()

    fun selectAsset(asset: Asset) {
        _selectedAsset.value = asset
        updateAnalysis()
    }

    fun selectTimeframe(timeframe: TimeFrame) {
        _selectedTimeframe.value = timeframe
        updateAnalysis()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: AssetCategory?) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(asset: Asset) {
        repository.toggleFavorite(asset)
    }

    private fun updateAnalysis() {
        val analysis = repository.getAnalysis(_selectedAsset.value, _selectedTimeframe.value)
        _currentAnalysis.value = analysis
    }

    fun saveCurrentSignal() {
        repository.saveSignalLog(_currentAnalysis.value, _selectedAsset.value)
        viewModelScope.launch {
            _notificationMessage.emit("سیگنال تحلیلی با موفقیت در سوابق ذخیره شد.")
        }
    }

    fun openPaperTrade(tradeType: String, amountUsdt: Double) {
        val analysis = _currentAnalysis.value
        val asset = _selectedAsset.value
        if (paperBalance.value < amountUsdt) {
            viewModelScope.launch {
                _notificationMessage.emit("موجودی حساب مجازی شما کافی نیست!")
            }
            return
        }
        repository.executePaperTrade(
            asset = asset,
            tradeType = tradeType,
            amountUsdt = amountUsdt,
            entryPrice = analysis.entryPrice,
            stopLoss = analysis.stopLoss,
            takeProfit = analysis.takeProfit1
        )
        viewModelScope.launch {
            _notificationMessage.emit("معامله مجازی $tradeType با موفقیت باز شد.")
        }
    }

    fun closePaperTrade(trade: PaperTrade) {
        val currentPrice = allAssets.value.find { it.symbol == trade.symbol }?.currentPrice ?: trade.entryPrice
        repository.closePaperTrade(trade, currentPrice)
        viewModelScope.launch {
            _notificationMessage.emit("معامله مجازی بسته‌شد و سود/زیان محاسبه گردید.")
        }
    }

    fun simulateWebhookAlert(symbol: String, signalStr: String, messageFa: String) {
        val log = repository.simulateTradingViewWebhook(
            symbol = symbol,
            signalTypeStr = signalStr,
            timeframeCode = _selectedTimeframe.value.code,
            messageFa = messageFa
        )
        viewModelScope.launch {
            _notificationMessage.emit("🔔 هشدار جدید تریدینگ‌ویو دریافت شد: ${log.symbolNameFa} - ${log.signalType}")
        }
        updateAnalysis()
    }
}
