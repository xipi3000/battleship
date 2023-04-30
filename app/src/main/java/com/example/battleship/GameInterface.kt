package com.example.battleship

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

class GameInterface : ComponentActivity() {
    lateinit var lastShip: Ship //tecnicament lateinit no es null
    lateinit var grid: Unit
    lateinit var isClickedState: SnapshotStateList<Boolean>//TODO agafar els anteriors que hem fet setup
    lateinit var enemyHasShips: SnapshotStateList<Boolean>//TODO això és la informació del tablero enemic, ens la hem de guardar
    var isInPortraitOrientation: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {

                when(LocalConfiguration.current.orientation){
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        isInPortraitOrientation=false
                    }
                    else -> {
                        isInPortraitOrientation=true
                    }
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
                id = if (hasShip==1) {
                    R.drawable.saltwater
                }
                else if (hasShip==0){
                    R.drawable.water
                }
                else{
                    R.drawable.explosion
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
        var timeRemainig by remember { mutableStateOf(60) }
        LaunchedEffect(Unit) {
            while(timeRemainig>0) {
                print(timeRemainig)
                delay(1000)
                timeRemainig--
            }
        }
        //because of preview
        enemyHasShips = remember { mutableStateListOf() }
        for (i in 0 until 100) {
            enemyHasShips.add(false)
        }
        enemyHasShips[99] = true


        val carrier = Ship(ShipType.CARRIER)
        val battleship = Ship(ShipType.BATTLESHIP)
        val crusier = Ship(ShipType.CRUISER)
        val destroyer = Ship(ShipType.DESTROYER)
        val submarine = Ship(ShipType.SUBMARINE)
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
                        .background(if (timeRemainig > 0) Color.Gray else Color.Red)
                        .height(90.dp),
                    contentAlignment = Alignment.Center,


                ) {
                    Column() {
                        Text(
                            text =
                            if (isYourTurn) "Your turn to fire"
                            else "Enemys turn",
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,

                            )
                        Text(
                            text =
                            "Time remaining: "+timeRemainig,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            )
                    }

                }
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .aspectRatio(1f)
                )
                {
                    var enemyHasShipsUI: SnapshotStateList<Int>
                    enemyHasShipsUI = remember { mutableStateListOf() }
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
                                            if (enemyHasShips[it]) {
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
                    Column() {
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
                        var enemyHasShipsUI: SnapshotStateList<Int>
                        enemyHasShipsUI = remember { mutableStateListOf() }
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
                                                if (enemyHasShips[it]) {
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
                        Column() {
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

}
