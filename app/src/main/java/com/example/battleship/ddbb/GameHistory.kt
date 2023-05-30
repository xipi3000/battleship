package com.example.battleship.ddbb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.example.battleship.MainActivity
import com.example.battleship.ui.theme.BattleshipTheme

class GameHistory  : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                GameHistoryScreen()
            }
        }
    }
    private val gameInfoViewModel: GameInfoViewModel by viewModels {
        GameInfoViewModelFactory((application as GameInfoApplication).repository)
    }
    private lateinit var observer : Observer<List<GameInfo>>
    @SuppressLint("NotConstructor")
    @Composable
    fun GameHistoryScreen(){
        val gamesState = remember{ mutableStateListOf<GameInfo>()}
        val isLoading = remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            val games = gameInfoViewModel.allGames
             observer = Observer<List<GameInfo>> { newGames ->
                gamesState.clear()
                gamesState.addAll(newGames)
                isLoading.value = false
                Log.i("DDBB_Log", "[LaunchedEffect] AllGames.size: ${gamesState.size}")
            }
            games.observe(this@GameHistory, observer)
        }
        if (isLoading.value) {
            // Show a loading indicator
            Text(text = "Loading...")
        } else {
            MainView(gamesState)
        }
    }
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainView(gamesState: List<GameInfo>){
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val intent = Intent (this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    this.startActivity(intent)
                }){
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            }) {
            LazyColumn {
                items(gamesState.size) {game ->
                    Sumary(gamesState[game])
                }
            }
        }
    }
    @Composable
    fun Sumary(game: GameInfo){
        //Versió turbo goofy dels resums dels logs, faltarà fer clickable i que t'obri el log sencer
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(this, GameDetail::class.java)
                    val bund = Bundle()
                    bund.putString("name", game.alias)
                    bund.putString("result", game.result)
                    bund.putInt("fired", game.shots)
                    bund.putInt("hit", game.hit)
                    bund.putInt("miss", game.miss)
                    bund.putFloat("accuracy", game.accuracy)
                    intent.putExtra("bund", bund)
                    this.startActivity(intent)
                },
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = game.alias)
                Text(text = "${game.accuracy}%")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameInfoViewModel.allGames.removeObserver(observer)
    }
}