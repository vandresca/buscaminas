package com.minesweeper.minesweeper.domain

//Enum para los tipos de celdas del tablero de juego
//BOMB -> Bomba
//VOID -> Vacío
//ONE -> Una bomba
//TWO -> Dos bombas
//THREE -> Tres bombas
enum class CellType(val value:Int) {
    BOMB(-1),
    VOID(0),
    ONE(1),
    TWO(2),
    THREE(3);
    companion object {
        //Obtener el tipo de CellType a partir del valor númerico
        fun getCellType(value: Int): CellType {
            for (stateType in values()) {
                if (stateType.value == value) {
                    return stateType
                }
            }
            return VOID
        }
    }
}