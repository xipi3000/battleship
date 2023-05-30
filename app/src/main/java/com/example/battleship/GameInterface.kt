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
import android.icu.text.ListFormatter.Width
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.RectangleShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.StringTokenizer

class LogText(time: Int, casellaSel: String, isTocat: Boolean) {
    var time: Int = time

    var casellaSel: String = casellaSel

    var isTocat: Boolean = isTocat

    fun print(): String {
        return "Casella selecccionada: $casellaSel\n " +
                if (isTocat) "Vaixell enemic tocat\n" else "Has tocat aigua\n" +
                        "Temps: $time\n"
    }
}

@Suppress("UNCHECKED_CAST")
class GameInterface : ComponentActivity() {
    private lateinit var enemyHasShipsUI: SnapshotStateList<CellState>
    private lateinit var playerHasShipsUI: SnapshotStateList<CellState>
    private lateinit var cellsShot: SnapshotStateList<Boolean>
    private var player1ships =
        GameConfiguration.State["Player1Ships"] as ArrayList<Int> //Player's ship setup
    private var player2ships =
        GameConfiguration.State["Player2Ships"] as ArrayList<Int> //Bot/2nd Player's ship setup
    private var player1Grid = SetUpYourShips.Grids["player1Grid"] as ArrayList<CellState>
    private var player2Grid = SetUpYourShips.Grids["player2Grid"] as ArrayList<CellState>
    private var cellsShotSave = SetUpYourShips.Grids["cellsShot"] as ArrayList<Boolean>
    private var isInPortraitOrientation: Boolean = true
    private lateinit var enemy: Enemy
    private var isYourTurn: Boolean = true
    private lateinit var timeRemaining: MutableState<Int>
    private var logPartida = mutableListOf<String>()
    private lateinit var logListState: LazyListState

    val logSaver = Saver<SnapshotStateList<String>,List<String>>(
        save = {
            it.toList()
        },
        restore = {
            it.toMutableStateList()
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                isInPortraitOrientation = when (LocalConfiguration.current.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        false
                    }

                    else -> {
                        true
                    }
                }
                val firstLog: String =
                    "Alias: " + GameConfiguration.State["Alias"] + "\n" + "Num cells: 100\n" + "Num ships: 5\n" + "Total time: " + GameConfiguration.State["MaxTime"] + "\n"

                logPartida = rememberSaveable(saver= logSaver) {
                    mutableStateListOf(firstLog)
                }

                logListState = rememberLazyListState()
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
        val onClick = {
            if (cellsShot[text.toInt()]) Toast.makeText(
                this,
                "This cell has already been fired",
                Toast.LENGTH_SHORT
            ).show()
            else {
                cellsShot[text.toInt()] = true

                onCellClicked()
            }
        }

        Image(
            painter = painterResource(
                id = when (hasShip) {
                    CellState.WATER -> R.drawable.water
                    CellState.UNKNOWN -> R.drawable.undiscovered
                    CellState.SHIPFOUND -> R.drawable.explosion
                    CellState.SHIPHIDDEN -> R.drawable.isship
                    CellState.OUTOFBOUNDS -> R.drawable.water //no s'utilitza pero es necessita per el when
                    CellState.BATTLESHIP -> R.drawable.water
                }
            ),
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                //.fillMaxWidth()
                .clickable(enabled = isClickable) { onClick() }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {

        timeRemaining = remember { mutableStateOf(GameConfiguration.State["ActualTime"] as Int) }
        val timed = GameConfiguration.State["Timed"]
        LaunchedEffect(Unit) {
            while (timeRemaining.value > 0) {
                print(timeRemaining.value)
                delay(1000)
                timeRemaining.value--
            }
            //if time=0 -> finish game
            GameConfiguration.State = GameConfiguration.State + ("FinalTime" to timeRemaining.value)
            startActivity(Intent(this@GameInterface, ResultActivity::class.java))
        }
        if (!::enemyHasShipsUI.isInitialized || !::playerHasShipsUI.isInitialized) {
            enemyHasShipsUI = remember { mutableStateListOf() }
            playerHasShipsUI = remember { mutableStateListOf() }
            cellsShot = remember { mutableStateListOf() }
        }
        for (i in 0 until 100) {
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
    private fun ShowScreenContent(timed: Boolean) {
        return Column(verticalArrangement = Arrangement.SpaceEvenly)
        {
            val modifier : Modifier
            if(timed) modifier =Modifier.height(90.dp)
            else modifier =Modifier.height(60.dp)
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(if (timeRemaining.value > 0) Color.Gray else Color.Red)
                    ,

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
                            "Time remaining: " + timeRemaining.value,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            val configuration = LocalConfiguration.current
            var screenWidth = configuration.screenWidthDp
            when (screenWidth.dp < 600.dp) {
                true -> {
                    Column {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .aspectRatio(1f),
                        ) { BigGridComponent() }
                        Box(
                            modifier = Modifier.padding(40.dp)
                        ) {
                            Column {
                                Text(text = "Your table")
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .aspectRatio(1f)
                                ) { SmallGridComponent() }
                            }
                        }
                    }
                }

                false -> {
                    val configuration = LocalConfiguration.current
                    when (configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT -> {
                            Column(
                                modifier = Modifier,
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                BoxWithConstraints(

                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxHeight(fraction = 0.5f)
                                        .fillMaxWidth()
                                        .wrapContentSize(
                                            Alignment.Center
                                        )
                                ) {

                                    BigGridComponent(
                                        modifier = Modifier
                                            .heightIn(0.dp, maxHeight)
                                            .widthIn(0.dp, maxWidth)
                                            .aspectRatio(1f)
                                            .fillMaxSize(),
                                    )
                                }
                                BoxWithConstraints(

                                    modifier = Modifier
                                        .fillMaxHeight(fraction = 1f)
                                        .fillMaxWidth()
                                        .wrapContentSize(
                                            Alignment.Center
                                        )

                                ) {
                                    val maxHeight = maxHeight
                                    val maxWidth = maxWidth
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column(modifier = Modifier.padding(40.dp)) {

                                            Text(text = "Your table")
                                            SmallGridComponent(
                                                modifier = Modifier
                                                    .heightIn(0.dp, maxHeight)
                                                    .widthIn(0.dp, maxWidth - 250.dp)
                                                    .aspectRatio(1f)
                                                    .fillMaxSize(),
                                            )
                                        }
                                        GameLogComponent()


                                    }
                                }
                            }
                        }


                        else -> {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                BoxWithConstraints(

                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(0.5f)
                                        .fillMaxHeight()
                                        .wrapContentSize(
                                            Alignment.Center
                                        )
                                ) {

                                    BigGridComponent(
                                        modifier = Modifier
                                            .heightIn(0.dp, maxHeight)
                                            .widthIn(0.dp, maxWidth)
                                            .aspectRatio(1f)
                                            .fillMaxSize(),
                                    )
                                }
                                BoxWithConstraints(

                                    modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .fillMaxHeight()
                                        .wrapContentSize(
                                            Alignment.Center
                                        )

                                ) {
                                    val maxHeight = maxHeight
                                    val maxWidth = maxWidth
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp)) {

                                            Text(text = "Your table")
                                            SmallGridComponent(
                                                modifier = Modifier
                                                    .heightIn(0.dp, maxHeight)
                                                    .widthIn(0.dp, maxWidth - 250.dp)
                                                    .aspectRatio(1f)
                                                    .fillMaxSize(),
                                            )
                                        }
                                        GameLogComponent()


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
    private fun GameLogComponent(

    ) {
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

    @Composable
    private fun SmallGridComponent(modifier: Modifier = Modifier) {
        return LazyVerticalGrid(
            modifier = modifier,
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
    private fun BigGridComponent(
        modifier: Modifier = Modifier
    ) {
        LaunchedEffect(logPartida.size) {
            if (logPartida.size != 0) logListState.animateScrollToItem(logPartida.size - 1)

        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        LazyVerticalGrid(
            // modifier = Modifier.size(if (screenWidth > screenHeight) (screenHeight * 0.8).dp else screenWidth.dp),
            modifier = modifier,
            userScrollEnabled = false,
            columns = GridCells.Fixed(10),
            content = {
                items(100) {
                    TableCell(
                        text = it.toString(),
                        hasShip = enemyHasShipsUI[it],
                        onCellClicked = {
                            //player's shot
                            val res = playTurn(it)
                            logPartida.add(res.print())



                            isYourTurn = !isYourTurn
                            //hauriem de mirar de fer que, d'alguna manera, s'actualitzés la grid
                            //abans del bot shot


                            //bot's shot
                            botTurn()
                            isYourTurn = !isYourTurn


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
        val parsedCell = cell.first * 10 + cell.second
        Log.i("BotCell", "Shooting $parsedCell")
        val infoCell = if (parsedCell in player1ships) {
            CellState.SHIPFOUND
        } else {
            CellState.WATER
        }
        Log.i("BotCell", "It had $infoCell")
        enemy.checkCell(cell, infoCell)

        if (infoCell == CellState.SHIPFOUND) {
            playerHasShipsUI[parsedCell] = CellState.SHIPFOUND
            player1ships.remove(parsedCell)
        } else {
            playerHasShipsUI[parsedCell] = CellState.WATER
        }

    }

    private fun playTurn(cell: Int): LogText {
        var cela = ""
        var isTocat = false
        if (cell in player2ships) {
            enemyHasShipsUI[cell] = CellState.SHIPFOUND
            //Remove shipcell from state
            player2ships.remove(cell)

            isTocat = true
            //GameConfiguration.State = GameConfiguration.State + ("Player2Ships" to player2ships)
        } else {
            enemyHasShipsUI[cell] = CellState.WATER
            isTocat = false
        }
        val row = cell / 10
        val col = cell % 10
        cela = "($row-$col)"

        return LogText(time = timeRemaining.value, casellaSel = cela, isTocat = isTocat)
    }

    private fun endGame() {
        if (player1ships.isEmpty() || player2ships.isEmpty()) {
            GameConfiguration.State = GameConfiguration.State + ("FinalTime" to timeRemaining)
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    override fun onDestroy() {
        //Update gameData
        for (i in 0 until 100) {
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
        super.onDestroy()
    }
}