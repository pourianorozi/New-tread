package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signal_logs")
data class SignalLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val symbolNameFa: String,
    val timeframe: String, // 1m, 5m, 15m, 1h, 4h, 1D, 1W
    val signalType: String, // STRONG_BUY, BUY, NEUTRAL, SELL, STRONG_SELL
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val strengthScore: Int, // 1 to 10
    val reasoningFa: String,
    val indicatorSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
