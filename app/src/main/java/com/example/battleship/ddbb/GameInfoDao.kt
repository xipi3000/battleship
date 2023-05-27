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

    @Insert //li trec el onConflict perque com tenim PrimareyKey el nom pos
    suspend fun insert(shot: GameInfo)

    @Query("DELETE FROM previous_games")
    suspend fun deleteAll()
}