package com.example.battleship

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    var hasCheckedButt: Boolean = false

    fun randomCell(): Pair<Int, Int> {
        var i = Random.nextInt(0, 10)
        var j = Random.nextInt(0, 10)
        while (taulell[i][j] != CellState.UNKNOWN) {
            i = Random.nextInt(0, 10)
            j = Random.nextInt(0, 10)
        }
        return Pair(i, j)
    }

    fun play(): Pair<Int, Int> {

        if (state == EnemyState.TARGETTING) { //Estem fent la creu
            var nextCell = nextCell()
            while (getCellState(nextCell) != CellState.UNKNOWN) {
                nextCell = nextCell()
            }
            return nextCell


        } else if (state == EnemyState.SHOOTOUT) {
            val orientation = Pair(
                centerCell.first - lastCell.first,
                centerCell.second - lastCell.second
            )
            var nextCell =
                Pair(lastCell.first - orientation.first, lastCell.second - orientation.second)
            if (getCellState(nextCell) != CellState.UNKNOWN) {
                //S'acaba el proces o mirem el darrere
                if (hasCheckedButt) return randomCell()
                hasCheckedButt = true
                nextCell =
                    Pair(lastCell.first + orientation.first, lastCell.second + orientation.second)
                if (getCellState(nextCell) != CellState.UNKNOWN) {
                     return randomCell()
                    while (getCellState(nextCell) == CellState.SHIP) {
                        nextCell = Pair(
                            nextCell.first + orientation.first,
                            nextCell.second + nextCell.second
                        )
                    }
                    return nextCell
                }
                return nextCell
            }
            return nextCell
        }
        return randomCell()
    }
    fun getCellState(cell: Pair<Int,Int>) : CellState{
        return taulell[cell.first][cell.second]
    }

    fun nextCell(): Pair<Int, Int> {
        val orientation = Random.nextInt(0, 3)
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
            centerCell == coords
            state = EnemyState.TARGETTING
        } else if (state == EnemyState.TARGETTING && result == CellState.SHIP) {
            state = EnemyState.SHOOTOUT

        } else {
            state = EnemyState.SEARCHING
        }

    }
}
