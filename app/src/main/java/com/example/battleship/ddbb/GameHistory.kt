package com.example.battleship.ddbb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.battleship.ui.theme.BattleshipTheme

class GameHistory  : ComponentActivity(){
    val GameInfoViewModel: GameInfoViewModel by viewModels {
        WordViewModelFactory((application as GameInfoApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        val adapter = GameInfoListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        GameInfoViewModel.allGames.observe(this) { games ->
            // Update the cached copy of the words in the adapter.
            games.let { adapter.submitList(it) }
        }
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
            items(5){
                Sumary()
            }
        })
    }
    @Composable
    fun Sumary(){
        return Column(){
            Row(){
                //player name - time
                Text(text = "player name - time")
            }
            Row(){
                //%aciertos
                Text(text = "%aciertos")
            }
        }
    }
}