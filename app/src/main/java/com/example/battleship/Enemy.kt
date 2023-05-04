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
    var orientations : MutableList<Int> =
        MutableList(4){
                it
        }
    val cellsList : MutableList<Int> =
        MutableList(100){
                it
        }

    fun getRandom():Pair<Int,Int>{
        println(cellsList)
        val cell : Int = cellsList.random()
        return Pair(cell/10,cell%10)
    }

    fun isOutOfBounds(coord: Pair<Int,Int>) : Boolean{
        if(coord.first > 9 || coord.first<0 || coord.second>9 || coord.second<0){
            return true
        }
        return false
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
                if(isOutOfBounds(nextCell)){
                    isCheckingButt=true
                    return Pair(centerCell.first + orientation.first, centerCell.second + orientation.second)
                }
            }
            else{
                nextCell =
                    Pair(lastCell.first + orientation.first, lastCell.second + orientation.second)
                if(isOutOfBounds(nextCell)){
                    state = EnemyState.SEARCHING
                    return getRandom()
                }
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
                if(isOutOfBounds(nextCell)){
                    state = EnemyState.SEARCHING
                    return getRandom()
                }

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

    fun getCellState(cell: Pair<Int,Int>) : CellState{
        return taulell[cell.first][cell.second]
    }

    fun nextCell(): Pair<Int, Int> {
        var outOfBounds = true
        var newCoord = Pair(0,0)
        while(outOfBounds) {
            var orientation = orientations.random()

            if (orientations.size == 0) {
                state = EnemyState.SEARCHING
                return getRandom()
            }
            checkedCells += orientation

            println(orientations)


            if (orientation == 0) {
                orientations.remove(0)
                newCoord = Pair(centerCell.first - 1, centerCell.second)
                if (!isOutOfBounds(newCoord)) {
                    outOfBounds = false
                }
                newCoord = Pair(centerCell.first - 1, centerCell.second)
            } //Amunt
            else if (orientation == 1) {
                orientations.remove(1)
                newCoord = Pair(centerCell.first, centerCell.second + 1)
                if (!isOutOfBounds(newCoord)) {
                    outOfBounds = false
                    newCoord = Pair(0, 0)
                }

            } //Dreta
            else if (orientation == 2) {
                orientations.remove(2)
                newCoord = Pair(centerCell.first + 1, centerCell.second)
                if (!isOutOfBounds(newCoord)) {
                    outOfBounds = false
                }
            } //Devall
            else if (orientation == 3) {
                orientations.remove(3)
                newCoord = Pair(centerCell.first, centerCell.second - 1) //Esquerra
                if (!isOutOfBounds(newCoord)) {
                    outOfBounds = false
                }
            }
        }
        return newCoord


    }

    fun checkCell(coords: Pair<Int, Int>, result: CellState) {
        lastResult = result
        lastCell = coords
        cellsList.remove(coords.first*10+coords.second)
        taulell[coords.first][coords.second]=result
        println(centerCell)
        println(lastCell)
        if (state == EnemyState.SEARCHING && result == CellState.SHIP) {
            isCheckingButt = false
            centerCell = coords
            state = EnemyState.TARGETTING
            orientations.clear()
            orientations= MutableList(4){
                it
            }

        } else if (state == EnemyState.TARGETTING && result == CellState.SHIP) {
            orientation = Pair(
                centerCell.first - lastCell.first,
                centerCell.second - lastCell.second
            )
            orientations.clear()
            orientations= MutableList(4){
                    it
            }

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
