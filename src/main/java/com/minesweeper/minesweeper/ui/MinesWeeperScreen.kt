package com.minesweeper.minesweeper.ui


import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minesweeper.R
import com.minesweeper.minesweeper.domain.CellType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import java.util.*
import kotlin.time.Duration.Companion.seconds

//Metodo raíz de los composable
//Implementamos una caja e inicializamos dentro de ella el tablero, la
//ubicación de las bombas y pintamos la pantalla
@Composable
fun MinesWeeperScreen(viewModel: MinesWeeperViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        viewModel.initTable()
        viewModel.initBombs()
        viewModel.initSurroundingBombs()
        PrintScreen(
            Modifier.align(Alignment.Center),
            viewModel
        )
    }
}

//Metodo que pinta la pantalla
//En la pantalla tenemos el texto que indica si hemos ganado
//los paneles (bombas y temporizador) y el tablero
//de juego
@Composable
fun PrintScreen(modifier: Modifier, viewModel: MinesWeeperViewModel) {
    Column(modifier = modifier) {
        Winner(modifier=Modifier.align(Alignment.CenterHorizontally),
            viewModel=viewModel)
        Panels(modifier = modifier, viewModel = viewModel)
        Spacer(modifier = Modifier.padding(10.dp))
        Table(modifier = modifier, viewModel = viewModel)
    }
}

//Metodo que muestra un rotulo indicando si es el caso que hemos ganado
@Composable
fun Winner(modifier:Modifier, viewModel: MinesWeeperViewModel){
    Box(modifier= modifier){
        if(viewModel.hasWin()) {
            Text(
                stringResource(id = R.string.congrats),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }
    }
}



//Metodo que pinta los paneles.
//Primero creamos una caja que los contendra
//Los paneles son: Panel de bombas en el juego y temporizador
@Composable
fun Panels(modifier: Modifier, viewModel: MinesWeeperViewModel) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp)
    ) {
        BombsPanel(Modifier.align(Alignment.CenterStart), viewModel)
        TimerPanel(Modifier.align(Alignment.TopEnd), viewModel)
    }
}

//Panel indicador de las bombas. Creamos una columna
//En primera posición colocamos el texto a modo título lo separamos
//con un spacer y añadimos el card con el número de bombas
@Composable
fun BombsPanel(modifier: Modifier, viewModel: MinesWeeperViewModel) {
    Column(modifier = modifier) {
        TextBombPanel()
        Spacer(modifier = Modifier.padding(5.dp))
        CardBombPanel(modifier = modifier, viewModel = viewModel)
    }
}

//Texto a modo título del panel de bombas
@Composable
fun TextBombPanel() {
    Text(
        stringResource(R.string.bombsLeft),
        color = Color.Black,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}

//Card indicativo del número de bombas
@Composable
fun CardBombPanel(modifier: Modifier, viewModel: MinesWeeperViewModel) {
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black))
            .width(100.dp)
            .height(60.dp),
        backgroundColor = Color.Black,
    ) { TextNumberBombs(modifier, viewModel) }
}

//Texto indicativo del número de bombas del card
@Composable
fun TextNumberBombs(modifier: Modifier, viewModel: MinesWeeperViewModel) {
    Text(
        viewModel.numberBombs.toString(),
        color = Color.Red,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(5.dp, 5.dp , 10.dp, 5.dp),
        textAlign = TextAlign.End,
    )
}

//Panel del temporizador. Igual que con el de bombas creamos una columna con
//un primer elemento a modo de titulo espaciado con un card del temporizador
@Composable
fun TimerPanel(modifier: Modifier, viewModel: MinesWeeperViewModel) {
    Column(modifier = modifier) {
        TextTimerPanel()
        Spacer(modifier = Modifier.padding(5.dp))
        Timer(modifier, viewModel)
    }
}

//Texto a modo título del panel del temporizador
@Composable
fun TextTimerPanel() {
    Text(
        stringResource(R.string.time),
        color = Color.Black,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}

//Temporizador
@Composable
fun Timer(modifier: Modifier, viewModel: MinesWeeperViewModel) {

    //Declaramos una variable como estado de compose inicializada a 0
    var ticks by remember { mutableStateOf(0) }

    //Iniciamos un contador que corra hasta que acabe el juego y que
    //tenga un retraso de un segundo. Por cada iteración aumentamos la
    //variable de estado declarada anteriormente
    LaunchedEffect(Unit) {
        while(!viewModel.isEnded) {
            delay(1.seconds)
            ticks++
        }
    }

    //Pintamos una card con el resultado que se va actualizando
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black))
            .width(150.dp)
            .height(60.dp),
        backgroundColor = Color.White,
        contentColor = Color.Red
    ) {TextNumbersTimer(ticks, modifier)}
}

//Texto que muestra el contador del temporizador
@Composable
fun TextNumbersTimer(ticks: Int, modifier: Modifier){
    Text(
        ticks.toString(),
        color = Color.Red,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(5.dp, 5.dp , 10.dp, 5.dp),
        textAlign = TextAlign.End,
    )
}

//Pinta el tablero de juego
@Composable
fun Table(modifier: Modifier, viewModel: MinesWeeperViewModel) {

    //Utilizamos el componente lazyverticalgrid para organizar una lista
    //de elementos en una cuadricula (grid). En cada celda pintamos una
    //CardCellTable
    LazyVerticalGrid(
        columns = GridCells.Adaptive(38.dp),
        content = {
            items(viewModel.size * viewModel.size) { cell ->
                CardCellTable(cell, viewModel)
            }
        })
}

//Pinta una celda del tablero de juego
@SuppressLint("UnrememberedMutableState")
@Composable
fun CardCellTable(numberCell: Int, viewModel: MinesWeeperViewModel) {

    //Obtenemos el estado y la visibilidad de la celda
    val state =viewModel.getStateOfCell(numberCell)
    val isVisibleCell = viewModel.isVisibleCell(numberCell)

    //Implementamos el card
    Card(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .width(38.dp)
            .height(38.dp)
            .clickable { viewModel.onClickCell(numberCell) },
        contentColor = Color.Black,
        backgroundColor = viewModel.getBackgroundCellTable(numberCell)
    ) {
        //Si la celda es visible. Mostramos el tipo de celda según corresponda
        if (isVisibleCell) {
            when (state) {
                CellType.BOMB -> BombImageCell()
                CellType.VOID -> TextCell("")
                else -> TextCell(state.value.toString())
            }
        }
    }
}

//Crea una imagen con el icono de la bomba
@Composable
fun BombImageCell() {
    Image(
        painter = painterResource(id = R.drawable.bomb),
        contentDescription = "Bomb icon",
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .width(38.dp)
            .height(38.dp)
            .clickable { println("Button Clicked!") }
    )
}

//Crea un texto con el número de bombas que tiene alrededor la bomba
@Composable
fun TextCell(number: String) {
    Text(
        text = number,
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    )
}
