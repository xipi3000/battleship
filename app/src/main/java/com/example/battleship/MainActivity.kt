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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    companion object {
        var State = mapOf("Rows" to 10,
            "NumShips" to 5, //5, 4, 3, 3, 2
            "Timed" to false,
            "Alias" to "",
            "Time" to -1)
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
            val context = LocalContext.current
            val activity = MainActivity()
            Text(text = "Battleship", fontWeight = FontWeight.Bold, color = Color.Gray)
            Button(onClick = { context.startActivity(Intent(context,Configuration::class.java)) }) {
                Text(text = "Configuration")
            }
            Button(onClick = { context.startActivity(Intent(context,HelpActivity::class.java)) }) {
                Text(text = "Help")
            }
            Button(onClick = {activity.finish(); exitProcess(0) }) {
                Text(text = "Quit")
            }
        }
    }
}