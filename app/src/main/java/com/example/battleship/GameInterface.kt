package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleship.ui.theme.BattleshipTheme
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Suppress("UNCHECKED_CAST")
class GameInterface : ComponentActivity() {
    private lateinit var enemyHasShipsUI: SnapshotStateList<CellState>
    lateinit var playerHasShipsUI: SnapshotStateList<CellState>
    var player1ships = GameConfiguration.State["Player1Ships"] as ArrayList<Int> //Player's ship setup
    var player2ships = GameConfiguration.State["Player2Ships"] as ArrayList<Int> //Bot/2nd Player's ship setup
    var isInPortraitOrientation: Boolean = true
    private val enemy = Enemy()
   var isYourTurn:Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                isInPortraitOrientation = when(LocalConfiguration.current.orientation){
                    Configuration.ORIENTATION_LANDSCAPE -> {false}
                    else -> {true}
                }
                MainView()
            }
        }
    }

    @Composable
    fun TableCell(
        text: String,
        hasShip: CellState,
        hasBeenClicked: Boolean = false,
        onCellClicked: () -> Unit,
        isClickable: Boolean = true,
    ) {
        var cellShot by remember { mutableStateOf(hasBeenClicked) }
        val onClick= {
            if(cellShot) Toast.makeText(this, "This cell has already been fired", Toast.LENGTH_SHORT).show()
            else{
                cellShot = true
                onCellClicked()
            }
        }

        Image(
            painter = painterResource(
                id = when (hasShip){
                    CellState.WATER -> R.drawable.water
                    CellState.UNKNOWN -> R.drawable.undiscovered
                    CellState.SHIP -> R.drawable.explosion
                    CellState.OUTOFBOUNDS -> R.drawable.water
                }
            ),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable(enabled = isClickable) {onClick()}
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        var timeRemaining by remember { mutableStateOf(GameConfiguration.State["MaxTime"].toString().toInt()) }
        val timed = GameConfiguration.State["Timed"]
        LaunchedEffect(Unit) {
            while(timeRemaining>0) {
                print(timeRemaining)
                delay(1000)
                timeRemaining--
            }
            //if time=0 -> finish game
            GameConfiguration.State = GameConfiguration.State + ("FinalTime" to timeRemaining)
            startActivity(Intent(this@GameInterface, ResultActivity::class.java))
        }

        //TODO: implement with rememberSaveable
        enemyHasShipsUI= remember { mutableStateListOf() }
        for (i in 0 until 100) enemyHasShipsUI.add(CellState.UNKNOWN)
        playerHasShipsUI= remember { mutableStateListOf() }
        for (i in 0 until 100) playerHasShipsUI.add(if(i in player1ships)CellState.UNKNOWN else CellState.WATER)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            ShowScreenContent(timeRemaining, timed as Boolean)
        }
    }

    @Composable
    private fun ShowScreenContent(timeRemaining: Int, timed:Boolean) {
        return Column{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (timeRemaining > 0) Color.Gray else Color.Red)
                    .height(90.dp),
                contentAlignment = Alignment.Center,
            )
            {
                Column {
                    Text(
                        text =
                        if (isYourTurn) "Your turn to fire"
                        else "Enemy's turn",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    AnimatedVisibility(visible = timed) {
                        Text(
                            text =
                            "Time remaining: $timeRemaining",
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            when (isInPortraitOrientation){
                true ->
                Column{
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .aspectRatio(1f),
                    ) {ShowBigGrid(timeRemaining)}
                    Box(
                        modifier = Modifier.padding(40.dp)
                    ) {
                        Column {
                            Text(text ="Your table")
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .aspectRatio(1f)
                            ){ShowSmallGrid()}
                        }
                    }
                }
                false ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .aspectRatio(1f),
                    ) {ShowBigGrid(timeRemaining)}
                    Box(
                        modifier = Modifier.padding(40.dp)
                    ) {
                        Column {
                            Text(text ="Your table")
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .aspectRatio(1f)
                            ){ShowSmallGrid()}
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ShowSmallGrid() {
        return LazyVerticalGrid(
            userScrollEnabled = false,
            columns = GridCells.Fixed(10),
            content = {
                items(100) {
                    TableCell(
                        text = it.toString(),
                        hasShip = playerHasShipsUI[it], //unknown enlloc de ship per el sprite
                        onCellClicked = {},
                        isClickable = false,
                    )
                }
            }
        )
    }

    @Composable
    private fun ShowBigGrid(timeRemaining:Int) {
        return LazyVerticalGrid(
            userScrollEnabled = false,
            columns = GridCells.Fixed(10),
            content = {
                items(100) {
                    TableCell(
                        text = it.toString(),
                        hasShip = enemyHasShipsUI[it],
                        isClickable = true,//testing = true; final = isYourTurn
                        onCellClicked = {
                            //player's shot
                            playTurn(it)
                            isYourTurn = !isYourTurn
                            //hauriem de mirar de fer que, d'alguna manera, s'actualitzés la grid
                            //abans del bot shot

                            //bot's shot
                            botTurn()
                            isYourTurn = !isYourTurn

                            //check if someone won
                            endGame(timeRemaining)
                        }
                    )
                }
            }
        )
    }

    private fun botTurn() {
        val cell = enemy.play()
        val parsedCell = cell.first*10+cell.second
        Log.i("BotCell", "Shooting $parsedCell")
        val infoCell = if(parsedCell in player1ships){CellState.SHIP }else{CellState.WATER}
        Log.i("BotCell", "It had $infoCell")
        enemy.checkCell(cell,infoCell)

        if(infoCell==CellState.SHIP) {
            playerHasShipsUI[parsedCell]=CellState.SHIP
            player1ships.remove(parsedCell)
        }
    }

    private fun playTurn(cell:Int) {
        if (cell in player2ships) {
            enemyHasShipsUI[cell] = CellState.SHIP
            //Remove shipcell from state
            player2ships.remove(cell)
            GameConfiguration.State = GameConfiguration.State + ("Player2Ships" to player2ships)
        }else{
            enemyHasShipsUI[cell] = CellState.WATER
        }
    }

    private fun endGame(timeRemaining:Int){
        if (player1ships.isEmpty() || player2ships.isEmpty()){
            GameConfiguration.State = GameConfiguration.State + ("FinalTime" to timeRemaining)
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}