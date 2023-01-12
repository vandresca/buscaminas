package com.minesweeper.minesweeper.ui

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import com.minesweeper.minesweeper.domain.Cell
import com.myfood.getOrAwaitValueTest
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test


internal class MinesWeeperViewModelTest {

    lateinit var minesWeeperViewModel: MinesWeeperViewModel

    @RelaxedMockK
    lateinit var matrix: LiveData<Array<Cell>>


    @Before
    fun onBefore(){
        minesWeeperViewModel = MinesWeeperViewModel()
    }

    @Test
    fun initTable() {
        minesWeeperViewModel.initTable()
        val size  = minesWeeperViewModel.matrix.value?.size
        Assert.assertEquals(size, 100)
    }


    @Test
    fun isOutOfLimit() {
        var result = minesWeeperViewModel.isOutOfLimit(-1, 6)
        Assert.assertEquals(result, true)
        result = minesWeeperViewModel.isOutOfLimit(0, 6)
        Assert.assertEquals(result, false)
        result = minesWeeperViewModel.isOutOfLimit(6, 0)
        Assert.assertEquals(result, false)
        result = minesWeeperViewModel.isOutOfLimit(6, -1)
        Assert.assertEquals(result, true)
        result = minesWeeperViewModel.isOutOfLimit(9, 1)
        Assert.assertEquals(result, false)
        result = minesWeeperViewModel.isOutOfLimit(10, 1)
        Assert.assertEquals(result, true)
        result = minesWeeperViewModel.isOutOfLimit(1, 9)
        Assert.assertEquals(result, false)
        result = minesWeeperViewModel.isOutOfLimit(1, 10)
        Assert.assertEquals(result, true)
    }


}