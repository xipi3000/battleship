package com.example.battleship

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.random.Random

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

    fun play() : Pair<Int,Int>{
        if(state==EnemyState.SEARCHING){
            var i = Random.nextInt(0,10)
            var j = Random.nextInt(0,10)
            while(taulell[i][j]!=CellState.UNKNOWN){
                i = Random.nextInt(0,10)
                j = Random.nextInt(0,10)
            }
            return Pair(i,j)
        }
        else if (state==EnemyState.TARGETTING){


        }
        else{

        }
    }
    fun checkCell(coords){

    }
}
