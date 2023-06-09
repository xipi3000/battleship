package com.example.battleship

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.battleship.ui.theme.BattleshipTheme

class GameConfiguration : ComponentActivity() {
    companion object {
        var State = mapOf(
            "Timed" to false,
            "StartTime" to 0,
            "FinalTime" to 0,
            "MaxTime" to 0,
            "ActualTime" to 0,
            "Alias" to "Player",
            "Player1Ships" to arrayListOf<Int>(), //bot or 2nd player
            "Player2Ships" to arrayListOf<Int>(), //bot or 2nd player
            "VersusBot" to true,
            "Enemy" to Enemy(),
        )
    }

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
            val alias = rememberSaveable{ mutableStateOf("") }
            val temps = rememberSaveable { mutableStateOf("") }
            val checked = rememberSaveable { mutableStateOf(false) }
            val versus = rememberSaveable { mutableStateOf(true) }
            //Player name (needed)
            Text(text = "Alias")
            TextField(
                value = alias.value,
                onValueChange = { alias.value = it },
                placeholder = { Text(text = "Enter in-game name", color = Color.Gray) },
            )
            //Timing (available)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked.value,
                    onCheckedChange = {checked.value = it},
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(text = "Timed challenge?")
            }
                //Only show "enter time" text if we want our game to be timed
            AnimatedVisibility(visible = checked.value) {
                TextField(
                value = temps.value,
                onValueChange = { temps.value = it },
                placeholder = { Text(text = "Enter max time (seconds)", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )}
            //Game mode
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = versus.value,
                    onCheckedChange = {
                        Toast.makeText(this@GameConfiguration, "1v1 mode in development", Toast.LENGTH_SHORT).show()
                        /*versus.value = !versus.value*/},
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(text = "Against bot")
                Checkbox(
                    checked = !versus.value,
                    onCheckedChange = {
                        Toast.makeText(this@GameConfiguration, "1v1 mode in development", Toast.LENGTH_SHORT).show()
                        /*versus.value = !versus.value*/ },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(text = "Against friend")
            }
            Button(onClick = {
                //We need an alias
                if (alias.value == "") {
                    Toast.makeText(context, "Provide an alias", Toast.LENGTH_SHORT)
                        .show()
                } //If we want time, we need a time
                else if(checked.value && temps.value == ""){
                    Toast.makeText(context, "Provide a time", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    //Store config values and start ship setup
                    State = State + ("Alias" to alias.value)
                    State = State + ("Timed" to checked.value)
                    State = State + ("MaxTime" to when(checked.value){
                        false-> Int.MAX_VALUE
                        true -> temps.value.toInt()
                    })//if not specified, not used
                    State = State + ("ActualTime" to when(checked.value){
                        false-> Int.MAX_VALUE
                        true -> temps.value.toInt()
                    })
                    State = State + ("VersusBot" to versus.value)
                    val intent = Intent(context, SetUpYourShips::class.java)
                    context.startActivity(intent)
                }
            }) {
                Text(text = "Preparar tablero") }
        }
    }
}