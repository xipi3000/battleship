package com.example.battleship

enum class CellState {
    UNKNOWN,
    WATER,
    SHIPFOUND,
    SHIPHIDDEN,
    OUTOFBOUNDS,

    //shipDiscovered -> explosió
    //shipUndiscovered -> sprite gris pero diferent?
}
