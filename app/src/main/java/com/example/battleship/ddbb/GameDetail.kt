package com.example.battleship.ddbb

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
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
        val parsedTime = parseTime(game.timeSpent)
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {this.finish()}){
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            }) {
            Column(){
                Text(text="Name: ${game.alias}")
                Text(text="Result: ${game.result}")
                Text(text="Shots: ${game.shots}")
                Text(text="Hit: ${game.hit}")
                Text(text="Miss: ${game.miss}")
                Text(text="Accuracy: ${game.accuracy}")
                Text(text="Time: ${game.time}")
                Text(text="TimeSpent: $parsedTime")
            }
        }
    }

    private fun parseTime(timeSpent: Int): Any {
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
        val timeSpent = bund.getInt("timeSpent")
        if (name!=null && res!=null)return GameInfo(0, name, res, shots, hit, miss, acc, time!!, timeSpent)
        return GameInfo(-1, "Error", "Error", 0,0,0,0.0f, "Error", 0)

    }
}