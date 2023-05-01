package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
    private lateinit var lastShip:Ship
    private lateinit var grid:Unit
    private lateinit var gridContent: SnapshotStateList<ShipType>
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
                .clickable { onCellClicked() }
        )
    }

    private fun calculateCoords(start:Int) {
        //acceptable position check
        if (checkIfFits(start)){
            val new:ArrayList<Int> = if(lastShip.orientation == Orientation.Horizontal){
                newHorizontal(start)
            }else{
                newVertical(start)
            }
            if (freeSpace(new, lastShip.type))drawOnBoard(new)
            else  Toast.makeText(this, "One or more positions are already occupied by another boat", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "I'm sorry, but this ship won't fit there", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkIfFits(start:Int): Boolean{
        return when (lastShip.orientation){
            Orientation.Horizontal -> (start/10)%10 == ((start+(lastShip.type.size-1))/10)%10
            Orientation.Vertical -> start < 100-((lastShip.type.size-1)*10)
        }
    }

    private fun newVertical(initial: Int):ArrayList<Int> {
        val array = arrayListOf<Int>()
        var size = 0
        while(size!=lastShip.type.size){
            if (size==0)array.add(initial)
            else array.add(initial + 10*size)
            size++
        }
        return array
    }

    private fun newHorizontal(initial:Int):ArrayList<Int> {
        val array = arrayListOf<Int>()
        var size = lastShip.type.size
        while(size !=0){
            if (size == lastShip.type.size)array.add(initial)
            else array.add(initial + lastShip.type.size-size)
            size--
        }
        return array
    }

    private fun drawOnBoard(newPos:ArrayList<Int>) {
        //first time positioning -> position and draw
        if (!lastShip.hasBeenSet){
            lastShip.position(newPos)
            for (item in newPos){
                gridContent[item] = lastShip.type
            }
        }
        //not first time positioning -> eraseOld, positionNew and drawNew
        else{
            for (item in lastShip.coords){
                gridContent[item] = ShipType.WATER
            }
            lastShip.position(newPos)
            for (item in lastShip.coords){
                gridContent[item] = lastShip.type
            }
        }
    }

    private fun freeSpace(newPos:ArrayList<Int>, myBoat: ShipType):Boolean {
        var nonOccupied = true
        for (pos in newPos){
            if (gridContent[pos] != ShipType.WATER && gridContent[pos] != myBoat){
                nonOccupied = false
                break
            }
        }
        return nonOccupied
    }

    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        val carrier=Ship(ShipType.CARRIER)
        val battleship=Ship(ShipType.BATTLESHIP)
        val cruiser=Ship(ShipType.CRUISER)
        val destroyer=Ship(ShipType.DESTROYER)
        val submarine=Ship(ShipType.SUBMARINE)
        gridContent = remember { mutableStateListOf() }
        for (i in 0 until 100) {
            gridContent.add(ShipType.WATER)
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
                                hasShip = (gridContent[it]!=ShipType.WATER),
                                onCellClicked = {
                                    if (!::lastShip.isInitialized){
                                        Toast.makeText(this@SetUpYourShips, "Select a ship to position", Toast.LENGTH_SHORT).show()
                                    }else{
                                        calculateCoords(it)
                                    }
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
                        .clickable { lastShip = cruiser }
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
            Button(onClick = {
                if(lastShip.hasBeenSet) rotateShip()
                else Toast.makeText(this@SetUpYourShips, "First position the ship", Toast.LENGTH_SHORT).show()
            }) {
                Text(text="Rotate Ship")
            }
            Button(onClick = {
                //Ships must be set
                if (!carrier.hasBeenSet || !battleship.hasBeenSet || !cruiser.hasBeenSet || !destroyer.hasBeenSet || !submarine.hasBeenSet)
                    Toast.makeText(this@SetUpYourShips, "Please set up all ships", Toast.LENGTH_LONG).show()
                else {
                    //Store player grid content to access when playing
                    MainActivity.State = MainActivity.State + ("Player1Grid" to gridContent)
                    //Store 2nd player grid (bot or human must have different implementations)
                    startActivity(Intent(baseContext,GameInterface :: class.java))
                }
            }) {
                Text(text="Start Game")
            }
        }
        // The LazyColumn will be our table. Notice the use of the weights below
    }

    private fun rotateShip() {
        //calculate before to change orientation to new
        val newCoords = lastShip.rotate()
        //if correct draw
        if(checkIfFits(lastShip.coords[0]) && freeSpace(newCoords, lastShip.type)){
            drawOnBoard(newCoords)
        }//else revert (change orientation to old)
        else{ //podriem personalitzar el missatge d'error (segons quina de les dos condicions no es compleix)
            lastShip.rotate()
            Toast.makeText(this@SetUpYourShips, "Invalid rotation, please reposition ship", Toast.LENGTH_SHORT).show()
        }
    }

}
