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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.battleship.ui.theme.BattleshipTheme

class Configuration : ComponentActivity() {

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
            Text(text = "Alias")
            //Values we need to check the state of
            val alias = remember { mutableStateOf(TextFieldValue()) }
            val temps = remember { mutableStateOf(TextFieldValue()) }
            val checked = remember { mutableStateOf(false) }
            TextField(
                value = alias.value,
                onValueChange = { alias.value = it },
                placeholder = { Text(text = "Enter in-game name", color = Color.Gray) },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked.value,
                    onCheckedChange = { checked.value = it},
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
            Button(onClick = {
                val intent = Intent(context, SetUpYourShips::class.java)
                //We need an alias
                if (alias.value.text == "") {
                    Toast.makeText(context, "Introdueixi un alias correcte", Toast.LENGTH_SHORT)
                        .show()
                } //If we want time, we need a time
                else if(checked.value && temps.value.text == ""){
                    Toast.makeText(context, "Introdueixi un temps", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    //Store config values and start ship setup
                    MainActivity.State = MainActivity.State + ("Alias" to alias.value.text)
                    MainActivity.State = MainActivity.State + ("Timed" to checked.value)
                    if (checked.value) MainActivity.State = MainActivity.State + ("InitialTime" to temps.value.text)
                    else MainActivity.State = MainActivity.State + ("Time" to -1)
                    context.startActivity(intent)
                }
            }) {
                Text(text = "Preparar tablero") }
        }
    }
}