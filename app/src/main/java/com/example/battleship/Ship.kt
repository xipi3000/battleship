package com.example.battleship

enum class GridType(val size:Int, val resource:Int){
    CARRIER(5, R.drawable.carrier),
    BATTLESHIP(4, R.drawable.battleship),
    CRUISER(3, R.drawable.cruiser),
    SUBMARINE(3, R.drawable.submarine),
    DESTROYER(2, R.drawable.destroyer),
    WATER(0, R.drawable.water),
    SHOT(0, R.drawable.isship),
    UNDISCOVERED(0, R.drawable.undiscovered),
}
enum class Orientation{
    Horizontal,
    Vertical
}

class Ship(val type:GridType) {
    lateinit var coords:ArrayList<Int>
    var orientation:Orientation = Orientation.Horizontal
    var hasBeenSet:Boolean = false

    fun position(newCoord: ArrayList<Int>){
        coords = newCoord
        hasBeenSet=true
    }
    fun isHit(coord:Int): Boolean {
        coords.remove(coord) //Busca i elimina la primera ocurrència de "coord" (si no hi es no fa res)
        return (coord in coords)
    }

    fun isDead(): Boolean {
        return coords.isEmpty()
    }

    fun newOrientation(or: Orientation){
        orientation = or
    }

    fun rotate(): ArrayList<Int>{
        var count= 0
        val newCoord:ArrayList<Int> = arrayListOf()
        orientation = if (orientation==Orientation.Horizontal) {
            for (pos in coords){
                newCoord.add(coords[count] + 9*count)
                count++
            }
            Orientation.Vertical
        } else {
            for (pos in coords){
                newCoord.add(coords[count] - 9*count)
                count++
            }
            Orientation.Horizontal
        }
        return newCoord
    }

    fun hasBeenInitialized(): Boolean {
        return ::coords.isInitialized

    }
}