package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.PaperTradeDao
import com.example.data.local.dao.SignalDao
import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entities.PaperTrade
import com.example.data.local.entities.SignalLog
import com.example.data.local.entities.WatchlistItem

@Database(
    entities = [WatchlistItem::class, SignalLog::class, PaperTrade::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun signalDao(): SignalDao
    abstract fun paperTradeDao(): PaperTradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smarttrade_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
