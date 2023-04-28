package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import kotlin.system.exitProcess

class ResultActivity : ComponentActivity(){
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
    fun MainView() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val context = LocalContext.current
            //Values we need to check the state of
            val correu = remember { mutableStateOf(TextFieldValue()) }
            val activity = ResultActivity()
            Text(text = "Dia y Hora")
            TextField(
                value = MainActivity.State.get("Time").toString(),
                onValueChange = {},
                enabled = false,
            )
            Text(text = "Log values")
            TextField(
                value = MainActivity.State.toString(),
                onValueChange = {},
                enabled = false,
            )
            Text(text = "E-mail")
            TextField(
                value = correu.value,
                onValueChange = {correu.value = it},
            )
            Button(onClick = {
                if(correu.value.text == "")Toast.makeText(this@ResultActivity, "Introdueixi un correu", Toast.LENGTH_SHORT).show()
                else{
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(correu.value.text))
                        //putExtra(Intent.EXTRA_SUBJECT, ??) -> valors de dia i hora
                        //putExtra(Intent.EXTRA_TEXT, MainActivity.State.toString()) -> fem un toString a State
                    }
                    context.startActivity(Intent.createChooser(intent,"Choose an Email client : "))
                }
            }) {
                Text(text = "Enviar resultados") }
            Button(onClick = { context.startActivity(Intent(context,Configuration::class.java)) }) {
                Text(text = "Nueva partida")
            }
            Button(onClick = { moveTaskToBack(true);activity.finish(); exitProcess(1) }) {
                Text(text = "Salir")
            }
        }
    }
}