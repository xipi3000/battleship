package com.example.battleship

enum class ShipType(val size:Int){
    CARRIER(5),
    BATTLESHIP(4),
    CRUISER(3),
    DESTROYER(3),
    SUBMARINE(2),
    WATER(0),
}
enum class Orientation(){
    Horizontal,
    Vertical
}

class Ship(val type:ShipType) {
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
}