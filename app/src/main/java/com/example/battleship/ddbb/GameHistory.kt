package com.example.battleship.ddbb

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
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
            // Retrieving database
            Text(text = "Loading...")
        } else {
            // Got database, but could be empty
            if (gamesState.isEmpty()){
                Text(text = "Vaja, sembla que encara no has fet cap partida")
            }else{
                // Main screen creation
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp

                when (screenWidth.dp < 600.dp) {
                    true -> {
                        MainView(gamesState)
                    }
                    false -> {
                        //movil en landscape || tablet
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_PORTRAIT -> {
                                //tablet en portrait
                                MainView(gamesState)
                                Log.i("Layout", "I'm a tablet in portrait mode")
                            }
                            else -> {
                                //landscape els dos
                                when (configuration.screenHeightDp.dp < 600.dp){
                                    true -> {
                                        //movil landscape
                                        MainView(gamesState)
                                        Log.i("Layout", "I'm a phone in landscape mode")
                                    }
                                    false -> {
                                        //tablet landscape
                                        MainView(gamesState)
                                        Log.i("Layout", "I'm a tablet in landscape mode")
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
    }
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainView(gamesState: List<GameInfo>){
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {this.finish()}){
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            }) {
            LazyColumn (modifier = when (LocalConfiguration.current.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                        Modifier.width(LocalConfiguration.current.screenWidthDp.dp/1.5f)
                }
                else ->{Modifier.width(LocalConfiguration.current.screenWidthDp.dp/1.2f)}
            })
            {
                items(gamesState.size) {game ->
                    Sumary(gamesState[game])
                }
            }
        }
    }
    @Composable
    fun Sumary(game: GameInfo){
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
                    bund.putString("time", game.time)
                    bund.putInt("timeSpent", game.timeSpent)
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
                Text(text = game.result)
            }
        }
    }

    //modificar per aquest cas d'ús
    private var logPartida = mutableListOf<String>()
    private lateinit var logListState: LazyListState
    @Composable
    private fun GameLogComponent() {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .width(250.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = "Log:",
                Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
            )
            LazyColumn(
                modifier = Modifier
                    .background(Color.LightGray)
                    .height(150.dp)
                    .width(250.dp),
                state = logListState
            )
            {
                itemsIndexed(logPartida) { _, log ->
                    Text(
                        text = log,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameInfoViewModel.allGames.removeObserver(observer)
    }
}