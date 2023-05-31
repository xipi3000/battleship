package com.example.battleship.ddbb

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class GameInfoRepository (private val gameDao: GameInfoDao) {
    val allGames: Flow<List<GameInfo>> = gameDao.getGames()

    @WorkerThread
    suspend fun insert(game: GameInfo) {
        gameDao.insert(game)
    }
}