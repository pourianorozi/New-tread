package com.example.domain.model

enum class AssetCategory(val displayNameFa: String) {
    CRYPTO("رمزارزها"),
    FOREX("جفت‌ارزها و طلا"),
    STOCKS("سهام بین‌المللی")
}

data class Asset(
    val symbol: String, // e.g. "BTCUSDT", "EURUSD", "AAPL"
    val tvSymbol: String, // TradingView widget symbol e.g. "BINANCE:BTCUSDT"
    val name: String,
    val nameFa: String,
    val category: AssetCategory,
    val currentPrice: Double,
    val change24h: Double,
    val high24h: Double,
    val low24h: Double,
    val volume: Double,
    val isFavorite: Boolean = false
)
