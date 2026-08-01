package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entities.PaperTrade
import com.example.data.local.entities.SignalLog
import com.example.data.local.entities.WatchlistItem
import com.example.domain.engine.TechnicalAnalysisEngine
import com.example.domain.model.Asset
import com.example.domain.model.AssetCategory
import com.example.domain.model.ConfluenceAnalysis
import com.example.domain.model.SignalType
import com.example.domain.model.TimeFrame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TradingRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val watchlistDao = db.watchlistDao()
    private val signalDao = db.signalDao()
    private val paperTradeDao = db.paperTradeDao()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Pre-populated assets database
    private val initialAssets = listOf(
        Asset("BTCUSDT", "BINANCE:BTCUSDT", "Bitcoin", "بیت‌کوین", AssetCategory.CRYPTO, 67450.00, +2.45, 68200.0, 65900.0, 24500000.0),
        Asset("ETHUSDT", "BINANCE:ETHUSDT", "Ethereum", "اتریوم", AssetCategory.CRYPTO, 3480.50, +3.12, 3540.0, 3360.0, 18200000.0),
        Asset("SOLUSDT", "BINANCE:SOLUSDT", "Solana", "سولانا", AssetCategory.CRYPTO, 184.20, +5.80, 189.0, 172.5, 9500000.0),
        Asset("XRPUSDT", "BINANCE:XRPUSDT", "Ripple", "ریپل", AssetCategory.CRYPTO, 0.625, -1.15, 0.645, 0.610, 4200000.0),
        Asset("BNBUSDT", "BINANCE:BNBUSDT", "BNB", "بایننس کوین", AssetCategory.CRYPTO, 582.40, +1.20, 590.0, 575.0, 6100000.0),
        Asset("XAUUSD", "OANDA:XAUUSD", "Gold Spot", "طلای جهانی (XAU)", AssetCategory.FOREX, 2384.50, +0.85, 2398.0, 2365.0, 58000000.0),
        Asset("EURUSD", "FX:EURUSD", "EUR / USD", "یورو به دلار", AssetCategory.FOREX, 1.0875, -0.22, 1.0910, 1.0840, 32000000.0),
        Asset("GBPUSD", "FX:GBPUSD", "GBP / USD", "پوند به دلار", AssetCategory.FOREX, 1.2940, +0.45, 1.2980, 1.2890, 21000000.0),
        Asset("AAPL", "NASDAQ:AAPL", "Apple Inc.", "سهام اپل", AssetCategory.STOCKS, 224.30, +1.40, 226.5, 221.8, 14500000.0),
        Asset("TSLA", "NASDAQ:TSLA", "Tesla Inc.", "سهام تسلا", AssetCategory.STOCKS, 248.80, -2.10, 255.0, 244.2, 19800000.0),
        Asset("NVDA", "NASDAQ:NVDA", "NVIDIA", "سهام انویدیا", AssetCategory.STOCKS, 126.50, +4.60, 129.0, 121.2, 45000000.0)
    )

    private val _assetsState = MutableStateFlow(initialAssets)
    val assetsState: StateFlow<List<Asset>> = _assetsState.asStateFlow()

    val watchlistItems: Flow<List<WatchlistItem>> = watchlistDao.getAllWatchlist()
    val signalLogs: Flow<List<SignalLog>> = signalDao.getAllSignals()
    val paperTrades: Flow<List<PaperTrade>> = paperTradeDao.getAllTrades()

    // Virtual Balance State (default $10,000 USDT)
    private val _paperBalance = MutableStateFlow(10000.0)
    val paperBalance: StateFlow<Double> = _paperBalance.asStateFlow()

    init {
        // Pre-populate watchlist if empty
        coroutineScope.launch {
            val initialFavorites = listOf(
                WatchlistItem("BTCUSDT", "Bitcoin", "بیت‌کوین", "CRYPTO"),
                WatchlistItem("ETHUSDT", "Ethereum", "اتریوم", "CRYPTO"),
                WatchlistItem("XAUUSD", "Gold Spot", "طلای جهانی", "FOREX"),
                WatchlistItem("NVDA", "NVIDIA", "سهام انویدیا", "STOCKS")
            )
            initialFavorites.forEach { watchlistDao.insertItem(it) }
        }
    }

    fun toggleFavorite(asset: Asset) {
        coroutineScope.launch {
            val isFav = watchlistDao.isFavorite(asset.symbol)
            if (isFav) {
                watchlistDao.deleteBySymbol(asset.symbol)
            } else {
                watchlistDao.insertItem(
                    WatchlistItem(
                        symbol = asset.symbol,
                        name = asset.name,
                        nameFa = asset.nameFa,
                        category = asset.category.name
                    )
                )
            }
        }
    }

    fun getAnalysis(asset: Asset, timeframe: TimeFrame): ConfluenceAnalysis {
        return TechnicalAnalysisEngine.analyze(asset, timeframe)
    }

    fun saveSignalLog(analysis: ConfluenceAnalysis, asset: Asset) {
        coroutineScope.launch {
            val log = SignalLog(
                symbol = analysis.symbol,
                symbolNameFa = asset.nameFa,
                timeframe = analysis.timeFrame.code,
                signalType = analysis.overallSignal.name,
                entryPrice = analysis.entryPrice,
                stopLoss = analysis.stopLoss,
                takeProfit1 = analysis.takeProfit1,
                takeProfit2 = analysis.takeProfit2,
                strengthScore = analysis.strengthScore,
                reasoningFa = analysis.recommendedActionFa,
                indicatorSummary = analysis.indicators.joinToString(" | ") { "${it.nameFa}: ${it.signal.labelFa}" }
            )
            signalDao.insertSignal(log)
        }
    }

    fun executePaperTrade(asset: Asset, tradeType: String, amountUsdt: Double, entryPrice: Double, stopLoss: Double, takeProfit: Double) {
        coroutineScope.launch {
            if (_paperBalance.value >= amountUsdt) {
                _paperBalance.value -= amountUsdt
                val trade = PaperTrade(
                    symbol = asset.symbol,
                    symbolNameFa = asset.nameFa,
                    tradeType = tradeType,
                    entryPrice = entryPrice,
                    currentOrExitPrice = entryPrice,
                    amountUsdt = amountUsdt,
                    stopLoss = stopLoss,
                    takeProfit = takeProfit,
                    pnlUsdt = 0.0,
                    pnlPercent = 0.0,
                    isOpen = true
                )
                paperTradeDao.insertTrade(trade)
            }
        }
    }

    fun closePaperTrade(trade: PaperTrade, exitPrice: Double) {
        coroutineScope.launch {
            val isBuy = trade.tradeType == "BUY" || trade.tradeType == "خرید"
            val priceDiff = if (isBuy) (exitPrice - trade.entryPrice) else (trade.entryPrice - exitPrice)
            val pnlPercent = (priceDiff / trade.entryPrice) * 100
            val pnlUsdt = trade.amountUsdt * (pnlPercent / 100)
            val returnAmount = trade.amountUsdt + pnlUsdt

            _paperBalance.value += returnAmount

            val updatedTrade = trade.copy(
                currentOrExitPrice = exitPrice,
                pnlUsdt = pnlUsdt,
                pnlPercent = pnlPercent,
                isOpen = false,
                closedAt = System.currentTimeMillis()
            )
            paperTradeDao.updateTrade(updatedTrade)
        }
    }

    // Simulate incoming alert from TradingView Desktop Webhook
    fun simulateTradingViewWebhook(symbol: String, signalTypeStr: String, timeframeCode: String, messageFa: String): SignalLog {
        val asset = _assetsState.value.find { it.symbol == symbol } ?: _assetsState.value.first()
        val signalType = when (signalTypeStr.uppercase()) {
            "BUY", "خرید" -> SignalType.BUY
            "SELL", "فروش" -> SignalType.SELL
            "STRONG_BUY", "خرید قوی" -> SignalType.STRONG_BUY
            "STRONG_SELL", "فروش قوی" -> SignalType.STRONG_SELL
            else -> SignalType.NEUTRAL
        }

        val analysis = TechnicalAnalysisEngine.analyze(asset, TimeFrame.entries.find { it.code == timeframeCode } ?: TimeFrame.H1)
        val log = SignalLog(
            symbol = asset.symbol,
            symbolNameFa = asset.nameFa,
            timeframe = timeframeCode,
            signalType = signalType.name,
            entryPrice = asset.currentPrice,
            stopLoss = analysis.stopLoss,
            takeProfit1 = analysis.takeProfit1,
            takeProfit2 = analysis.takeProfit2,
            strengthScore = 9,
            reasoningFa = "ارسال شده از هشدارهای وب‌هوک TradingView دسکتاپ: $messageFa",
            indicatorSummary = "پاین اسکریپت دسکتاپ: $messageFa"
        )

        coroutineScope.launch {
            signalDao.insertSignal(log)
        }
        return log
    }
}
