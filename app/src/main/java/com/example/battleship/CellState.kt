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
public class CellStateInter(cellState: CellState,resource :Int =-1,pos:Orientation = Orientation.Vertical){

}
