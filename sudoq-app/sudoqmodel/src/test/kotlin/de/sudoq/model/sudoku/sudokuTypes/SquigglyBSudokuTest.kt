package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test

class SquigglyBSudokuTest : SquigglySudokuTypesTest() {

    override var squig = TypeBuilder.getType(SudokuTypes.squigglyb)

    override fun constraintsA(c: Constraint) {
        val m: List<Position> =
            row(0,  0, 1, 2, 3, 4) +
            row(1,  0, 1) +
            row(2,  0) +
            row(3,  0)
        assertions(m, c)
    }

    override fun constraintsB(c: Constraint) {
        val m: List<Position> =
            row(0,  5, 6, 7, 8) +
            row(1,        7, 8) +
            row(2,           8) +
            row(3,           8) +
            row(4,           8)
        assertions(m, c)
    }

    override fun constraintsC(c: Constraint) {
        val m: List<Position> =
            row(1,     2, 3, 4, 5) +
            row(2,  1, 2,       5) +
            row(3,              5, 6)
        assertions(m, c)
    }

    override fun constraintsD(c: Constraint) {
        val m: List<Position> =
            row(1,     6) +
            row(2,     6, 7) +
            row(3,        7) +
            row(4,        7) +
            row(5,  5, 6, 7) +
            row(6,  5)
        assertions(m, c)
    }

    override fun constraintsE(c: Constraint) {
        val m: List<Position> =
            row(2,        3) +
            row(3,  1, 2, 3) +
            row(4,  1) +
            row(5,  1) +
            row(6,  1, 2) +
            row(7,     2)
        assertions(m, c)
    }

    override fun constraintsF(c: Constraint) {
        val m: List<Position> =
            row(2,        4) +
            row(3,        4) +
            row(4,  2, 3, 4, 5, 6) +
            row(5,        4) +
            row(6,        4)
        assertions(m, c)
    }

    override fun constraintsG(c: Constraint) {
        val m: List<Position> =
            row(4,  0) +
            row(5,  0) +
            row(6,  0) +
            row(7,  0, 1) +
            row(8,  0, 1, 2, 3)
        assertions(m, c)
    }

    override fun constraintsH(c: Constraint) {
        val m: List<Position> =
            row(5,  2, 3) +
            row(6,     3,       6, 7) +
            row(7,     3, 4, 5, 6)
        assertions(m, c)
    }

    override fun constraintsI(c: Constraint) {
        val m: List<Position> =
            row(5,  8) +
            row(6,  8) +
            row(7,  7, 8) +
            row(8,  4, 5, 6, 7, 8)
        assertions(m, c)
    }

    @Test
    fun enumTypeTests() {
        squig.enumType.`should be`(SudokuTypes.squigglyb)
    }

    private fun row(r: Int, vararg c: Int): List<Position> = c.map { Position[it, r] }
}