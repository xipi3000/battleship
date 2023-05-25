package com.example.battleship

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import com.example.battleship.ui.theme.BattleshipTheme

class GameHistory  : ComponentActivity(){
    //anar mirant el projecte de RoomWithAView per veure com funcionen lo dels rooms
    lateinit var DDBB : LiveData<List<LogText>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                MainView()
            }
        }
    }

    @Composable
    fun MainView(){
        LazyColumn(content = {
            //llista d'elements clickables que diuen un resum de les dades de la partida
        })
    }

    @Composable
    fun Sumary(){
        return Column(){
            Row(){
                //player name - time
            }
            Row(){
                //%aciertos
            }
        }
    }
}