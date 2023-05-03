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
class GameInterface : ComponentActivity() {
    //lateinit var lastShip: Ship //tecnicament lateinit no es null
    lateinit var grid: Unit
    lateinit var enemyHasShipsUI: SnapshotStateList<CellState>
    lateinit var playerHasShipsUI: SnapshotStateList<CellState>
    var player1ships = GameConfiguration.State["Player1Ships"] as ArrayList<Int> //Player's ship setup
    var player2ships = GameConfiguration.State["Player2Ships"] as ArrayList<Int> //Bot/2nd Player's ship setup
    var isInPortraitOrientation: Boolean = true
    val enemy = Enemy()

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
                }
            ),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable(enabled = isClickable) { onCellClicked()/*onClick()*/ }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        var timeRemaining by remember { mutableStateOf(GameConfiguration.State["InitialTime"].toString().toInt()) }
        val timed = GameConfiguration.State["Timed"]
        LaunchedEffect(Unit) {
            while(timeRemaining>0) {
                print(timeRemaining)
                delay(1000)
                timeRemaining--
            }
        }

        /*
        val carrier = Ship(ShipType.CARRIER)
        val battleship = Ship(ShipType.BATTLESHIP)
        val crusier = Ship(ShipType.CRUISER)
        val destroyer = Ship(ShipType.DESTROYER)
        val submarine = Ship(ShipType.SUBMARINE)
        */
        var isYourTurn by remember {
            mutableStateOf(true)
        }

        if (isInPortraitOrientation) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Each cell of a column must have the same weight.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (timeRemaining > 0) Color.Gray else Color.Red)
                        .height(90.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column {
                        Text(
                            text =
                            if (isYourTurn) "Your turn to fire"
                            else "Enemy's turn",
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,

                            )
                        AnimatedVisibility(visible = timed as Boolean) {
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
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .aspectRatio(1f)
                )
                {
                    //TODO: implement with rememberSaveable
                    enemyHasShipsUI= remember { mutableStateListOf() }
                    for (i in 0 until 100) enemyHasShipsUI.add(CellState.UNKNOWN)
                    playerHasShipsUI= remember { mutableStateListOf() }
                    for (i in 0 until 100) playerHasShipsUI.add(if(i in player1ships)CellState.UNKNOWN else CellState.WATER)

                    grid = LazyVerticalGrid(
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

                                            //bot's shot
                                            val cell = enemy.play()
                                            val parsedCell = cell.first*10+cell.second
                                            Log.i("BotCell", "Shooting $parsedCell")
                                            val infoCell = if(parsedCell in player1ships){CellState.SHIP} else {CellState.WATER}
                                            Log.i("BotCell", "It had $infoCell")
                                            enemy.checkCell(cell,infoCell)

                                            if(infoCell==CellState.SHIP) {playerHasShipsUI[parsedCell]=CellState.SHIP}
                                            endGame()
                                            }
                                    )
                                }
                            }
                        )
                }
                Box(
                    modifier = Modifier.padding(40.dp)
                ) {
                    Column {
                        Text(text ="Your table")
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .aspectRatio(1f)
                        )
                        {
                        grid =LazyVerticalGrid(
                                userScrollEnabled = false,
                                columns = GridCells.Fixed(10),
                                content = {
                                    items(100) {
                                        TableCell(
                                            text = it.toString(),
                                            hasBeenClicked = false,
                                            hasShip = playerHasShipsUI[it], //unknown enlloc de ship per el sprite
                                            onCellClicked = {},
                                            isClickable = false,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        else{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Each cell of a column must have the same weight.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .height(70.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text =
                        if (isYourTurn) "Your turn to fire"
                        else "Enemys turn",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        )
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .aspectRatio(1f)
                    )
                    {
                        val enemyHasShipsUI: SnapshotStateList<CellState> = remember { mutableStateListOf() }
                        for (i in 0 until 100) enemyHasShipsUI.add(CellState.UNKNOWN)

                        grid = LazyVerticalGrid(
                            userScrollEnabled = false,
                            columns = GridCells.Fixed(10),
                            content = {
                                items(100) {
                                    TableCell(
                                        text = it.toString(),
                                        hasShip = enemyHasShipsUI[it],
                                        isClickable = isYourTurn,
                                        onCellClicked = {
                                            if (isYourTurn) {
                                                isYourTurn = false
                                                if (it in player2ships) {
                                                    enemyHasShipsUI[it] = CellState.SHIP
                                                }
                                                isYourTurn = false
                                            }
                                        }

                                    )
                                }
                            }
                        )
                    }
                    Box(
                        modifier = Modifier.padding(40.dp)
                    ) {
                        Column {
                            Text(text = "Yours table")

                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .aspectRatio(1f)
                            )
                            {
                                grid = LazyVerticalGrid(
                                    userScrollEnabled = false,
                                    columns = GridCells.Fixed(10),
                                    content = {
                                        items(100) {
                                            TableCell(
                                                text = it.toString(),
                                                hasShip = CellState.WATER, //isClickedState[it],
                                                onCellClicked = {},
                                                isClickable = false,
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playTurn(cell:Int) {
        if (cell in player2ships) {
            Toast.makeText(baseContext, "Tocat", Toast.LENGTH_SHORT).show()
            enemyHasShipsUI[cell] = CellState.SHIP
            //Remove shipcell from state
            player2ships.remove(cell)
            GameConfiguration.State = GameConfiguration.State + ("Player2Ships" to player2ships)
            Log.i("ShipChange", "Erased $cell from player2")
            Log.i("ShipChange", "Cells left: "+ GameConfiguration.State["Player2Ships"])
        }else{
            Toast.makeText(baseContext, "Aigua", Toast.LENGTH_SHORT).show()
            enemyHasShipsUI[cell] = CellState.WATER
        }
    }

    private fun endGame(){
        var count =0
        for (item in enemyHasShipsUI){
            if (item == CellState.SHIP) count++
            //(17 es el sumatori de sizes de tots els barcos, de moment esta cutre pero ja ho fem putamadre més tard)
            if (count == 17) startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}
