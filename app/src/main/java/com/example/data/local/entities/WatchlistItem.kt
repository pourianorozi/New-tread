package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_items")
data class WatchlistItem(
    @PrimaryKey val symbol: String,
    val name: String,
    val nameFa: String,
    val category: String, // CRYPTO, FOREX, STOCKS
    val isFavorite: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)
