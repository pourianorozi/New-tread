package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.SignalLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Query("SELECT * FROM signal_logs ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<SignalLog>>

    @Query("SELECT * FROM signal_logs WHERE symbol = :symbol ORDER BY timestamp DESC LIMIT 10")
    fun getSignalsForSymbol(symbol: String): Flow<List<SignalLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalLog)

    @Query("DELETE FROM signal_logs")
    suspend fun clearHistory()
}
