package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Query("DELETE FROM match_history")
    suspend fun clearHistory()

    @Query("SELECT COUNT(*) FROM match_history")
    fun getTotalMatchesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM match_history WHERE result = 'X_WON'")
    fun getXWinsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM match_history WHERE result = 'O_WON'")
    fun getOWinsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM match_history WHERE result = 'DRAW'")
    fun getDrawsCount(): Flow<Int>
}
