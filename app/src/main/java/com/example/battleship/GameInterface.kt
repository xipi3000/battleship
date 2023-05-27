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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
class LogText(var time: Int, var casellaSel: String, var isTocat:Boolean){
    fun print() : String{
        return "Casella selecccionada: $casellaSel\n " +
                if(isTocat) "Vaixell enemic tocat\n" else "Has tocat aigua\n" +
                        "Temps: $time\n"
    }
}
@Suppress("UNCHECKED_CAST")
class GameInterface : ComponentActivity() {
    private lateinit var enemyHasShipsUI: SnapshotStateList<CellState>
    private lateinit var playerHasShipsUI: SnapshotStateList<CellState>
    private lateinit var cellsShot:SnapshotStateList<Boolean>
    private var player1ships = GameConfiguration.State["Player1Ships"] as ArrayList<Int> //Player's ship setup
    private var player2ships = GameConfiguration.State["Player2Ships"] as ArrayList<Int> //Bot/2nd Player's ship setup
    private var player1Grid = SetUpYourShips.Grids["player1Grid"] as ArrayList<CellState>
    private var player2Grid = SetUpYourShips.Grids["player2Grid"] as ArrayList<CellState>
    private var cellsShotSave = SetUpYourShips.Grids["cellsShot"] as ArrayList<Boolean>
    private var isInPortraitOrientation: Boolean = true
    private lateinit var enemy: Enemy
    private var isYourTurn:Boolean = true
    private lateinit var timeRemaining: MutableState<Int>
    private var logPartida = mutableListOf<String>()

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
        onCellClicked: () -> Unit,
        isClickable: Boolean = true,
    ) {
        val onClick= {
            if(cellsShot[text.toInt()]) Toast.makeText(this, "This cell has already been fired", Toast.LENGTH_SHORT).show()
            else{
                cellsShot[text.toInt()] = true

                onCellClicked()
            }
        }

        Image(
            painter = painterResource(
                id = when (hasShip){
                    CellState.WATER -> R.drawable.water
                    CellState.UNKNOWN -> R.drawable.undiscovered
                    CellState.SHIPFOUND -> R.drawable.explosion
                    CellState.SHIPHIDDEN -> R.drawable.isship
                    CellState.OUTOFBOUNDS -> R.drawable.water //no s'utilitza pero es necessita per el when
                }
            ),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable(enabled = isClickable) { onClick() }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {

        timeRemaining =  remember { mutableStateOf(GameConfiguration.State["ActualTime"] as Int) }
        val timed = GameConfiguration.State["Timed"]
        LaunchedEffect(Unit) {
            while(timeRemaining.value>0) {
                print(timeRemaining.value)
                delay(1000)
                timeRemaining.value--
            }
            //if time=0 -> finish game
            GameConfiguration.State = GameConfiguration.State + ("FinalTime" to timeRemaining.value)
            saveData()
            startActivity(Intent(this@GameInterface, ResultActivity::class.java))
        }
        if(!::enemyHasShipsUI.isInitialized || !::playerHasShipsUI.isInitialized){
            enemyHasShipsUI = remember{ mutableStateListOf() }
            playerHasShipsUI = remember{ mutableStateListOf() }
            cellsShot = remember{ mutableStateListOf() }
        }
        for (i in 0 until 100){
            enemyHasShipsUI.add(player2Grid[i])
            playerHasShipsUI.add(player1Grid[i])
            cellsShot.add(cellsShotSave[i])
        }
        enemy = GameConfiguration.State["Enemy"] as Enemy


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            ShowScreenContent(timed as Boolean)
        }
    }

    @Composable
    private fun ShowScreenContent( timed:Boolean) {
        return Column(verticalArrangement = Arrangement.SpaceEvenly)
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (timeRemaining.value > 0) Color.Gray else Color.Red)
                    .height(60.dp),

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
                            "Time remaining: "+timeRemaining.value,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            val configuration = LocalConfiguration.current
            var screenWidth = configuration.screenWidthDp.dp
            when (screenWidth<=640.dp){
                true ->
                Column{
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .aspectRatio(1f)

                        ,
                    ) {BigGridComponent()}
                    Box(
                        modifier = Modifier.padding(40.dp)
                    ) {
                        Column {
                            Text(text ="Your table")
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .aspectRatio(1f)
                            ){SmallGridComponent()}
                        }
                    }
                }
                false ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ){

                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .aspectRatio(1f)
                            .weight(1f)

                           ,
                    ) {BigGridComponent()}
                    Box(
                        modifier = Modifier
                            .padding(60.dp)
                            .weight(1f)
                    ) {
                        Column() {
                            Column {
                                Text(text ="Your table")
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .aspectRatio(1f)
                                ){SmallGridComponent()}
                            }
                            Text(text = logPartida.toString(),
                                modifier = Modifier
                                    .height(200.dp)
                                    .verticalScroll(state = rememberScrollState())

                                ,

                                )
                        }

                    }
                }
            }
        }
    }

    @Composable
    private fun SmallGridComponent() {
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
    private fun BigGridComponent() {
        return LazyVerticalGrid(
            userScrollEnabled = false,
            columns = GridCells.Fixed(10),
            content = {
                items(100) {
                    TableCell(
                        text = it.toString(),
                        hasShip = enemyHasShipsUI[it],
                        onCellClicked = {
                            //player's shot
                            val logPlay = playTurn(it)

                            logPartida.add(logPlay.print())
                            isYourTurn = !isYourTurn
                            //hauriem de mirar de fer que, d'alguna manera, s'actualitzés la grid
                            //abans del bot shot


                            //bot's shot
                            botTurn()
                            isYourTurn = !isYourTurn

                            //check if someone won
                            endGame()
                        },//testing = true; final = isYourTurn
                        isClickable = true
                    )
                }
            }
        )
    }

    private fun botTurn() {
        val cell = enemy.play()
        val parsedCell = cell.first*10+cell.second
        Log.i("BotCell", "Shooting $parsedCell")
        val infoCell = if(parsedCell in player1ships){CellState.SHIPFOUND }else{CellState.WATER}
        Log.i("BotCell", "It had $infoCell")
        enemy.checkCell(cell,infoCell)

        if(infoCell==CellState.SHIPFOUND) {
            playerHasShipsUI[parsedCell]=CellState.SHIPFOUND
            player1ships.remove(parsedCell)
        }else{
            playerHasShipsUI[parsedCell] = CellState.WATER
        }

    }

    private fun playTurn(cell:Int) : LogText {
        var cela= ""
        val isTocat:Boolean
        if (cell in player2ships) {
            enemyHasShipsUI[cell] = CellState.SHIPFOUND
            //Remove shipcell from state
            player2ships.remove(cell)
            val row = cell/10
            val col = cell%10
            cela="($row-$col)"
            isTocat  = true
            //GameConfiguration.State = GameConfiguration.State + ("Player2Ships" to player2ships)
        }else{
            enemyHasShipsUI[cell] = CellState.WATER
            isTocat  = false
        }

        return LogText(time= timeRemaining.value, casellaSel=cela,isTocat= isTocat)
    }

    private fun endGame(){
        if (player1ships.isEmpty() || player2ships.isEmpty()){
            GameConfiguration.State = GameConfiguration.State + ("FinalTime" to timeRemaining.value)
            saveData()
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    private fun saveData(){
        //Update gameData
        for (i in 0 until 100){
            player2Grid[i] = enemyHasShipsUI[i]
            player1Grid[i] = playerHasShipsUI[i]
            cellsShotSave[i] = cellsShot[i]
        }
        //Store it
        SetUpYourShips.Grids = SetUpYourShips.Grids + ("player1Grid" to player1Grid)
        SetUpYourShips.Grids = SetUpYourShips.Grids + ("player2Grid" to player2Grid)
        SetUpYourShips.Grids = SetUpYourShips.Grids + ("cellsShot" to cellsShotSave)
        GameConfiguration.State = GameConfiguration.State + ("Enemy" to enemy)
        GameConfiguration.State = GameConfiguration.State + ("ActualTime" to timeRemaining.value)
    }
    override fun onDestroy() {
        saveData()
        super.onDestroy()
    }
}