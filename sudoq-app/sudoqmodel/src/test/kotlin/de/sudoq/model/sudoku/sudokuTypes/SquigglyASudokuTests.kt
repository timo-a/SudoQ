package de.sudoq.model.sudoku.sudokuTypes

import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should be`
import org.junit.Test

class SquigglyASudokuTests {

    private val squigglyA: SudokuType = TypeBuilder.getType(SudokuTypes.squigglya)

    @Test
    fun buildComplexityConstraintTest() {
		squigglyA.buildComplexityConstraint(null).`should be null`();
    }

    @Test
    fun enumTypeTests() {
        squigglyA.enumType.`should be`(SudokuTypes.squigglya)
    }
}