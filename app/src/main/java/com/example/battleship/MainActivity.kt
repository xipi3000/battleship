package com.example.battleship

import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleship.ddbb.GameHistory
import com.example.battleship.services.ThemeSongService
import com.example.battleship.ui.theme.BattleshipTheme
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentMusicService = Intent(this,ThemeSongService::class.java)
        startService(intentMusicService)
        setContent {
            BattleshipTheme {
                // A surface container using the 'background' color from the theme
                MainView()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("resume","started")
        val intentMusicService = Intent(this,ThemeSongService::class.java)
        startService(intentMusicService)
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
            //Text(text = "Battleship", fontWeight = FontWeight.Bold, color = Color.Gray)
            Image(painter = painterResource(id = R.drawable.battleship_text_logo),
                contentDescription = "Battleship",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {

                val intentMusicService = Intent(context,ThemeSongService::class.java)
                context.startService(intentMusicService.putExtra("transition",true))
                context.startActivity(Intent(context,GameConfiguration::class.java))
            }) {
                Text(text = "Configuration")
            }
            Button(onClick = { context.startActivity(Intent(context,HelpActivity::class.java)) }) {
                Text(text = "Help")
            }
            Button(onClick = { context.startActivity(Intent(context, GameHistory::class.java)) }) {
                Text(text = "Game History")
            }
            Button(onClick = {MainActivity().finish(); exitProcess(0) }) {
                Text(text = "Quit")
            }
        }
    }
}