package de.sudoq.model.sudoku.sudokuTypes

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

class SymbolIteratorTest {

    @Test
    fun test4x4() {
        val sst = TypeBuilder.getType(SudokuTypes.standard4x4)
        testEquality(sst)
    }

    @Test
    fun test9x9() {
        val sst = TypeBuilder.getType(SudokuTypes.standard9x9)
        testEquality(sst)
    }

    @Test
    fun test6x6() {
        val sst = TypeBuilder.getType(SudokuTypes.standard6x6)
        testEquality(sst)
    }

    @Test
    fun test16x16() {
        val sst = TypeBuilder.getType(SudokuTypes.standard16x16)
        testEquality(sst)
    }

    @Test
    fun arbitrary() {
        val sst = TypeBuilder.getType(SudokuTypes.standard4x4)
        for (i in intArrayOf(1, 2, 3, 20, 100)) {
            sst.setNumberOfSymbols(i)
            testEquality(sst)
        }
    }

    private fun testEquality(sst: SudokuType?) {
        var counter = 0
        for (i in sst!!.symbolIterator) {
            i.`should be`(counter++)
        }
        sst.numberOfSymbols.`should be`(counter)
        sst.size.`should not be null`()
    }
}