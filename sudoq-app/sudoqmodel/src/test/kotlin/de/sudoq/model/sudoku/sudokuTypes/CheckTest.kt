package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test

class CheckTest {

    var su1 = intArrayOf(
        9, 5, 8,  3, 1, 2,  7, 6, 4,
        4, 6, 1,  5, 7, 9,  8, 2, 3,
        3, 7, 2,  4, 6, 8,  9, 5, 1,

        8, 9, 6,  1, 2, 3,  5, 4, 7,
        1, 4, 3,  7, 9, 5,  2, 8, 6,
        5, 2, 7,  6, 8, 4,  3, 1, 9,

        7, 8, 5,  9, 4, 1,  6, 3, 2,
        2, 1, 9,  8, 3, 6,  4, 7, 5,
        6, 3, 4,  2, 5, 7,  1, 9, 8 )

    @Test
    fun Checktest() {
        for (i in su1.indices) {
            su1[i]--
        }
        su1[0].`should be`(8);

        val s99 = TypeBuilder.getType(SudokuTypes.standard9x9)
        val map = PositionMap(Position[9, 9], s99.validPositions) { pos -> su1[pos.y * 9 + pos.x]}
        val sudoku1 = Sudoku(s99, map, PositionMap(Position[9, 9]))
        for (f in sudoku1) f.currentValue = f.solution
        sudoku1.sudokuType.checkSudoku(sudoku1).`should be true`()
        sudoku1.getCell(Position[0, 0])!!.currentValue = 5
        sudoku1.sudokuType.checkSudoku(sudoku1).`should be false`()
    }
}