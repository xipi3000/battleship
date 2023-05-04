package com.example.battleship

import android.util.Log
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.math.log
import kotlin.random.Random

enum class EnemyState {
    SEARCHING,
    TARGETTING,
    SHOOTOUT,
}


class Enemy() {
    val taulell: Array<Array<CellState>> = Array(10) {
        Array(10) {
            CellState.UNKNOWN
        }
    }


    var state: EnemyState = EnemyState.SEARCHING
    var lastResult: CellState = CellState.UNKNOWN
    var lastCell: Pair<Int, Int> = Pair(0, 0)
    var centerCell: Pair<Int, Int> = Pair(0, 0)
    var isCheckingButt: Boolean = false
    lateinit var checkedCells: Set<Int>
    lateinit var orientation : Pair<Int,Int>
    val cellsList : MutableList<Int> =
        MutableList(100){
                i -> i + 1
        }

    fun getRandom():Pair<Int,Int>{
        val cell : Int = cellsList.random()
        cellsList.remove(cell)

        println(cellsList)
        return Pair(cell/10,cell%10)

    }

    fun play(): Pair<Int, Int> {

        if (state == EnemyState.TARGETTING) { //Estem fent la creu
            checkedCells= setOf()
            var nextCell = nextCell()
            while (getCellState(nextCell) != CellState.UNKNOWN) {
                nextCell = nextCell()
            }
            return nextCell


        } else if (state == EnemyState.SHOOTOUT) {

            var nextCell : Pair<Int,Int>
            if(!isCheckingButt) {
                nextCell =
                    Pair(lastCell.first - orientation.first, lastCell.second - orientation.second)
            }
            else{
                nextCell =
                    Pair(lastCell.first + orientation.first, lastCell.second + orientation.second)
            }

            if (getCellState(nextCell) != CellState.UNKNOWN) {
                //S'acaba el proces o mirem el darrere
                if (isCheckingButt) {
                    state = EnemyState.SEARCHING
                    return getRandom()
                }
                isCheckingButt = true
                nextCell =
                    Pair(centerCell.first + orientation.first, centerCell.second + orientation.second)

                if (getCellState(nextCell) != CellState.UNKNOWN) {
                    state = EnemyState.SEARCHING
                    return getRandom()
                }

                return nextCell

            }
            return nextCell
        }
        return getRandom()
    }
    fun randomCell(): Pair<Int, Int> {
        var i = Random.nextInt(0, 9)
        var j = Random.nextInt(0, 9)
        while (taulell[i][j] != CellState.UNKNOWN) {
            i = Random.nextInt(0, 9)
            j = Random.nextInt(0, 9)
        }
        return Pair(i, j)
    }
    fun getCellState(cell: Pair<Int,Int>) : CellState{
        return taulell[cell.first][cell.second]
    }

    fun nextCell(): Pair<Int, Int> {
        var orientation = Random.nextInt(0, 4)
        while(orientation in checkedCells && checkedCells.size!=4){
            orientation = Random.nextInt(0, 4)
            println("Orientation->"+orientation)
        }
        println("nextt one->"+orientation)
        if(checkedCells.size==4){
            state=EnemyState.SEARCHING
            return randomCell()
        }
        checkedCells += orientation

        if (orientation == 0) return Pair(centerCell.first - 1, centerCell.second) //Amunt
        else if (orientation == 1) return Pair(centerCell.first, centerCell.second + 1) //Dreta
        else if (orientation == 2) return Pair(centerCell.first + 1, centerCell.second) //Devall
        return Pair(centerCell.first, centerCell.second - 1) //Esquerra

    }

    fun checkCell(coords: Pair<Int, Int>, result: CellState) {
        lastResult = result
        lastCell = coords
        taulell[coords.first][coords.second] = result
        if (state == EnemyState.SEARCHING && result == CellState.SHIP) {
            isCheckingButt = false
            centerCell = coords
            state = EnemyState.TARGETTING
        } else if (state == EnemyState.TARGETTING && result == CellState.SHIP) {
            orientation = Pair(
                centerCell.first - lastCell.first,
                centerCell.second - lastCell.second
            )

            state = EnemyState.SHOOTOUT
        } else if (state == EnemyState.SHOOTOUT && result == CellState.WATER) {
            if (!isCheckingButt) {
                Log.i("goofy","mirem cul")
                Log.i("goofy",state.toString())
                lastCell=centerCell
                isCheckingButt = true

            }
            else{
                state=EnemyState.SEARCHING
            }

        }
    }
}
