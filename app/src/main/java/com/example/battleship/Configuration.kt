package com.example.battleship

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.battleship.ui.theme.BattleshipTheme

class Configuration : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                MainView()
            }
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun MainView(){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val context= LocalContext.current
            Text(text="Alias")
            val inputvalue = remember { mutableStateOf(TextFieldValue()) }
            TextField(
                value = inputvalue.value,
                onValueChange = {inputvalue.value=it},
                placeholder = {Text(text="Enter in-game name")}
            )
            Button(onClick = {
                val intent = Intent(context,SetUpYourShips::class.java)
                intent.putExtra("Alias", inputvalue.value.text)
                context.startActivity(intent) }) {
                Text(text = "Preparar tablero")
            }
        }
    }
}