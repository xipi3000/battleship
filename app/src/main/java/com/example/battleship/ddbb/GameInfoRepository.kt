package com.example.battleship.ddbb

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/*Tal qual del tutorial*/
class GameInfoRepository (private val gameDao: GameInfoDao) {
    val allGames: Flow<List<GameInfo>> = gameDao.getGames()

    @WorkerThread
    suspend fun insert(shot: GameInfo) {
        gameDao.insert(shot)
    }
}