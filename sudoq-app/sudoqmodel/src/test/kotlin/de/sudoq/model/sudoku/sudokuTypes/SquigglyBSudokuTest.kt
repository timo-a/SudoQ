package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test
import java.util.ArrayList

class SquigglyBSudokuTest : SquigglySudokuTypesTest() {

    override var squig = TypeBuilder.getType(SudokuTypes.squigglyb)

    override fun constraintsA(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[0, 0])
        m.add(Position[1, 0])
        m.add(Position[2, 0])
        m.add(Position[3, 0])
        m.add(Position[4, 0])
        m.add(Position[0, 1])
        m.add(Position[1, 1])
        m.add(Position[0, 2])
        m.add(Position[0, 3])
        assertions(m, c)
    }

    override fun constraintsB(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[5, 0])
        m.add(Position[6, 0])
        m.add(Position[7, 0])
        m.add(Position[8, 0])
        m.add(Position[7, 1])
        m.add(Position[8, 1])
        m.add(Position[8, 2])
        m.add(Position[8, 3])
        m.add(Position[8, 4])
        assertions(m, c)
    }

    override fun constraintsC(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[2, 1])
        m.add(Position[3, 1])
        m.add(Position[4, 1])
        m.add(Position[5, 1])
        m.add(Position[1, 2])
        m.add(Position[2, 2])
        m.add(Position[5, 2])
        m.add(Position[5, 3])
        m.add(Position[6, 3])
        assertions(m, c)
    }

    override fun constraintsD(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[6, 1])
        m.add(Position[6, 2])
        m.add(Position[7, 2])
        m.add(Position[7, 3])
        m.add(Position[7, 4])
        m.add(Position[5, 5])
        m.add(Position[6, 5])
        m.add(Position[7, 5])
        m.add(Position[5, 6])
        assertions(m, c)
    }

    override fun constraintsE(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[3, 2])
        m.add(Position[1, 3])
        m.add(Position[2, 3])
        m.add(Position[3, 3])
        m.add(Position[1, 4])
        m.add(Position[1, 5])
        m.add(Position[1, 6])
        m.add(Position[2, 6])
        m.add(Position[2, 7])
        assertions(m, c)
    }

    override fun constraintsF(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[4, 2])
        m.add(Position[4, 3])
        m.add(Position[2, 4])
        m.add(Position[3, 4])
        m.add(Position[4, 4])
        m.add(Position[5, 4])
        m.add(Position[6, 4])
        m.add(Position[4, 5])
        m.add(Position[4, 6])
        assertions(m, c)
    }

    override fun constraintsG(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[0, 4])
        m.add(Position[0, 5])
        m.add(Position[0, 6])
        m.add(Position[0, 7])
        m.add(Position[1, 7])
        m.add(Position[0, 8])
        m.add(Position[1, 8])
        m.add(Position[2, 8])
        m.add(Position[3, 8])
        assertions(m, c)
    }

    override fun constraintsH(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[2, 5])
        m.add(Position[3, 5])
        m.add(Position[3, 6])
        m.add(Position[6, 6])
        m.add(Position[7, 6])
        m.add(Position[3, 7])
        m.add(Position[4, 7])
        m.add(Position[5, 7])
        m.add(Position[6, 7])
        assertions(m, c)
    }

    override fun constraintsI(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[8, 5])
        m.add(Position[8, 6])
        m.add(Position[7, 7])
        m.add(Position[8, 7])
        m.add(Position[4, 8])
        m.add(Position[5, 8])
        m.add(Position[6, 8])
        m.add(Position[7, 8])
        m.add(Position[8, 8])
        assertions(m, c)
    }

    @Test
    fun enumTypeTests() {
        squig.enumType.`should be`(SudokuTypes.squigglyb)
    }
}