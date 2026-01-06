package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Position
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 20, 100])
    fun arbitrary(numOfSymbols: Int) {
        val arbitrarySudokuType = SudokuType(SudokuTypes.standard4x4, numOfSymbols, 0f, Position[1,1],
            Position[1,1], ArrayList(), ArrayList(), ArrayList(), ComplexityConstraintBuilder())
        testEquality(arbitrarySudokuType)
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