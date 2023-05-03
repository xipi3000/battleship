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
import kotlin.random.Random

class SetUpYourShips : ComponentActivity() {
    private lateinit var lastShip:Ship
    private lateinit var grid:Unit
    private lateinit var playerGrid: SnapshotStateList<GridType>
    private lateinit var botGrid:SnapshotStateList<GridType>

    private enum class Player{
        PLAYER,
        BOT,
    }
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
        hasShip:CellState,
        onCellClicked: () -> Unit,
    ) {
        Image(
            painter = painterResource(
                id = if(hasShip == CellState.UNKNOWN){
                    R.drawable.undiscovered
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

    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        //initialize ships, one of each kind
        val carrier=Ship(GridType.CARRIER)
        val battleship=Ship(GridType.BATTLESHIP)
        val cruiser=Ship(GridType.CRUISER)
        val destroyer=Ship(GridType.DESTROYER)
        val submarine=Ship(GridType.SUBMARINE)

        //initialize both grids (for now) and make them all water
        playerGrid = remember {mutableStateListOf()}
        botGrid = remember{mutableStateListOf()}
        for (i in 0 until 100) {
            playerGrid.add(GridType.WATER)
        }
        for (i in 0 until 100) {
            botGrid.add(GridType.WATER)
        }

        //Assign player (de moment 1, si volem fer-ne mes ja mirem com ho fem)
        //->Mirar si es contra player o contra bot -> si contra player -> mirar si first time

        val player = Player.PLAYER
        val player2:Boolean = MainActivity.State["VersusBot"] as Boolean
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            //Player grid
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
                                //playerGrid[it]!=GridType.WATER
                                hasShip = (when(playerGrid[it]){
                                    GridType.WATER -> CellState.WATER
                                    else-> CellState.UNKNOWN
                                }),
                                onCellClicked = {
                                    if (!::lastShip.isInitialized){
                                        Toast.makeText(this@SetUpYourShips, "Select a ship to position", Toast.LENGTH_SHORT).show()
                                    }else{
                                        /*function that calculates,evaluates the ship's new position
                                        and, if everything is correct, stores the value*/
                                        calculateCoords(it, lastShip, player, playerGrid)
                                    }
                                }
                            )
                        }
                    }
                )
            }
            //Buttons for ship positioning
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
                Image(painter = painterResource(id = R.drawable.submarine),
                    contentDescription = "Submarine",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = submarine }
                        .fillMaxWidth()
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.destroyer),
                    contentDescription = "Destroyer",
                    contentScale= ContentScale.FillHeight,
                    modifier= Modifier
                        .clickable { lastShip = destroyer }
                        .fillMaxWidth()
                        .weight(1f))
            }
            //Functionality buttons
            Button(onClick = {
                if(lastShip.hasBeenSet) rotateShip()
                else lastShip.newOrientation(when(lastShip.orientation){
                    Orientation.Horizontal -> Orientation.Vertical
                    Orientation.Vertical -> Orientation.Horizontal
                })
            }) {
                Text(text="Rotate Ship")
            }
            Button(onClick = {
                //Ships must be set
                if (!carrier.hasBeenSet || !battleship.hasBeenSet || !cruiser.hasBeenSet || !destroyer.hasBeenSet || !submarine.hasBeenSet)
                    Toast.makeText(this@SetUpYourShips, "Please set up all ships", Toast.LENGTH_LONG).show()
                else {
                    //Store player grid content to access when playing
                    val playerGridShips:ArrayList<Int> = arrayListOf()
                    for((cell, cellType) in playerGrid.withIndex()){
                        if (cellType!=GridType.WATER)
                            playerGridShips.add(cell)
                    }
                    MainActivity.State = MainActivity.State + ("Player1Ships" to playerGridShips)
                    MainActivity.State = MainActivity.State + ("Player1Grid" to playerGrid)
                    //Store 2nd player grid (bot or human must have different implementations)
                    if(player2){
                        val randGrid = randomSetup()
                        val botGridShips:ArrayList<Int> = arrayListOf()
                        for((cell, cellType) in botGrid.withIndex()){
                            if (cellType!=GridType.WATER)
                                botGridShips.add(cell)
                        }
                        MainActivity.State = MainActivity.State + ("Player2Ships" to botGridShips)
                        MainActivity.State = MainActivity.State + ("Player2Grid" to botGrid)
                        startActivity(Intent(baseContext,GameInterface :: class.java))
                    }else{
                        /*TODO: gestionar com ho fem
                        *  -> Fiquem una foto que es fiqui davant de tot per deixar que es passin el movil
                        *  -> Boolean "changing" per gestionar que es vegi o no
                        *  -> Boolean "player1set" per gestionar quina grid montem i guardem
                        *  -> Un cop guardad grid2 -> start activity*/
                    }
                }
            }) {
                Text(text="Start Game")
            }
        }
    }

/* CALCULATE AND STORE VALUES FOR THE NEW POSSIBLE POSITION OF A SHIP */
    /*Method used to calculate the new position of the ship, check if it's correct and not already
    * occupied, and in case all of this happens, store the new values*/
    private fun calculateCoords(start:Int, ship:Ship, player:Player, grid: SnapshotStateList<GridType>) {
        if (checkIfFits(start, ship)){
            val new:ArrayList<Int> = newPosition(start, ship)
            if (!freeSpace(new, ship.type, grid) && (player != Player.BOT))
                Toast.makeText(this, "One or more positions are already occupied by another boat", Toast.LENGTH_SHORT).show()
            else placeOnBoard(new, ship, grid)
        } else{
            if (player != Player.BOT)Toast.makeText(this, "I'm sorry, but this ship won't fit there", Toast.LENGTH_SHORT).show()
        }
    }

    /*We use the same method for both orientations changing the 'modifier' value depending on which
    * orientation the ship "grows"*/
    private fun newPosition(initial: Int, ship: Ship):ArrayList<Int> {
        val array = arrayListOf<Int>()
        val modifier = when(ship.orientation){
            Orientation.Horizontal-> 1
            Orientation.Vertical-> 10
        }
        var size = 0
        while(size!=ship.type.size){
            array.add(initial + modifier*size)
            size++
        }
        return array
    }

    /*We use this function when all checks have been cleared to store the new coordinates both on
    * the ship and the grid*/
    private fun placeOnBoard(newPos:ArrayList<Int>, ship: Ship, grid: SnapshotStateList<GridType>) {
        if (!ship.hasBeenSet){ //first time positioning -> storeCoords and positionOnGrid
            ship.position(newPos)
            for (item in newPos){
                grid[item] = ship.type
            }
        }else{//not first time -> removeOldPositioning, storeCoords and positionNewOnGrid
            for (item in ship.coords){
                grid[item] = GridType.WATER
            }
            ship.position(newPos)
            for (item in ship.coords){
                grid[item] = ship.type
            }
        }
    }

/* CHECK IF THE NEW POSITION IS CORRECT AND AVAILABLE */
    /*Checks if the position that is going to be calculated is correct:
    *   -Horizontal -> ship is only on one row
    *   -Vertical -> ship doesn't go below the grid*/
    private fun checkIfFits(start:Int, ship: Ship): Boolean{
        return when (ship.orientation){
            Orientation.Horizontal -> (start/10)%10 == ((start+(ship.type.size-1))/10)%10
            Orientation.Vertical -> start < 100-((ship.type.size-1)*10)
        }
    }

    /*We use this function to check if, from the new coordinates of a ship, any of them is already
    * occupied by another ship*/
    private fun freeSpace(newPos:ArrayList<Int>, myBoat: GridType, grid:SnapshotStateList<GridType>):Boolean {
        var nonOccupied = true
        for (pos in newPos){
            if (grid[pos] != GridType.WATER && grid[pos] != myBoat){
                nonOccupied = false
                break
            }
        }
        return nonOccupied
    }

/* OTHER FUNCTIONALITIES */
    /*We use this function to rotate the ship we are placing the player's ships on the grid*/
    private fun rotateShip() {
        //Calculate new possible coords and adjust orientation
        val newCoords = lastShip.rotate()
        //If the new position isn't available, revert to original state and tell user what went wrong
        if(!checkIfFits(lastShip.coords[0],lastShip)){
            lastShip.rotate()
            Toast.makeText(this@SetUpYourShips, "The ship wouldn't fit in the new position", Toast.LENGTH_SHORT).show()
        }else if(!freeSpace(newCoords, lastShip.type, playerGrid)){
            lastShip.rotate()
            Toast.makeText(this@SetUpYourShips, "One or more of cells are already occupied", Toast.LENGTH_SHORT).show()
        }else{
            //If checks are passed, update to new values
            placeOnBoard(newCoords, lastShip, playerGrid)
        }
    }

    /*We use this function to generate a random ship setup for the bot*/
    private fun randomSetup(): SnapshotStateList<GridType>{
        Toast.makeText(this, "Generating random board, please wait", Toast.LENGTH_LONG).show()
        val ships = arrayListOf(GridType.CRUISER, GridType.SUBMARINE, GridType.DESTROYER, GridType.BATTLESHIP, GridType.CARRIER)
        var set:Boolean
        var ship:Ship
        var randPos:Int
        var randOr:Int
        for (i in 0..4) {
            ship=Ship(ships[i])
            set=false
            while(!set){//Until we get an available positioning for the ship
                //get random orientation and random initial position
                randPos = Random.nextInt(0,99)
                randOr = Random.nextInt(0,9)
                when(randOr%2){
                    0 -> ship.newOrientation(Orientation.Horizontal)
                    1 -> ship.newOrientation(Orientation.Vertical)
                }
                //try to place (calculateCoords already checks for us)
                calculateCoords(randPos, ship, Player.BOT, botGrid)
                //if there were no problems -> nextShip; else -> try again
                if(ship.hasBeenInitialized()){
                    set = true
                }
            }
        }
        Toast.makeText(this, "Board created, starting game", Toast.LENGTH_SHORT).show()
        return botGrid
    }
}