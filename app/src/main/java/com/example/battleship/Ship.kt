package com.example.battleship

enum class CellType(val size:Int, val resource:Int,val ress: List<Int>){
    CARRIER(5, R.drawable.carrier, listOf(R.drawable.carrier_0,R.drawable.carrier_1,R.drawable.carrier_2,R.drawable.carrier_3,R.drawable.carrier_4)),
    BATTLESHIP(4, R.drawable.battleship,listOf(R.drawable.battleship_0,R.drawable.battleship_1,R.drawable.battleship_2,R.drawable.battleship_3)),
    CRUISER(3, R.drawable.cruiser,listOf(R.drawable.cruiser_0,R.drawable.cruiser_1,R.drawable.cruiser_2)),
    SUBMARINE(3, R.drawable.submarine,listOf(R.drawable.submarine_0,R.drawable.submarine_1,R.drawable.submarine_2)),
    DESTROYER(2, R.drawable.destroyer,listOf(R.drawable.destroyer_0,R.drawable.destroyer_1)),
    WATER(0, R.drawable.water,listOf()),
    SHOT(0, R.drawable.isship,listOf()),
    UNDISCOVERED(0, R.drawable.undiscovered,listOf()),;
}


class GridType(var type: CellType,var pos: Int = -1,var orientation: Orientation = Orientation.Horizontal){

}

enum class Orientation(val degrees : Float){
    Horizontal(0.0f),
    Vertical(90.0f)
}

class Ship(val type:GridType) {
    lateinit var coords:ArrayList<Int>
    var orientation:Orientation = Orientation.Horizontal
    var hasBeenSet:Boolean = false

    fun position(newCoord: ArrayList<Int>){
        coords = newCoord
        hasBeenSet=true
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