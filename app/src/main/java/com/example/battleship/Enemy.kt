package com.example.battleship

import android.util.Log

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
            var i = 0
            var nextCell = nextCell(i)

            while (getCellState(nextCell) != CellState.UNKNOWN && i<4 || isOutOfBounds(nextCell ) && i<4  ) {
                i++
                nextCell = nextCell(i)

            }
            if( isOutOfBounds(nextCell) && i ==3){
                state=EnemyState.SEARCHING
                return getRandom()
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
        if(!isOutOfBounds(cell)) {
            return taulell[cell.first][cell.second]
        }
        return CellState.OUTOFBOUNDS
    }

    fun nextCell(orientation: Int): Pair<Int, Int> {
        var newCoord = Pair(0,0)
        println(orientations)
        if (orientation == 0) {
            return Pair(centerCell.first - 1, centerCell.second)
        } //Amunt
        else if (orientation == 1) {
            return Pair(centerCell.first, centerCell.second + 1)

        } //Dreta
        else if (orientation == 2) {

            return  Pair(centerCell.first + 1, centerCell.second)
        } //Davall
        else if (orientation == 3) {
            return  Pair(centerCell.first, centerCell.second - 1) //Esquerra
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
        if (state == EnemyState.SEARCHING && result == CellState.SHIPFOUND) {
            isCheckingButt = false
            centerCell = coords
            state = EnemyState.TARGETTING
            //orientations.clear()
            //orientations= MutableList(4){
            //    it
            //}
        } else if (state == EnemyState.TARGETTING && result == CellState.SHIPFOUND) {
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
