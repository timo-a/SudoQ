package de.sudoq.controller.sudoku

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import de.sudoq.persistence.sudoku.SudokuBE
import de.sudoq.persistence.sudoku.SudokuMapper
import de.sudoq.persistence.sudokuType.SudokuTypeBE
import de.sudoq.persistence.sudokuType.SudokuTypeMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

class CompletedSymbolsTest {
    @ParameterizedTest
    @EnumSource(SudokuTypes::class)
    fun `empty boards and pencil notes have no completed symbols`(type: SudokuTypes) {
        val sudoku = loadSudoku(type)
        assertTrue(completedSymbols(sudoku).isEmpty())
        sudoku.forEach { cell ->
            sudoku.sudokuType.symbolIterator.forEach(cell::toggleNote)
        }
        assertTrue(completedSymbols(sudoku).isEmpty())
    }

    @ParameterizedTest
    @EnumSource(SudokuTypes::class)
    fun `each symbol completes independently and becomes incomplete after deletion`(type: SudokuTypes) {
        val sudoku = loadSudoku(type)
        for (symbol in sudoku.sudokuType.symbolIterator) {
            val cells = sudoku.filter { it.solution == symbol }
            cells.dropLast(1).forEach { it.currentValue = symbol }
            assertFalse(symbol in completedSymbols(sudoku))

            cells.last().currentValue = symbol
            assertEquals(setOf(symbol), completedSymbols(sudoku))

            cells.last().clearCurrentValue()
            assertTrue(completedSymbols(sudoku).isEmpty())
            cells.forEach(Cell::clearCurrentValue)
        }
    }

    @ParameterizedTest
    @EnumSource(SudokuTypes::class)
    fun `givens count toward completion`(type: SudokuTypes) {
        val sudoku = loadSudoku(type, withGivens = true)
        assertTrue(sudoku.any { !it.isEditable })
        sudoku.filter { it.isEditable }.forEach { it.currentValue = it.solution }
        assertEquals(sudoku.sudokuType.symbolIterator.toSet(), completedSymbols(sudoku))
    }

    @Test
    fun `nine occurrences do not complete a Samurai symbol`() {
        val sudoku = loadSudoku(SudokuTypes.samurai)
        val cells = sudoku.filter { it.solution == 0 }
        assertTrue(cells.size > 9)
        cells.take(9).forEach { it.currentValue = 0 }
        assertFalse(0 in completedSymbols(sudoku))
    }

    @Test
    fun `duplicates do not stand in for missing occurrences`() {
        val sudoku = loadSudoku(SudokuTypes.standard9x9)
        // Nine entries in one row meet a naive count threshold, but leave other regions empty.
        for (x in 0 until 9) sudoku.getCell(Position[x, 0]).currentValue = 0
        assertFalse(0 in completedSymbols(sudoku))

        sudoku.forEach(Cell::clearCurrentValue)
        sudoku.filter { it.solution == 0 }.forEach { it.currentValue = 0 }
        assertTrue(0 in completedSymbols(sudoku))
        sudoku.first { it.solution != 0 }.currentValue = 0
        assertFalse(0 in completedSymbols(sudoku))
    }

    @Test
    fun `completion uses visible entries rather than hidden solutions`() {
        val sudoku = loadSudoku(SudokuTypes.standard4x4)
        // Permuting symbols preserves every region, even though entries differ from the solution.
        sudoku.forEach { it.currentValue = (it.solution + 1) % 4 }
        assertTrue(sudoku.any { it.currentValue != it.solution })
        assertEquals(setOf(0, 1, 2, 3), completedSymbols(sudoku))
    }

    @Test
    fun `missing diagonal prevents completion in X Sudoku`() {
        val sudoku = loadSudoku(SudokuTypes.Xsudoku)
        // This valid standard-grid placement has nine ones but none on the main diagonal.
        val columns = listOf(1, 3, 6, 2, 5, 8, 0, 4, 7)
        columns.forEachIndexed { y, x -> sudoku.getCell(Position[x, y]).currentValue = 0 }
        assertTrue(sudoku.sudokuType.constraints
            .filter { it.name.startsWith("Row") || it.name.startsWith("Column") || it.name.startsWith("Block") }
            .all { region -> region.count { sudoku.getCurrentValue(it) == 0 } == 1 })
        assertFalse(0 in completedSymbols(sudoku))
    }

    @Test
    fun `types without full unique regions do not report completion`() {
        val type = loadSudoku(SudokuTypes.standard4x4).sudokuType
        val noRegions = SudokuType(
            type.enumType, type.numberOfSymbols, 0f, type.size, type.blockSize,
            emptyList(), emptyList(), emptyList(), type.ccb
        )
        assertTrue(completedSymbols(Sudoku(noRegions, Complexity.easy)).isEmpty())
    }
}

/** Read the shipped puzzle and rules so these tests exercise the actual supported variants. */
internal fun loadSudoku(type: SudokuTypes, withGivens: Boolean = false): Sudoku {
    val directory = File("src/main/assets/sudokus/$type")
    val helper = XmlHelper()
    val typeBE = SudokuTypeBE().apply {
        fillFromXml(helper.loadXml(File(directory, "$type.xml"))!!)
    }
    val sudokuType = SudokuTypeMapper.fromBE(typeBE)
    val sudokuBE = SudokuBE().apply {
        fillFromXml(helper.loadXml(File(directory, "easy/sudoku_1.xml"))!!, ReadRepo { sudokuType })
    }
    val sudoku = SudokuMapper.fromBE(sudokuBE)
    if (withGivens) return sudoku

    val solutions = PositionMap.Builder<Int>(sudokuType.size).from(sudokuType.validPositions) {
        sudoku.getCell(it).solution
    }
    return Sudoku(sudokuType, solutions, emptySet())
}
