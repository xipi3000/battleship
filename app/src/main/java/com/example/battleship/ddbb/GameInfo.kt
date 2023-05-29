package com.example.battleship.ddbb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "previous_games")
class GameInfo(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name="playerName") val alias: String,
    @ColumnInfo(name="ending") val result: String,
    @ColumnInfo(name="fired") val shots: Int,
    @ColumnInfo(name="hit") val hit: Int,
    @ColumnInfo(name="miss") val miss: Int,
    @ColumnInfo(name="accuracy") val accuracy: Float,
    )