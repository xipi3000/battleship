package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.battleship.ddbb.GameInfo
import com.example.battleship.ddbb.GameInfoApplication
import com.example.battleship.ddbb.GameInfoListAdapter
import com.example.battleship.ddbb.GameInfoViewModel
import com.example.battleship.ddbb.GameInfoViewModelFactory
import com.example.battleship.ui.theme.BattleshipTheme
import kotlin.system.exitProcess

class ResultActivity : ComponentActivity(){
    private val gameViewModel: GameInfoViewModel by viewModels {
        GameInfoViewModelFactory((application as GameInfoApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameConfiguration.State = GameConfiguration.State + ("Enemy" to Enemy())

        val adapter = GameInfoListAdapter()
        gameViewModel.allGames.observe(this) { games ->
            // Update the cached copy of the words in the adapter.
            games.let { adapter.submitList(it) }
        }

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
            Button(onClick = {
                val intent = Intent(context,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            }) {
                Text(text = "Menú principal")
            }
            Button(onClick = { moveTaskToBack(true);activity.finish(); exitProcess(1) }) {
                Text(text = "Salir")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseGameResult(): String {
        val player1ships = GameConfiguration.State["Player1Ships"] as ArrayList<Int>
        val player2ships = GameConfiguration.State["Player2Ships"] as ArrayList<Int>
        val alias = GameConfiguration.State["Alias"].toString()
        val player2grid = SetUpYourShips.Grids["player2Grid"] as ArrayList<CellState>
        var fired = 0; var hit = 0; var miss = 0; var accuracy = 0.0f

        for (i in 0 until 100){
            if (player2grid[i] != CellState.UNKNOWN){
                if (player2grid[i] == CellState.WATER) miss +=1
                else hit +=1
                fired+=1
            }
        }
        if (fired !=0)accuracy = (((hit.toFloat()/fired.toFloat())*100))

        /** He ficat el 0 assumint que se autogenerarà i se sobreescriurà. Potser no funciona així **/
        return if (player2ships.isEmpty()){
            val game= GameInfo(0, alias, "Winner", fired, hit, miss, accuracy)
            Log.i("GameInfo", "Alias: "+game.alias+System.lineSeparator()+
                    "Result: "+game.result+System.lineSeparator()+
                    "Fired: "+game.shots.toString()+System.lineSeparator()+
                    "Hit: "+game.hit.toString()+System.lineSeparator()+
                    "Miss: "+game.miss.toString()+System.lineSeparator()+
                    game.hit.toString()+"/"+game.shots.toString()+"="+game.accuracy.toString())
            gameViewModel.insert(game)
            Log.i("GameInfo","There are currently ${gameViewModel.allGames.value?.size} elements on ddbb")

            "Enhorabona! Has guanyat la partida :D"
        }else if(player1ships.isEmpty()){
            val game= GameInfo(0, alias, "Loser", fired, hit, miss, accuracy)
            Log.i("GameInfo", "Alias: "+game.alias+System.lineSeparator()+
                    "Result: "+game.result+System.lineSeparator()+
                    "Fired: "+game.shots.toString()+System.lineSeparator()+
                    "Hit: "+game.hit.toString()+System.lineSeparator()+
                    "Miss: "+game.miss.toString()+System.lineSeparator()+
                    game.hit.toString()+"/"+game.shots.toString()+"="+game.accuracy.toString())
            gameViewModel.insert(game)
            Log.i("GameInfo","There are currently ${gameViewModel.allGames.value?.size} elements on ddbb")
            "Una llàstima, sembla que has perdut :("
        }else{
            val game= GameInfo(0, alias, "Draw", fired, hit, miss, accuracy)
            Log.i("GameInfo", "ID: "+game.id+System.lineSeparator()+
                    "Alias: "+game.alias+System.lineSeparator()+
                    "Result: "+game.result+System.lineSeparator()+
                    "Fired: "+game.shots.toString()+System.lineSeparator()+
                    "Hit: "+game.hit.toString()+System.lineSeparator()+
                    "Miss: "+game.miss.toString()+System.lineSeparator()+
                    game.hit.toString()+"/"+game.shots.toString()+"="+game.accuracy.toString())
            gameViewModel.insert(game)
            Log.i("GameInfo","There are currently ${gameViewModel.allGames.value?.size} elements on ddbb")

            "Ep! Sembla que necessitaves més temps..."
        }
    }
}