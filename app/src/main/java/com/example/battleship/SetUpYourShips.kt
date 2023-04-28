package com.example.battleship

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme

class SetUpYourShips : ComponentActivity() {

    // Calculate the desired dimensions for the image
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
        text: String
    ) {
        Image(painter = painterResource(id = R.drawable.water), contentDescription = "huhg",
            contentScale= ContentScale.FillBounds,
            modifier = Modifier
                .padding(1.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
                .clickable {
                    Toast
                        .makeText(
                            applicationContext,
                            text,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
        )
    }
    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        lateinit var lastShip:Ship
        val carrier=Ship(ShipType.CARRIER)
        val battleship=Ship(ShipType.BATTLESHIP)
        val crusier=Ship(ShipType.CRUISER)
        val destroyer=Ship(ShipType.DESTROYER)
        val submarine=Ship(ShipType.SUBMARINE)
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
                LazyVerticalGrid(
                    userScrollEnabled = false,
                    columns = GridCells.Fixed(10),
                    content = {
                        items(100) {
                            TableCell(it.toString())
                        }
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ){
                Image(painter = painterResource(id = R.drawable.carrier),
                    contentDescription = "Carrier",
                    contentScale= ContentScale.FillHeight,
                    modifier=Modifier.clickable{lastShip=carrier}.fillMaxWidth().weight(1f))
                Image(painter = painterResource(id = R.drawable.battleship),
                    contentDescription = "Battleship",
                    contentScale= ContentScale.FillHeight,
                    modifier=Modifier.clickable{lastShip=battleship}.fillMaxWidth().weight(1f))
                Image(painter = painterResource(id = R.drawable.cruiser),
                    contentDescription = "Cruiser",
                    contentScale= ContentScale.FillHeight,
                    modifier=Modifier.clickable{lastShip=crusier}.fillMaxWidth().weight(1f))
                Image(painter = painterResource(id = R.drawable.destroyer),
                    contentDescription = "Destroyer",
                    contentScale= ContentScale.FillHeight,
                    modifier=Modifier.clickable{lastShip=destroyer}.fillMaxWidth().weight(1f))
                Image(painter = painterResource(id = R.drawable.submarine),
                    contentDescription = "Submarine",
                    contentScale= ContentScale.FillHeight,
                    modifier=Modifier.clickable{lastShip=submarine}.fillMaxWidth().weight(1f))
            }
            Button(onClick = { lastShip.rotate() }) {
                Text(text="Rotate Ship")
            }

        }

        // The LazyColumn will be our table. Notice the use of the weights below
    }
}