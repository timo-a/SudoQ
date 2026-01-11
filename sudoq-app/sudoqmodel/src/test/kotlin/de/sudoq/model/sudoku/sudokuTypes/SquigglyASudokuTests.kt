package de.sudoq.model.sudoku.sudokuTypes

import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test

class SquigglyASudokuTests {

    private val squigglyA: SudokuType = TypeBuilder.getType(SudokuTypes.squigglya)

    @Test
    fun enumTypeTests() {
        squigglyA.enumType.`should be`(SudokuTypes.squigglya)
    }
}
