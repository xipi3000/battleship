package com.example.battleship

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
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
        val width: Int = resources.displayMetrics.widthPixels / 28
        val height: Int = resources.displayMetrics.heightPixels / 28
        Image(painter = painterResource(id = R.drawable.saltwater), contentDescription = "huhg",
            contentScale= ContentScale.FillBounds,
            modifier = Modifier
                .size(if(width<height) width.dp else height.dp)
                .clickable {
                    Toast
                        .makeText(
                            applicationContext,
                            text,
                            Toast.LENGTH_SHORT
                        ).show()
                }
                .padding(1.dp),
        )
    }
    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        val tableData = (1..100).mapIndexed { index, item ->
            index to "Item $index"
        }
        // Each cell of a column must have the same weight.

        // The LazyColumn will be our table. Notice the use of the weights below
        var count = 0
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(10) {
                val id = it
                LazyRow(Modifier.fillParentMaxWidth()) {
                    items(10) {
                        TableCell(text = (count).toString())
                        count++
                    }
                }
            }
        }
    }
}