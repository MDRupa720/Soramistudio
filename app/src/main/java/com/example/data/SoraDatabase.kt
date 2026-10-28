package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_records ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HistoryRecordEntity)

    @Query("DELETE FROM history_records WHERE id = :id")
    suspend fun deleteRecord(id: String)

    @Query("DELETE FROM history_records")
    suspend fun clearAll()
}

@Database(entities = [HistoryRecordEntity::class], version = 1, exportSchema = false)
abstract class SoraDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: SoraDatabase? = null

        fun getDatabase(context: Context): SoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoraDatabase::class.java,
                    "sora_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
