package com.example.battleship

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleship.ui.theme.BattleshipTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BattleshipTheme {
                MainView()
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Preview(showBackground = true)
    @Composable
    fun MainView() {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "How to play:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                        },
                    )

                }
            )

            {


                Column(
                    Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {

                    Card(
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(5.dp)) {
                            Text(
                                text = "Setting the gamemode:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = stringResource(R.string.sett_game),
                                modifier = Modifier.padding(
                                    top = 5.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom= 10.dp,
                                ),
                                textAlign = TextAlign.Justify,
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(5.dp)) {
                            Text(
                                text = "Setting the gamemode:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = stringResource(R.string.sett_ships),
                                modifier = Modifier.padding(
                                    top = 5.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom= 10.dp,
                                ),
                                textAlign = TextAlign.Justify,
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(5.dp)) {
                            Text(
                                text = "Setting the gamemode:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = stringResource(R.string.playing),
                                modifier = Modifier.padding(
                                    top = 5.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom= 10.dp,
                                ),
                                textAlign = TextAlign.Justify,

                                )
                        }
                    }


                    Button(onClick = { finish() }, modifier = Modifier.padding(10.dp)) {
                        Text(text = "Go back")
                    }
                }
            }

        }
    }
}



