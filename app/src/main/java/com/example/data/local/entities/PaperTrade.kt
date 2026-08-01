package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paper_trades")
data class PaperTrade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val symbolNameFa: String,
    val tradeType: String, // BUY, SELL
    val entryPrice: Double,
    val currentOrExitPrice: Double,
    val amountUsdt: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val pnlUsdt: Double,
    val pnlPercent: Double,
    val isOpen: Boolean = true,
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null
)
