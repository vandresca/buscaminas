package com.minesweeper.minesweeper.domain

//Entidad Celda.
//Representa a una casilla del tablero de juego
data class Cell (
    val numberCell: Int,
    var state: CellType)
