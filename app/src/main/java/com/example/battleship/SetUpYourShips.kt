package com.example.battleship

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme

class SetUpYourShips : ComponentActivity() {
    lateinit var lastShip:Ship
    lateinit var grid:Unit
    lateinit var isClickedState: SnapshotStateList<Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                MainView()
            }
        }
    }

    @Composable
    fun TableCell(
        text: String,
        hasShip:Boolean,
        onCellClicked: () -> Unit,
    ) {
        Image(
            painter = painterResource(
                id = if(hasShip){
                    R.drawable.saltwater
                }else{
                    R.drawable.water
                }),
            contentDescription = text,
            contentScale= ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable {onCellClicked()}
        )
    }

    private fun calculateCoords(start:Int) {
        //implemented most basic version imaginable
        /* TODO:
            - Add ship orientation
        *       -> [V]ertical - [H]orizontal; 90º rotations alternating state; i have ideas to implement rotations
        *   - Check if valid position [de moment només canvia la imatge de la cela SEMPRE, encara que IndexOutOfBounds]
        *       -> Horizontally: ship fits without changing line (ex: ship.size = 3 -> if casella%10 != casella+size%10 no cap)
                                                                                                [_6=ultima on si cap a cada fila]
        *       -> Vertically: Ship fits in existing vertical lines (ex: ship.size = 3 -> if casella > (100-(size*10))+9 no cap)
                                                                                                [79=ultima on si cap]
        *       -> Non Already Occupied: if(isClickedState[all_cells] = false){putamadre} else {cagaste manin}
        *       ->Diria que ja no hi ha mes casos en que no hauria de poder
        *   */
        val array = arrayListOf<Int>()
        var size = lastShip.type.size
        while(size !=0){
            if (size == lastShip.type.size)array.add(start)
            else array.add(start + lastShip.type.size-size)
            size--
        }
        lastShip.coords = array
        for (item in array){
            isClickedState[item] = !isClickedState[item]
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        val carrier=Ship(ShipType.CARRIER)
        val battleship=Ship(ShipType.BATTLESHIP)
        val crusier=Ship(ShipType.CRUISER)
        val destroyer=Ship(ShipType.DESTROYER)
        val submarine=Ship(ShipType.SUBMARINE)
        isClickedState = remember { mutableStateListOf() }
        for (i in 0 until 100) {
            isClickedState.add(false)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Each cell of a column must have the same weight.
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
                                text= it.toString(),
                                hasShip = isClickedState[it],
                                onCellClicked = {
                                    calculateCoords(it)
                                }
                            )
                        }
                    }
                )

            }
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ){
                Image(painter = painterResource(id = R.drawable.carrier),
                    contentDescription = "Carrier",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = carrier }
                        .fillMaxWidth()
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.battleship),
                    contentDescription = "Battleship",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = battleship }
                        .fillMaxWidth()
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.cruiser),
                    contentDescription = "Cruiser",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = crusier }
                        .fillMaxWidth()
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.destroyer),
                    contentDescription = "Destroyer",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = destroyer }
                        .fillMaxWidth()
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.submarine),
                    contentDescription = "Submarine",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = submarine }
                        .fillMaxWidth()
                        .weight(1f))
            }
            Button(onClick = { lastShip.rotate() }) {
                Text(text="Rotate Ship")
            }

        }

        // The LazyColumn will be our table. Notice the use of the weights below
    }
}