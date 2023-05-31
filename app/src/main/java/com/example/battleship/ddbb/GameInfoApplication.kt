package com.example.battleship.ddbb

import android.app.Application

class GameInfoApplication : Application() {
    private val database by lazy {GameInfoRoomDatabase.getDatabase(this)}
    val repository by lazy {GameInfoRepository(database.GameInfoDao())}
}