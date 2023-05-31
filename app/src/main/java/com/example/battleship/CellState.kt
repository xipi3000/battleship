package com.example.battleship

enum class CellState() {
    UNKNOWN,
    WATER,
    SHIPFOUND,
    SHIPHIDDEN,
    OUTOFBOUNDS,
    BATTLESHIP,
    FALLO,

    //shipDiscovered -> explosió
    //shipUndiscovered -> sprite gris pero diferent?
}
class CellStateInter(var cellState: CellState,var resource :Int =-1,var orientation: Orientation = Orientation.Vertical){

}
