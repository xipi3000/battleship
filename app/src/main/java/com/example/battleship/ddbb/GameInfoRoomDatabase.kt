package com.example.battleship.ddbb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameInfo::class], version = 1, exportSchema = false)
abstract class GameInfoRoomDatabase : RoomDatabase() {
    // Annotates class to be a Room Database with a table (entity) of the GameInfo class
    abstract fun GameInfoDao(): GameInfoDao
    companion object {
        @Volatile
        private var INSTANCE: GameInfoRoomDatabase? = null

        fun getDatabase(context: Context): GameInfoRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameInfoRoomDatabase::class.java,
                    "previous_games"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}