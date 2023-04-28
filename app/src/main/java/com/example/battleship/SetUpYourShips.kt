package com.example.battleship

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
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
                            TableCell("")

                        }
                    }
                )
            }

        }
        // The LazyColumn will be our table. Notice the use of the weights below
    }
}