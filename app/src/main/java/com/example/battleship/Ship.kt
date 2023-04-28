package com.example.battleship


enum class ShipType(val size:Int){
    CARRIER(5),
    BATTLESHIP(4),
    CRUISER(3),
    DESTROYER(3),
    SUBMARINE(2),
}

class Ship(val type:ShipType ,val coords: ArrayList<Int>) {
    //coords: array with the positions it occupies (where it hasn't been hit)
    //type: enum stated before
    //Since every square is a different number...

    fun isHit(coord:Int): Boolean {
        coords.remove(coord) //Busca i elimina la primera ocurrència de "coord" (si no hi es no fa res)
        return (coord in coords)
    }

    fun isDead(): Boolean {
        return coords.isEmpty()
    }
}