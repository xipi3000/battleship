package com.example.battleship.ddbb

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/*Tal qual del tutorial [ús només al crear el ViewModel]*/
class GameInfoApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob())

    private val database by lazy {GameInfoRoomDatabase.getDatabase(this, appScope)}
    val repository by lazy {GameInfoRepository(database.GameInfoDao())}
}