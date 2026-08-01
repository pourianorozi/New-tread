package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.PaperTrade
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperTradeDao {
    @Query("SELECT * FROM paper_trades ORDER BY openedAt DESC")
    fun getAllTrades(): Flow<List<PaperTrade>>

    @Query("SELECT * FROM paper_trades WHERE isOpen = 1 ORDER BY openedAt DESC")
    fun getOpenTrades(): Flow<List<PaperTrade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: PaperTrade): Long

    @Update
    suspend fun updateTrade(trade: PaperTrade)

    @Query("DELETE FROM paper_trades WHERE id = :id")
    suspend fun deleteTrade(id: Long)
}
