package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.battleship.ddbb.GameInfo
import com.example.battleship.ddbb.GameInfoApplication
import com.example.battleship.ddbb.GameInfoViewModel
import com.example.battleship.ddbb.GameInfoViewModelFactory
import com.example.battleship.ui.theme.BattleshipTheme
import kotlin.system.exitProcess

class ResultActivity : ComponentActivity() {
    private val totalTime =
        (GameConfiguration.State["MaxTime"] as Int) - (GameConfiguration.State["FinalTime"] as Int)
    private val gameViewModel: GameInfoViewModel by viewModels {
        GameInfoViewModelFactory((application as GameInfoApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameConfiguration.State = GameConfiguration.State + ("Enemy" to Enemy())
        SetUpYourShips.Grids = SetUpYourShips.Grids + ("player1Grid" to ArrayList<CellStateInter>())
        SetUpYourShips.Grids = SetUpYourShips.Grids + ("player2Grid" to ArrayList<CellStateInter>())
        SetUpYourShips.Grids = SetUpYourShips.Grids + ("ships" to ArrayList<Ship>())
        gameViewModel.allGames.observe(this) {}

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
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val context = LocalContext.current
            val correu = rememberSaveable { mutableStateOf("") }
            val activity = ResultActivity()
            val logMessage =
                "Player: " + GameConfiguration.State["Alias"] + "." + System.getProperty("line.separator") +
                        "La partida ha durat: " + totalTime.toString() + " segons." + System.getProperty(
                    "line.separator"
                ) +
                        parseGame()
            Text(text = "Dia y Hora")
            TextField(
                value = GameConfiguration.State["StartTime"].toString(),
                onValueChange = {},
                enabled = false,
            )
            Text(text = "Log values")
            TextField(
                value = logMessage,
                onValueChange = {},
                enabled = false,
            )
            Text(text = "E-mail")
            TextField(
                value = correu.value,
                onValueChange = { correu.value = it },
            )
            Button(onClick = {
                if (correu.value == "") Toast.makeText(
                    this@ResultActivity,
                    "Introdueixi un correu",
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(correu.value))
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            GameConfiguration.State["StartTime"].toString()
                        )
                        putExtra(Intent.EXTRA_TEXT, logMessage)
                    }
                    context.startActivity(Intent.createChooser(intent, "Choose an Email client : "))
                }
            }) {
                Text(text = "Enviar resultados")
            }
            Button(onClick = {
                val intent = Intent(context, GameConfiguration::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }) {
                Text(text = "Nueva partida")
            }
            Button(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }) {
                Text(text = "Menú principal")
            }
            Button(onClick = {
                finishAffinity()
                //moveTaskToBack(true)
                //activity.finish()
                //exitProcess(1)

            }) {
                Text(text = "Salir")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseGameResult(): String {
        val player1ships = GameConfiguration.State["Player1Ships"] as ArrayList<Int>
        val player2ships = GameConfiguration.State["Player2Ships"] as ArrayList<Int>

        return if (player2ships.isEmpty()){
            "Enhorabona! Has guanyat la partida :D//Won"
        }else if(player1ships.isEmpty()){
            "Una llàstima, sembla que has perdut :(//Lost"
        }else{
            "Ep! Sembla que necessitaves més temps...//Draw"
        }
    }

    fun parseGame() : String{
        //variables needed
        val player2grid = SetUpYourShips.Grids["player2Grid"] as ArrayList<CellState>
        val alias = GameConfiguration.State["Alias"].toString()
        var fired = 0; var hit = 0; var miss = 0; var unShot = 0; var accuracy = 0.0f

        //calculate results
        for (i in 0 until 100){
            if (player2grid[i] != CellState.UNKNOWN){
                if (player2grid[i] == CellState.WATER) miss +=1
                else hit+=1
                fired+=1
            }else unShot+=1
        }
        if (fired !=0)accuracy = (((hit.toFloat()/fired.toFloat())*100))
        val message = parseGameResult()

        val parsedMessage = message.split("//")

        //insert into database
        val game= GameInfo(0, alias, parsedMessage[1], fired, hit, miss, accuracy,
            time = GameConfiguration.State["StartTime"].toString(),
            timeSpent = totalTime,
            timedGame = GameConfiguration.State["Timed"] as Boolean)
        gameViewModel.insert(game)

        //previous functionality
        return parsedMessage[0]
    }
}