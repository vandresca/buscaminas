package com.minesweeper.minesweeper  .ui


import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.minesweeper.minesweeper.domain.Cell
import com.minesweeper.minesweeper.domain.CellType

//Clase ViewModel
class MinesWeeperViewModel: ViewModel() {

    companion object{
        var win:Boolean = false
    }

    //Declaración variables globales
    //Matrix -> Matriz del tablero
    //VisibleCells -> Matriz de visibilidad de las casillas del tablero
    //isEnded -> Indica si ha finalizado el juego
    //Size -> Indica el tamaño del tablero de juego
    //NumberBombs -> Indica el número de bombas que existen en el tablero
    var matrix by mutableStateOf(emptyList<Cell>())
    var visibleCells by mutableStateOf(emptyList<Boolean>())
    var isEnded: Boolean = false
    val size:Int = 10
    var numberBombs = 5

    //Enum que indica diferentes decisiones. Se utiliza para el recorrido de
    //casillas continuas una vez que pulsamos en una casilla.
    enum class Decision(val value:Int){
        CONTINUE(0),
        STOP(1),
        NOTHING(2)
    }

    //hasWin -> Indica si se ha ganado la partida
    fun hasWin(): Boolean {
        val predicate: (Boolean) -> Boolean = { !it }

        if (!win){
            win = (numberBombs == visibleCells.count(predicate))
        }else{
            isEnded = true
            showAllBombsAndFinish()
        }
        return win
    }

    //Inicializa la matriz del tablero y la de visibilidad de casillas del tablero
    fun initTable(){
        matrix = List(size*size)
        { index -> Cell(index, CellType.VOID)}
        visibleCells = List(size*size){false}
    }

    //Dado un numero de celda del tablero devuelve su estado
    fun getStateOfCell(numberCell:Int): CellType{
        return matrix[numberCell].state
    }

    //Dada la fila y la columna de una celda devuelve su estado
    private fun getStateOfCell(row:Int, col:Int):CellType{
        val numberCell = row*size + col
        return getStateOfCell(numberCell)
    }

    //Dada una fila y una columna de una celda le asigna un estado
    private fun setStateOfCell(row:Int, col:Int, state: CellType){
        val numberCell = row*size + col
        setStateOfCell(numberCell, state)
    }

    //Dado un número de celda del tablero le asigna un estado
    private fun setStateOfCell(numberCell:Int, state: CellType){
        matrix[numberCell].state = state
    }

    //Obtiene el número de celda y devuelve si la casilla es visible o no
    fun isVisibleCell(numberCell:Int):Boolean{
        return visibleCells[numberCell]
    }

    //Obtiene el número de fila y columna y devuelve si la casilla es 
    //visible o no
    private fun isVisibleCell(row:Int, col:Int):Boolean{
        val numberCell = row*size + col
        return isVisibleCell(numberCell)
    }

    //Inicializa la posicion de las bombas en el tablero de juego
    fun initBombs(){
        var randRow: Int
        var randCol: Int

        //Recorremos todas las bombas
        for(i in 1..numberBombs){

            //Por cada una generamos una fila y columna aleatoria
            //Si esa casilla del tablero esta vacía le asignamos la bomba
            do{
                randRow = (1..size).random()-1
                randCol = (1..size).random()-1
            }while(getStateOfCell(randRow, randCol) != CellType.VOID)
            setStateOfCell(randRow, randCol,CellType.BOMB)
        }
    }

    //Inicializa las casillas contiguas a la bomba, que indican el número
    //de bombas que existen alrededor de está.
    fun initSurroundingBombs(){
        for(r in 1..size){
            for(c in 1..size){
                val numberCell = (r-1)*size+(c-1)
                setStateOfCell(numberCell, nearBombsOfCell(row=r-1, col=c-1))
            }
        }
    }

    //Indica si el número de fila y/o columna sale de los limites del tablero
    fun isOutOfLimit(row: Int, col:Int): Boolean{
        if (row < 0 || row > (size -1)
            || col < 0 || col > (size - 1)){
            return true
        }
        return false
    }

    //Indica si la casilla del tablero tiene una bomba
    private fun hasBomb(row:Int, col:Int): Boolean{
        if(!isOutOfLimit(row, col)){
            val state = getStateOfCell(row, col)
            if(state == CellType.BOMB) return true
        }
        return false
    }

    //Devuelve el número de bombas que tiene la casilla alrededor
    private fun nearBombsOfCell(row:Int, col:Int): CellType{
        var countBomb = 0
        if(hasBomb(row, col)) return CellType.BOMB
        for(x in -1 .. 1)
            for(y in -1.. 1){
                if(x != 0 && y!=0){
                    if(hasBomb(row=row+x, col=col+y)) countBomb += 1
                }
            }
        return CellType.getCellType(countBomb)
    }

    //Devuelve el color de la celda del tablero de juego
    fun getBackgroundCellTable(numberCell:Int): Color{
        val state = getStateOfCell(numberCell)
        return if(isVisibleCell(numberCell)) {
            when (state) {
                CellType.VOID -> Color(0xFFCACCCC)
                CellType.ONE -> Color(0xFF6AC1E9)
                CellType.TWO -> Color(0xFFCF7D62)
                CellType.THREE -> Color(0xFF8FD691)
                else -> Color(0xFFCACCCC)
            }
        }else{
            Color(0xFF868A87)
        }
    }

    //Función que se ejecuta tras una click en una celda del tablero
    fun onClickCell(numberCell: Int){
        //Verificamos si la casilla pulsada es una bomba y si se ha
        //finalizado el juego. Si no es así iniciamos el recorrido de celdas
        if(!isBombClicked(numberCell) && !isEnded)
            traverseCells(numberCell)
    }

    //Muestra el contenido de la celda al usuario
    private fun showCell(numberCell:Int){
        visibleCells = visibleCells.mapIndexed { index, details ->
            if (numberCell == index) true
            else details
        }
    }

    //Recorre todas las celdas contiguas a la celda actual y las destapa siempre que no
    //haya bombas
    private fun traverseCells(numberCell:Int){
        //Comprobamos si se ha ganado la partida
        hasWin()

        //Mostramos el contenido de la celda actual en el tablero
        showCell(numberCell)

        //Comprobamos que la celda actual sea vacia
        if(getStateOfCell(numberCell) == CellType.VOID) {
            val row = numberCell / size
            val col = numberCell % size

            //Recorre celdas a izquierda, derecha, arriba y abajo
            decideContinueOrEnd(row=row-1, col=col)
            decideContinueOrEnd(row=row, col=col-1)
            decideContinueOrEnd(row=row, col=col+1)
            decideContinueOrEnd(row=row+1, col=col)
        }
    }

    //Decide si debemos continuar recorriendo celdas o parar
    private fun decideContinueOrEnd(row: Int, col:Int){
        val numberCell = (row*size)+col

        //Comprobamos si debemos continuar recorriendo celdas
        val decision = checkContinueTraversing(row,col)

        //Si la decisión es continuar, seguimos recorriendo celdas
        //en caso contrario paramos y mostramos el contenido de la
        //celda donde nos hemos detenido siempre que no sea una bomba
        if(decision == MinesWeeperViewModel.Decision.CONTINUE) {
            traverseCells(numberCell)
        }else if (decision == Decision.STOP){
            if(getStateOfCell(numberCell) != CellType.BOMB)
                showCell(numberCell)

            //Comprobamos si se ha ganado la partida
            hasWin()
        }
    }
    
    //Comprueba y decide si se debe parar o continuar con el recorrido
    //de celdas
    private fun checkContinueTraversing(row:Int, col:Int): Decision {
        if(!isOutOfLimit(row, col)) {
            if(getStateOfCell(row, col) == CellType.VOID
                && !isVisibleCell(row, col)
            ) {return Decision.CONTINUE}
            return Decision.STOP
        }
        return Decision.NOTHING
    }

    //Indica si se ha hecho clic sobre la casilla de una bomba y
    //en caso afirmativo muestra todas las bombas del tablero.
    private fun isBombClicked(numberCell: Int): Boolean{
        if(getStateOfCell(numberCell) == CellType.BOMB){
            showAllBombsAndFinish()
            isEnded = true
            return true
        }else{
            return false
        }
    }

    //Muestra todas las bombas del tablero
    private fun showAllBombsAndFinish(){
        visibleCells = visibleCells.mapIndexed { index, details ->
            if (getStateOfCell(index)==CellType.BOMB) true
            else details
        }
    }
}


