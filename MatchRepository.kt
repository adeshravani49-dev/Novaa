package com.example.data

import com.example.data.db.MatchDao
import com.example.data.db.MatchEntity
import kotlinx.coroutines.flow.Flow

class MatchRepository(private val matchDao: MatchDao) {

    val allMatches: Flow<List<MatchEntity>> = matchDao.getAllMatches()
    val totalMatches: Flow<Int> = matchDao.getTotalMatchesCount()
    val xWins: Flow<Int> = matchDao.getXWinsCount()
    val oWins: Flow<Int> = matchDao.getOWinsCount()
    val draws: Flow<Int> = matchDao.getDrawsCount()

    suspend fun recordMatch(match: MatchEntity) {
        matchDao.insertMatch(match)
    }

    suspend fun clearAllHistory() {
        matchDao.clearHistory()
    }
}
