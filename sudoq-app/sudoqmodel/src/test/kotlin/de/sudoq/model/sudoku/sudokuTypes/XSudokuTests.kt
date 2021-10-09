package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Position
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class XSudokuTests {
    private val stX: SudokuType = TypeBuilder.getType(SudokuTypes.Xsudoku)

    @Test
    fun counstraintCountTest() {
        stX.shouldHaveSize(9 + 9 + 9 + 2)
    }

    @Test
    fun constraintsUpDiagonalTest() {
        val dur = (0..8).map { Position[it, 8 - it] }

        val blocks = stX.filter { it.toString().contains("diagonal up right")}
        blocks.shouldHaveSingleItem()
        val c = blocks[0]

        c.shouldHaveSize(9)
        c.shouldContainAll(dur)
    }

    @Test
    fun constraintsDownDiagonalTest() {
        val ddr = (0..8).map { Position[it, it] }

        val blocks = stX.filter { it.toString().contains("diagonal down right")}
        blocks.shouldHaveSingleItem()
        val c = blocks[0]

        c.shouldHaveSize(9)
        c.shouldContainAll(ddr)
    }

    @Test
    fun enumTypeTest() {
        stX.enumType.`should be`(SudokuTypes.Xsudoku)
    }

    @Test
    fun buildComplexityConstraintTest() {
        stX.buildComplexityConstraint(null).`should be null`()
    }
}