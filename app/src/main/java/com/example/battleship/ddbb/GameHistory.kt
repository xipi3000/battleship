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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    private lateinit var clickedGame : MutableState<GameInfo>
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
                        //mòvil en portrait
                        MainView(gamesState)
                    }
                    false -> {
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_PORTRAIT -> {
                                //tablet en portrait
                                TabletView(gamesState)
                                Log.i("Layout", "I'm a tablet in portrait mode")
                            }
                            else -> {
                                when (configuration.screenHeightDp.dp < 600.dp){
                                    true -> {
                                        //mòvil en landscape
                                        MainView(gamesState)
                                        Log.i("Layout", "I'm a phone in landscape mode")
                                    }
                                    false -> {
                                        //tablet en landscape
                                        TabletView(gamesState)
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

    @Composable
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    fun TabletView(gamesState: List<GameInfo>) {
        clickedGame = remember { mutableStateOf(gamesState[0])}
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {this.finish()}){
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            },
            topBar = {
                TopAppBar (
                    title = {
                        Text("Game history")
                    }
                )
            }) {
            when(LocalConfiguration.current.orientation){
                Configuration.ORIENTATION_PORTRAIT->{
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()){
                        TabletInfo(gamesState)
                    }
                }
                else->{
                    Row(modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.Top){
                        TabletInfo(gamesState)
                    }
                }
            }
        }
    }

    @Composable
    private fun TabletInfo(gamesState:List<GameInfo>) {
        LazyColumn (modifier = when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Modifier.fillMaxWidth(0.75f).fillMaxHeight()
            }
            else ->{Modifier.fillMaxWidth(0.85f).fillMaxHeight(0.75f)}
        })
        {
            items(gamesState.size) { game ->
                Sumary(gamesState[game], false)
            }
        }
        GameLogComponent(clickedGame)
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
                    Sumary(gamesState[game], true)
                }
            }
        }
    }
    @Composable
    fun Sumary(game: GameInfo, newActivity:Boolean){
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable {
                    if (newActivity) {
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
                    } else {
                        clickedGame.value = game
                    }
                },
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row{
                    Text(text = game.alias)
                    Text(text = " - ")
                    Text(text = game.time)
                }
                Text(text = game.result)
            }
        }
    }

    @Composable
    private fun GameLogComponent(game:MutableState<GameInfo>) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .width(250.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game detail: ",
                Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .background(Color.LightGray)
                    .wrapContentHeight()
                    .width(250.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                InfoPiece("Name: ${game.value.alias}")
                InfoPiece("Result: ${game.value.result}")
                InfoPiece("Shots: ${game.value.shots}")
                InfoPiece("Hit: ${game.value.hit}")
                InfoPiece("Miss: ${game.value.miss}")
                InfoPiece("Accuracy: ${game.value.accuracy}")
                InfoPiece("Name: ${game.value.alias}")
                InfoPiece("Time: ${game.value.time}")
            }
        }
    }

    @Composable
    fun InfoPiece(text:String){
        Text(text = text, fontSize = (LocalConfiguration.current.screenHeightDp/50).sp)
    }

    override fun onDestroy() {
        gameInfoViewModel.allGames.removeObserver(observer)
        super.onDestroy()
    }
}