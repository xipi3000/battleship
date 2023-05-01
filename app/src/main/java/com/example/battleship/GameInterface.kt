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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

class GameInterface : ComponentActivity() {
    //lateinit var lastShip: Ship //tecnicament lateinit no es null
    lateinit var grid: Unit
    lateinit var enemyHasShipsUI: SnapshotStateList<Int>
    var player1Grid:SnapshotStateList<ShipType> =
        MainActivity.State["Player1Grid"] as SnapshotStateList<ShipType> //Player's ship setup
    var player2Grid:SnapshotStateList<ShipType> =
        MainActivity.State["Player2Grid"] as SnapshotStateList<ShipType> //Bot/2nd player ship setup
    var isInPortraitOrientation: Boolean = true


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
        hasShip: Int,
        onCellClicked: () -> Unit,
        isClickable: Boolean = true,
    ) {
        Image(
            painter = painterResource(
                id = when (hasShip){
                    1 -> R.drawable.saltwater
                    0 -> R.drawable.water
                    else -> R.drawable.explosion
                }
            ),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable(enabled = isClickable) { onCellClicked() }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        var timeRemaining by remember { mutableStateOf(MainActivity.State["InitialTime"].toString().toInt()) }
        val timed = MainActivity.State["Timed"]
        LaunchedEffect(Unit) {
            while(timeRemaining>0) {
                print(timeRemaining)
                delay(1000)
                timeRemaining--
            }
        }
        //just for testing purposes
        //player2Grid = player1Grid

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
                    enemyHasShipsUI= remember { mutableStateListOf() }
                    for (i in 0 until 100) enemyHasShipsUI.add(0)

                    grid = when(isYourTurn) {
                        true ->LazyVerticalGrid(
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
                                                //funció "playTurn"
                                                playTurn(it)
                                                endGame()
                                                isYourTurn = false
                                            }else{
                                                //funció "botTurn"
                                            }
                                        }
                                    )
                                }
                            }
                        )
                        false -> LazyVerticalGrid(
                            userScrollEnabled = false,
                            columns = GridCells.Fixed(10),
                            content = {
                                items(100) {
                                    TableCell(
                                        text = it.toString(),
                                        hasShip = when(player1Grid[it]){
                                            ShipType.WATER -> 0
                                            else -> 1},
                                        onCellClicked = {
                                            Toast.makeText(this@GameInterface, "changing turn", Toast.LENGTH_SHORT).show()
                                            isYourTurn = !isYourTurn },
                                        isClickable = true,
                                    )
                                }
                            }
                        )
                    }
                }
                Box(
                    modifier = Modifier.padding(40.dp)
                ) {
                    Column {
                        Text(text = if (isYourTurn) "Your table"
                        else "Enemy's table")
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .aspectRatio(1f)
                        )
                        {
                            grid = when(isYourTurn) {
                                false ->LazyVerticalGrid(
                                    userScrollEnabled = false,
                                    columns = GridCells.Fixed(10),
                                    content = {
                                        items(100) {
                                            TableCell(
                                                text = it.toString(),
                                                hasShip = enemyHasShipsUI[it],
                                                onCellClicked = {},
                                                isClickable = false,

                                            )
                                        }
                                    }
                                )
                                true -> LazyVerticalGrid(
                                    userScrollEnabled = false,
                                    columns = GridCells.Fixed(10),
                                    content = {
                                        items(100) {
                                            TableCell(
                                                text = it.toString(),
                                                hasShip = when(player1Grid[it]){
                                                    ShipType.WATER -> 0
                                                    else -> 1},
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
                        val enemyHasShipsUI: SnapshotStateList<Int> = remember { mutableStateListOf() }
                        for (i in 0 until 100) enemyHasShipsUI.add(0)

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
                                                if (player2Grid[it] != ShipType.WATER) {
                                                    Toast.makeText(
                                                        baseContext,
                                                        "tocat",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                    enemyHasShipsUI[it] = 3

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
                                                hasShip = 1, //isClickedState[it],
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
        if (player2Grid[cell] != ShipType.WATER) {
            Toast.makeText(
                baseContext,
                "Tocat",
                Toast.LENGTH_SHORT
            )
                .show()
            enemyHasShipsUI[cell] = 3
        }else{
            Toast.makeText(
                baseContext,
                "Aigua",
                Toast.LENGTH_SHORT
            ).show()
            enemyHasShipsUI[cell] = 1
        }
    }

    private fun endGame(){
        var count =0
        for (item in enemyHasShipsUI){
            if (item == 3) count++
            //(17 es el sumatori de sizes de tots els barcos, de moment esta cutre pero ja ho fem putamadre més tard)
            if (count == 17) startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}
