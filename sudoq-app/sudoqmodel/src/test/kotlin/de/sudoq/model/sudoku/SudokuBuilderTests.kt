package de.sudoq.model.sudoku

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should match all with`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test

internal class SudokuBuilderTests {
    private val str: ReadRepo<SudokuType> = SudokuTypeRepo4Tests()

    var cell: Cell? = null

    @Test
    fun initialisation() = SudokuTypes.entries.forEach(::testBuildergeneric)

    private fun testBuildergeneric(t: SudokuTypes) {
        val sudoku = SudokuBuilder(t, str).createSudoku()
        sudoku.sudokuType.validPositions
            .mapNotNull { sudoku.getCellNullable(it) }
            .`should match all with` { it.currentValue == Cell.EMPTYVAL }
    }

    @Test
    fun builderWithSolutions() {
        val sb = SudokuBuilder(SudokuTypes.standard9x9, str)
        sb.addSolution(Position[0, 0], 5)
        sb.setFixed(Position[0, 0])
        sb.addSolution(Position[0, 1], 3)
        val s = sb.createSudoku()

        s.getCell(Position[0, 0]).solution `should be` 5
        s.getCell(Position[0, 0]).currentValue `should be` 5
        s.getCell(Position[0, 1]).solution `should be` 3
        s.getCell(Position[0, 1]).currentValue `should be` Cell.EMPTYVAL

        invoking { sb.addSolution(Position[1, 3], -5) } `should throw` IllegalArgumentException::class
        invoking { sb.addSolution(Position[1, 3], 9) } `should throw` IllegalArgumentException::class
    }
}