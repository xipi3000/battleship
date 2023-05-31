package com.example.battleship

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.random.Random


class SetUpYourShips : ComponentActivity() {
    private lateinit var lastShip:Ship
    private lateinit var grid:Unit
    private lateinit var playerGrid: SnapshotStateList<GridType>
    private lateinit var botGrid: SnapshotStateList<GridType>
    private lateinit var carrier:Ship
    private lateinit var battleship:Ship
    private lateinit var cruiser:Ship
    private lateinit var destroyer:Ship
    private lateinit var submarine:Ship


    companion object {
        var Grids = mapOf(
            "player1Grid" to ArrayList<CellStateInter>(),
            "player2Grid" to ArrayList<CellState>(),
            "cellsShot" to ArrayList<Boolean>(),
            "ships" to ArrayList<Ship>(),
        )
    }

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
        cell:GridType,
        onCellClicked: () -> Unit,
    ) {
        Image(
            painter = painterResource(
                id = (if(cell.type == CellType.WATER){
                    R.drawable.water
                }else{
                    Log.i("TAG", "TableCell: "+cell.pos)
                    cell.type.ress[cell.pos]
                })
            ),
            contentDescription = text,
            contentScale= ContentScale.FillBounds,
            modifier = Modifier
                .rotate(if (cell.orientation == Orientation.Vertical) 90.0f else 0.0f)
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .paint(painterResource(id = R.drawable.water), contentScale = ContentScale.FillWidth)
                .clickable { onCellClicked() }
        )
    }

    @SuppressLint("SimpleDateFormat")
    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        //initialize both grids (for now) and make them all water
        playerGrid = remember {mutableStateListOf()}
        botGrid = remember{mutableStateListOf()}
        val player1G = Grids["player1Grid"] as ArrayList<GridType>
        for (i in 0 until 100){
            if (Grids["player1Grid"]?.isEmpty() == true){
                playerGrid.add(GridType(CellType.WATER))
            }else{
                playerGrid.add(player1G[i])
            }
            botGrid.add(GridType(CellType.WATER))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //Player grid
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .weight(1f)
            )
            {
                grid =UsersBoatsGrid()
            }
                ShipsButtonsComponent()
        }
    }

    @Composable
    private fun UsersBoatsGrid(){
        val player = Player.PLAYER
        return LazyVerticalGrid(
            userScrollEnabled = false,
            columns = GridCells.Fixed(10),
            content = {
                items(100) {
                    TableCell(
                        text= it.toString(),
                        cell = (playerGrid[it]),
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

    @SuppressLint("SimpleDateFormat")
    @Composable
    private fun ShipsButtonsComponent(){
        //Buttons for ship positioning
        if (Grids["ships"]?.isEmpty() == true){
            carrier=Ship(GridType(CellType.CARRIER))
            battleship=Ship(GridType(CellType.BATTLESHIP))
            cruiser=Ship(GridType(CellType.CRUISER))
            destroyer=Ship(GridType(CellType.DESTROYER))
            submarine=Ship(GridType(CellType.SUBMARINE))
        }else{
            val ships = Grids["ships"] as ArrayList<Ship>
            carrier = ships[0]
            battleship = ships[1]
            cruiser = ships[2]
            destroyer = ships[3]
            submarine = ships[4]
            lastShip = if(ships[5].type == GridType(CellType.WATER)){carrier}
            else {ships[5]}
        }

        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp

        val versusBot:Boolean = GameConfiguration.State["VersusBot"] as Boolean
        return Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,)
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Image(painter = painterResource(id = R.drawable.carrier),
                    contentDescription = "Carrier",
                    contentScale= ContentScale.Fit,
                    modifier= Modifier
                        .clickable { lastShip = carrier }
                        .size((screenHeight * 0.06).dp)
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.battleship),
                    contentDescription = "Battleship",
                    contentScale= ContentScale.Fit,
                    modifier= Modifier
                        .clickable { lastShip = battleship }
                        .size((screenHeight * 0.06).dp)
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.cruiser),
                    contentDescription = "Cruiser",
                    contentScale= ContentScale.Fit,
                    modifier= Modifier
                        .clickable { lastShip = cruiser }
                        .size((screenHeight * 0.06).dp)
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.submarine),
                    contentDescription = "Submarine",
                    contentScale= ContentScale.Fit,
                    modifier= Modifier
                        .clickable { lastShip = submarine }
                        .size((screenHeight * 0.06).dp)
                        .weight(1f))
                Image(painter = painterResource(id = R.drawable.destroyer),
                    contentDescription = "Destroyer",
                    contentScale= ContentScale.Fit,
                    modifier= Modifier
                        .clickable { lastShip = destroyer }
                        .size((screenHeight * 0.06).dp)
                        .weight(1f))
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                    if(lastShip.hasBeenSet) rotateShip()
                    else lastShip.newOrientation(when(lastShip.orientation){
                        Orientation.Horizontal -> Orientation.Vertical
                        Orientation.Vertical -> Orientation.Horizontal
                    })
                }) {
                    Text(text="Rotate Ship")
                }
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                    //Ships must be set
                    if (!carrier.hasBeenSet || !battleship.hasBeenSet || !cruiser.hasBeenSet || !destroyer.hasBeenSet || !submarine.hasBeenSet)
                        Toast.makeText(this@SetUpYourShips, "Please set up all ships", Toast.LENGTH_LONG).show()
                    else {
                        //Store player grid content to access when playing
                        val playerGridShips:ArrayList<Int> = arrayListOf()
                        for((cell, cellType) in playerGrid.withIndex()){
                            if (cellType.type!=CellType.WATER)
                                playerGridShips.add(cell)
                        }
                        GameConfiguration.State = GameConfiguration.State + ("Player1Ships" to playerGridShips)
                        //Store 2nd player grid (bot or human must have different implementations)
                        if(versusBot){ //bot
                            randomSetup()
                            val botGridShips:ArrayList<Int> = arrayListOf()
                            for((cell, cellType) in botGrid.withIndex()){
                                if (cellType.type!=CellType.WATER)
                                    botGridShips.add(cell)
                            }
                            GameConfiguration.State = GameConfiguration.State + ("Player2Ships" to botGridShips)
                            val formatter = SimpleDateFormat("HH:mm yyyy-MM-dd")
                            GameConfiguration.State = GameConfiguration.State + ("StartTime" to formatter.format(Calendar.getInstance().time))
                            saveGrids()
                            startActivity(Intent(baseContext,GameInterface :: class.java))
                        }else{ //second player
                            /* TODO: gestionar com ho fem
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
    }


    private fun saveGrids() {
        val player1Grid:ArrayList<CellStateInter> = arrayListOf()
        val player2Grid:ArrayList<CellState> = arrayListOf()
        val cellsShot = arrayListOf<Boolean>()
        for (item in playerGrid){
            if (item.type != CellType.WATER){
                player1Grid.add(CellStateInter(CellState.SHIPHIDDEN,item.type.ress[item.pos],item.orientation))
            }else{
                player1Grid.add(CellStateInter(CellState.WATER))
            }
            player2Grid.add(CellState.UNKNOWN)
            cellsShot.add(false)
        }
        Grids = Grids + ("player1Grid" to player1Grid)
        Grids = Grids + ("player2Grid" to player2Grid)
        Grids = Grids + ("cellsShot" to cellsShot)
    }
    private fun midSaveGrids() {
        val player1Grid:ArrayList<GridType> = arrayListOf()
        for (item in playerGrid){
            player1Grid.add(item)
        }
        Grids = Grids + ("player1Grid" to player1Grid)
        Grids = if(::lastShip.isInitialized) Grids + ("ships" to arrayListOf(carrier, battleship, cruiser, destroyer, submarine, lastShip))
        else Grids + ("ships" to arrayListOf(carrier, battleship, cruiser, destroyer, submarine, Ship(
            GridType(CellType.WATER)
        )))
    }

    /* CALCULATE AND STORE VALUES FOR THE NEW POSSIBLE POSITION OF A SHIP */
    /*Method used to calculate the new position of the ship, check if it's correct and not already
    * occupied, and in case all of this happens, store the new values*/
    private fun calculateCoords(start:Int, ship:Ship, player:Player, grid: SnapshotStateList<GridType>) {
        if (checkIfFits(start, ship)){
            val new:ArrayList<Int> = newPosition(start, ship)
            if (!freeSpace(new, ship.type, grid)){
                //cant put in the previous if because then it wouldn't check for the bot
                if(player != Player.BOT)Toast.makeText(this, "One or more positions are already occupied by another boat", Toast.LENGTH_SHORT).show()
            }
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
        while(size!=ship.type.type.size){
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
            for ((count, item) in newPos.withIndex()){
                grid[item] = GridType(ship.type.type,count,ship.orientation)
            }
        }else{//not first time -> removeOldPositioning, storeCoords and positionNewOnGrid
            for (item in ship.coords){
                grid[item] = GridType(CellType.WATER)
            }
            ship.position(newPos)
            for ((count, item) in ship.coords.withIndex()){
                grid[item] = GridType(ship.type.type,count,ship.orientation)
            }
        }
    }

/* CHECK IF THE NEW POSITION IS CORRECT AND AVAILABLE */
    /*Checks if the position that is going to be calculated is correct:
    *   -Horizontal -> ship is only on one row
    *   -Vertical -> ship doesn't go below the grid*/
    private fun checkIfFits(start:Int, ship: Ship): Boolean{
        return when (ship.orientation){
            Orientation.Horizontal -> (start/10)%10 == ((start+(ship.type.type.size-1))/10)%10
            Orientation.Vertical -> start < 100-((ship.type.type.size-1)*10)
        }
    }

    /*We use this function to check if, from the new coordinates of a ship, any of them is already
    * occupied by another ship*/
    private fun freeSpace(newPos:ArrayList<Int>, myBoat: GridType, grid:SnapshotStateList<GridType>):Boolean {
        var nonOccupied = true
        for (pos in newPos){
            if (grid[pos].type != CellType.WATER && grid[pos].type != myBoat.type){
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
        val ships = arrayListOf(GridType(CellType.CRUISER), GridType(CellType.SUBMARINE), GridType(CellType.DESTROYER), GridType(CellType.BATTLESHIP), GridType(CellType.CARRIER))
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
                //try to place
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

    override fun onDestroy() {
        midSaveGrids()
        super.onDestroy()
    }
}