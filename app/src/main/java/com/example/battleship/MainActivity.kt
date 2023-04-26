package com.example.battleship

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme
import java.util.Dictionary

class MainActivity : ComponentActivity() {
    companion object {
        var State = mapOf("Rows" to 10, "NumShips" to 3)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                // A surface container using the 'background' color from the theme
                MainView()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var context = LocalContext.current
            Text(text = "Battlehip", fontWeight = FontWeight.Bold, color = Color.Gray)
            Button(onClick = {context.startActivity(Intent(context,SetUpYourShips::class.java)) }) {
                Text(text = "Play")
            }
            Button(onClick = { context.startActivity(Intent(context,Configuration::class.java)) }) {
                Text(text = "Configuration")
            }
        }


    }
}