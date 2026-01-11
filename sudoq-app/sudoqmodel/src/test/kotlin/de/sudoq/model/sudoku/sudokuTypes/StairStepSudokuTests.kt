package de.sudoq.model.sudoku.sudokuTypes

import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test

class StairStepSudokuTests {

    var stair = TypeBuilder.getType(SudokuTypes.stairstep)

    @Test
    fun enumTypeTests() {
        stair.enumType.`should be`(SudokuTypes.stairstep)
    }
}