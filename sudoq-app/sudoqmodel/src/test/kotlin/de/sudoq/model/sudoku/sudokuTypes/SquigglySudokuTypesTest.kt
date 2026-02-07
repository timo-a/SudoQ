package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldHaveSingleItem
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

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
        val m: List<Position> =
            row(0, 0, 1, 2, 3) +
            row(1, 0, 1, 2) +
            row(2, 0, 1)
        assertions(m, c)
    }

    protected open fun constraintsB(c: Constraint) {
        val m: List<Position> =
            row(0, 4, 5, 6) +
            row(1, 3, 4, 5) +
            row(2, 2, 3, 4)
        assertions(m, c)
    }

    protected open fun constraintsC(c: Constraint) {
        val m: List<Position> =
            row(0, 7, 8) +
            row(1, 6, 7, 8) +
            row(2, 5, 6, 7, 8)
        assertions(m, c)
    }

    protected open fun constraintsD(c: Constraint) {
        val m: List<Position> =
            row(3,   0, 1, 2, 3) +
            row(4,   0, 1, 2) +
            row(5,   0, 1)
        assertions(m, c)
    }

    protected open fun constraintsE(c: Constraint) {
        val m: List<Position> =
            row(3,  4, 5, 6) +
            row(4,  3, 4, 5) +
            row(5,  2, 3, 4)
        assertions(m, c)
    }

    protected open fun constraintsF(c: Constraint) {
        val m: List<Position> =
            row(5-2, 7, 8) +
            row(5-1, 6, 7, 8) +
            row(5,   5, 6, 7, 8)
        assertions(m, c)
    }

    protected open fun constraintsG(c: Constraint) {
        val m: List<Position> =
            row(6,   0, 1, 2, 3) +
            row(6+1, 0, 1, 2) +
            row(6+2, 0, 1)
        assertions(m, c)
    }

    protected open fun constraintsH(c: Constraint) {
        val m: List<Position> =
            row(7-1,       4, 5, 6) +
            row(7,      3, 4, 5) +
            row(7+1, 2, 3, 4)
        assertions(m, c)
    }

    protected open fun constraintsI(c: Constraint) {
        val m: List<Position> =
            row(8 - 2,       7, 8) +
            row(8 - 1,    6, 7, 8) +
            row(8,     5, 6, 7, 8)
        assertions(m, c)
    }

    protected fun assertions(m: List<Position>, c: Constraint) {
        m.shouldContainAll(c)
        m.shouldHaveSize(c.size)
    }

    private fun row(r: Int, vararg c: Int): List<Position> = c.map { Position[it, r] }
}