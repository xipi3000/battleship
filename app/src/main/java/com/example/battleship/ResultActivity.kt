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
            val logMessage = "Player: "+GameConfiguration.State["Alias"]+"."+System.getProperty("line.separator")+
                    "La partida ha durat: "+((GameConfiguration.State["MaxTime"] as Int)-(GameConfiguration.State["FinalTime"] as Int))+
                    " segons."+System.getProperty("line.separator")+ parseGameResult()
            Text(text = "Dia y Hora")
            TextField(
                value = GameConfiguration.State["StartTime"].toString(),
                onValueChange = {},
                enabled = false,
            )
            //S'haurà de fer un parser per ficar un missatge, que encara amb els arrays li costa
            Text(text = "Log values")
            TextField(
                value = logMessage,
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
                        putExtra(Intent.EXTRA_SUBJECT, GameConfiguration.State["StartTime"].toString())
                        putExtra(Intent.EXTRA_TEXT, logMessage)
                    }
                    context.startActivity(Intent.createChooser(intent,"Choose an Email client : "))
                }
            }) {
                Text(text = "Enviar resultados") }
            Button(onClick = {
                val intent = Intent(context,GameConfiguration::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }) {
                Text(text = "Nueva partida")
            }
            Button(onClick = { moveTaskToBack(true);activity.finish(); exitProcess(1) }) {
                Text(text = "Salir")
            }
        }
    }

    private fun parseGameResult(): String {
        val player1ships = GameConfiguration.State["Player1Ships"] as ArrayList<Int>
        val player2ships = GameConfiguration.State["Player2Ships"] as ArrayList<Int>
        /* CANVIAR MISSATGES DE RESULT */
        return if (player2ships.isEmpty()){
            "Tremendo, has aconseguit guanyar a un bot. Vols un pin o una xapa?"
        }else if(player1ships.isEmpty()){
            "Malooo, que malo lol, git gud bitch."
        }else{
            "Puto lento, a veure si penses més ràpid."
        }
    }
}