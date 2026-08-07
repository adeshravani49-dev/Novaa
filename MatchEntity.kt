package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameMode: String,
    val difficulty: String?,
    val boardSizeLabel: String,
    val playerXName: String,
    val playerOName: String,
    val result: String, // "X_WON", "O_WON", "DRAW"
    val winnerName: String?,
    val moveCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
