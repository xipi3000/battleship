package com.example.battleship.ddbb

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*Tal qual del exemple menos el populate*/
@Database(entities = [GameInfo::class], version = 1, exportSchema = false)
abstract class GameInfoRoomDatabase : RoomDatabase() {
    // Annotates class to be a Room Database with a table (entity) of the GameInfo class
    abstract fun GameInfoDao(): GameInfoDao

    companion object {
        @Volatile
        private var INSTANCE: GameInfoRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GameInfoRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameInfoRoomDatabase::class.java,
                    "previous_games"
                ).addCallback(GameInfoDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class GameInfoDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.GameInfoDao())
                }
            }
        }

        suspend fun populateDatabase(gameDao: GameInfoDao) {
            gameDao.insert(GameInfo("test", "test", 1, 1,1,1f))
            Log.i("GameInfo", "Added test entry")
        }
    }

}