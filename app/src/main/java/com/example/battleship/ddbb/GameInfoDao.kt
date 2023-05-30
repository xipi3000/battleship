package com.example.battleship.ddbb

import androidx.room.Dao
import androidx.room.Insert

import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/*Tal qual del tutorial*/
@Dao
interface GameInfoDao {
    @Query("SELECT * FROM previous_games")
    fun getGames(): Flow<List<GameInfo>>

    @Insert
    suspend fun insert(game: GameInfo)

    @Query("DELETE FROM previous_games")
    suspend fun deleteAll()
}