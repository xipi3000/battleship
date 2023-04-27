package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme

class SetUpYourShips : ComponentActivity() {

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
    ) {
        Image(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = "huhg",
            modifier = Modifier.matchParentSize()
                .clickable {
                    Toast
                        .makeText(
                            applicationContext,
                            text,
                            Toast.LENGTH_SHORT
                        ).show()
                }
                .padding(1.dp)
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
                .horizontalScroll()
        ) {
            items(10) {
                val id = it
                LazyRow(Modifier.fillMaxWidth()) {
                    items(10) {

                        TableCell(text = (count).toString())
                        count++
                    }
                }
            }
        }
    }
}




