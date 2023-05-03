package com.example.battleship

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.lang.Math.random

enum class EnemyState{
    SEARCHING,
    TARGETTING,
    SHOOTOUT,
}


class Enemy(){
    val taulell: Array<Array<CellState>> = Array(10) {
        Array(10){
            CellState.UNKNOWN
        }
    }
    val state: EnemyState = EnemyState.SEARCHING

    fun play(){
        if(state==EnemyState.SEARCHING){
            random()
        }
        else if (state==EnemyState.TARGETTING){

        }
        else{

        }
    }
}
