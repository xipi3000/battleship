package com.example.battleship

enum class CellState {
    UNKNOWN,
    WATER,
    SHIP,
    OUTOFBOUNDS,
    //shipDiscovered -> explosió
    //shipUndiscovered -> sprite gris pero diferent?
}
