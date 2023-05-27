package com.example.battleship.ddbb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.battleship.ui.theme.BattleshipTheme

class GameHistory  : ComponentActivity(){
    private val gameInfoViewModel: GameInfoViewModel by viewModels {
        GameInfoViewModelFactory((application as GameInfoApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*1 me sembla que això no ho utilitzo, no se com d'important es fer-ho */
        val recyclerView = RecyclerView(this)
        val adapter = GameInfoListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        /*1 */
        /*2 potser lo de que només s'actualitzi sota x condicions te a veure amb això? */
        gameInfoViewModel.allGames.observe(this) { games ->
            // Update the cached copy of the words in the adapter.
            games.let { adapter.submitList(it) }
        }
        /*2 */
        setContent {
            BattleshipTheme {
                MainView()
            }
        }
    }

    @Composable
    fun MainView(){
        val elements = gameInfoViewModel.allGames
        val size = elements.value?.size ?: 0
        var count = 0
        LazyColumn {
            //llista d'elements clickables que diuen un resum de les dades de la partida
            items(size) {
                elements.value?.let { it1 -> Sumary(it1[count]) }
                count++
            }
        }
    }
    @Composable
    fun Sumary(game: GameInfo){
        //Versió turbo goofy dels resums dels logs, faltarà fer clickable i que t'obri el log sencer
        return Card(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                val name = game.alias
                val acc = game.accuracy
                Text(text = name)
                Text(text = "$acc%")
            }
        }

    }
}