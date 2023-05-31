package com.example.battleship.ddbb

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.battleship.ui.theme.BattleshipTheme

class GameDetail: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                MainView()
            }
        }
    }
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainView(){
        val game = getData(intent.getBundleExtra("bund")!!)
        val parsedTime:String = parseTime(game.timeSpent)
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {this.finish()}){
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            },
            topBar = {
                TopAppBar (
                    title = {
                        Text("Game details")
                    }
                )
            }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth(),
                ){
                    InfoElement("When did you play: ${game.time}")
                    InfoElement("Name: ${game.alias}")
                    InfoElement("Result: ${game.result}")
                    InfoElement("Shots: ${game.shots}")
                    InfoElement("Hit: ${game.hit}")
                    InfoElement("Miss: ${game.miss}")
                    InfoElement("Accuracy: ${game.accuracy}")
                    if(game.timedGame){
                        InfoElement(parsedTime)
                    }
                }
            }
        }
    }

    @Composable
    private fun InfoElement(info: String) {
        Card(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),
            elevation = 5.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(5.dp))
        ){
            Text(text=info, modifier = Modifier.padding(start = 5.dp, top= 3.dp, bottom = 3.dp))
        }
    }

    private fun parseTime(timeSpent: Int): String {
        val secs = timeSpent%60
        val mins = (timeSpent-secs)/60
        return "You spent $mins:$secs"
    }

    private fun getData(bund: Bundle): GameInfo {
        val name = bund.getString("name")
        val res = bund.getString("result")
        val shots = bund.getInt("fired")
        val hit = bund.getInt("hit")
        val miss = bund.getInt("miss")
        val acc = bund.getFloat("accuracy")
        val time = bund.getString("time")
        val timedGame = bund.getBoolean("timedGame")
        val timeSpent = bund.getInt("timeSpent")
        if (name!=null && res!=null)return GameInfo(0, name, res, shots, hit, miss, acc, time!!, timedGame, timeSpent)
        return GameInfo(-1, "Error", "Error", 0,0,0,0.0f, "Error", timedGame, 0)

    }
}