package de.sudoq.model.sudoku.sudokuTypes

import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class StandardSudokuType16x16Tests {

    val sst1616 = TypeBuilder.getType(SudokuTypes.standard16x16)

    @Test
    fun constraintsTest() {
        sst1616.constraints.shouldHaveSize(16 * 3)
        for (c in sst1616) c.shouldHaveSize(16)
    }

    @Test
    fun enumTypeTest() {
            sst1616.enumType.`should be`(SudokuTypes.standard16x16)
    }

    @Test
    fun buildComplexityConstraintTest() {
        val standard16x16 = TypeBuilder.getType(SudokuTypes.standard16x16)
        standard16x16.buildComplexityConstraint(null).`should be null`()
    }
}