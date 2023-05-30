package com.example.battleship.ddbb

import android.annotation.SuppressLint
import android.content.Intent
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
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val intent = Intent (this, GameHistory::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    this.startActivity(intent)
                }){
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
            }
        }
    }

    private fun getData(bund: Bundle): GameInfo {
        val name = bund.getString("name")
        val res = bund.getString("result")
        val shots = bund.getInt("fired")
        val hit = bund.getInt("hit")
        val miss = bund.getInt("miss")
        val acc = bund.getFloat("accuracy")
        if (name!=null && res!=null)return GameInfo(0, name, res, shots, hit, miss, acc)
        return GameInfo(-1, "Error", "Error", 0,0,0,0.0f)

    }
}