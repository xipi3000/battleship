package com.example.battleship

enum class CellState(var resource: Int) {
    UNKNOWN(-1),
    WATER(-1),
    SHIPFOUND(-1),
    SHIPHIDDEN(-1),
    OUTOFBOUNDS(-1),
    BATTLESHIP(-1),

    //shipDiscovered -> explosió
    //shipUndiscovered -> sprite gris pero diferent?
}
class CellStateInter(var cellState: CellState,var resource :Int =-1,var orientation: Orientation = Orientation.Vertical){

}
