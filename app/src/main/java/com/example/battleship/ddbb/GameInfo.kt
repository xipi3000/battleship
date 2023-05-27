package com.example.battleship.ddbb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "previous_games")
class GameInfo(
    //potser s'ha d'afegir un PrimaryKey diferent tho, que el nom en teoria s'ha de poder repetir?
    //fiquem un automàtic i apañao, total només volem veure coses
    @PrimaryKey @ColumnInfo(name="playerName") val alias: String,
    @ColumnInfo(name="ending") val result: String,
    @ColumnInfo(name="fired") val shots: Int,
    @ColumnInfo(name="hit") val hit: Int,
    @ColumnInfo(name="miss") val miss: Int,
    @ColumnInfo(name="accuracy") val accuracy: Float,
    )