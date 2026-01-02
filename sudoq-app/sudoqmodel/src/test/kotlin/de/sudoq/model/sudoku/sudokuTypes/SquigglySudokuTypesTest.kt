package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.util.ArrayList

open class SquigglySudokuTypesTest {

    open val squig = TypeBuilder.getType(SudokuTypes.stairstep)

    @Test
    fun constraintsCountTest() {
        squig.constraints.shouldHaveSize(27)
    }

    @Test
    fun constraintsSizeTest() {
        squig.forEach { it.shouldHaveSize(9) }
    }

    @Test
    fun constraintTestA() {

        val blocks = squig.filter { it.toString().contains("Block A") }
        blocks.shouldHaveSingleItem()
        constraintsA(blocks[0])

    }

    @Test
    fun constraintTestB() {

        val blocks = squig.filter { it.toString().contains("Block B") }
        blocks.shouldHaveSingleItem()
        constraintsB(blocks[0])

    }

    @Test
    fun constraintTestC() {

        val blocks = squig.filter { it.toString().contains("Block C") }
        blocks.shouldHaveSingleItem()
        constraintsC(blocks[0])

    }

    @Test
    fun constraintTestD() {

        val blocks = squig.filter { it.toString().contains("Block D") }
        blocks.shouldHaveSingleItem()
        constraintsD(blocks[0])

    }

    @Test
    fun constraintTestE() {

        val blocks = squig.filter { it.toString().contains("Block E") }
        blocks.shouldHaveSingleItem()
        constraintsE(blocks[0])

    }

    @Test
    fun constraintTestF() {

        val blocks = squig.filter { it.toString().contains("Block F") }
        blocks.shouldHaveSingleItem()
        constraintsF(blocks[0])

    }

    @Test
    fun constraintTestG() {

        val blocks = squig.filter { it.toString().contains("Block G") }
        blocks.shouldHaveSingleItem()
        constraintsG(blocks[0])

    }

    @Test
    fun constraintTestH() {

        val blocks = squig.filter { it.toString().contains("Block H") }
        blocks.shouldHaveSingleItem()
        constraintsH(blocks[0])

    }

    @Test
    fun constraintTestI() {

        val blocks = squig.filter { it.toString().contains("Block I") }
        blocks.shouldHaveSingleItem()
        constraintsI(blocks[0])

    }


    protected open fun constraintsA(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[0, 0])
        m.add(Position[1, 0])
        m.add(Position[2, 0])
        m.add(Position[3, 0])
        m.add(Position[0, 0 + 1])
        m.add(Position[1, 0 + 1])
        m.add(Position[2, 0 + 1])
        m.add(Position[0, 0 + 2])
        m.add(Position[1, 0 + 2])
        assertions(m, c)
    }

    protected open fun constraintsB(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[4, 1 - 1])
        m.add(Position[5, 1 - 1])
        m.add(Position[6, 1 - 1])
        m.add(Position[3, 1])
        m.add(Position[4, 1])
        m.add(Position[5, 1])
        m.add(Position[2, 1 + 1])
        m.add(Position[3, 1 + 1])
        m.add(Position[4, 1 + 1])
        assertions(m, c)
    }

    protected open fun constraintsC(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[7, 2 - 2])
        m.add(Position[8, 2 - 2])
        m.add(Position[6, 2 - 1])
        m.add(Position[7, 2 - 1])
        m.add(Position[8, 2 - 1])
        m.add(Position[5, 2])
        m.add(Position[6, 2])
        m.add(Position[7, 2])
        m.add(Position[8, 2])
        assertions(m, c)
    }

    protected open fun constraintsD(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[0, 3])
        m.add(Position[1, 3])
        m.add(Position[2, 3])
        m.add(Position[3, 3])
        m.add(Position[0, 3 + 1])
        m.add(Position[1, 3 + 1])
        m.add(Position[2, 3 + 1])
        m.add(Position[0, 3 + 2])
        m.add(Position[1, 3 + 2])
        assertions(m, c)
    }

    protected open fun constraintsE(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[4, 4 - 1])
        m.add(Position[5, 4 - 1])
        m.add(Position[6, 4 - 1])
        m.add(Position[3, 4])
        m.add(Position[4, 4])
        m.add(Position[5, 4])
        m.add(Position[2, 4 + 1])
        m.add(Position[3, 4 + 1])
        m.add(Position[4, 4 + 1])
        assertions(m, c)
    }

    protected open fun constraintsF(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[7, 5 - 2])
        m.add(Position[8, 5 - 2])
        m.add(Position[6, 5 - 1])
        m.add(Position[7, 5 - 1])
        m.add(Position[8, 5 - 1])
        m.add(Position[5, 5])
        m.add(Position[6, 5])
        m.add(Position[7, 5])
        m.add(Position[8, 5])
        assertions(m, c)
    }

    protected open fun constraintsG(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[0, 6])
        m.add(Position[1, 6])
        m.add(Position[2, 6])
        m.add(Position[3, 6])
        m.add(Position[0, 6 + 1])
        m.add(Position[1, 6 + 1])
        m.add(Position[2, 6 + 1])
        m.add(Position[0, 6 + 2])
        m.add(Position[1, 6 + 2])
        assertions(m, c)
    }

    protected open fun constraintsH(c: Constraint) {
        val m: MutableList<Position> = ArrayList()
        m.add(Position[4, 7 - 1])
        m.add(Position[5, 7 - 1])
        m.add(Position[6, 7 - 1])
        m.add(Position[3, 7])
        m.add(Position[4, 7])
        m.add(Position[5, 7])
        m.add(Position[2, 7 + 1])
        m.add(Position[3, 7 + 1])
        m.add(Position[4, 7 + 1])
        assertions(m, c)
    }

    protected open fun constraintsI(c: Constraint) {
        val m: List<Position> = listOf(
            Position[7, 8 - 2],
            Position[8, 8 - 2],
            Position[6, 8 - 1],
            Position[7, 8 - 1],
            Position[8, 8 - 1],
            Position[5, 8],
            Position[6, 8],
            Position[7, 8],
            Position[8, 8])
        assertions(m, c)
    }

    protected fun assertions(m: List<Position>, c: Constraint) {
        m.shouldContainAll(c)
        m.size.`should be equal to`(c.size)
    }
}