package com.minesweeper.minesweeper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import com.minesweeper.ui.theme.MinesWeeperTheme

//Actividad Principal
class MinesWeeperActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Indicamos el inicio de los componentes composable
        setContent {
            //Creamos un tema y dentro una superficie con un color
            //amarillo suave. Luego llamamos al MetodoMinesWeeperScreen
            MinesWeeperTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE9E7D2)
                ) {
                    MinesWeeperScreen(MinesWeeperViewModel())
                }
            }
        }
    }
}