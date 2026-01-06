package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class StandardSudokuType6x6Tests {

    val sst66 = TypeBuilder.getType(SudokuTypes.standard6x6)

    @Test
    fun constraintsCountTest() {
        sst66.constraints.shouldHaveSize(6 + 6 + 6)
    }

    @Test
    fun constraintsSizeTest() {
        sst66.forEach { it.shouldHaveSize(6) }
    }

    @Test
    fun constraintsTest0() {

        val blocks = sst66.filter { it.toString().contains("Block 0") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(0, 2, 0, 1, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest1() {

        val blocks = sst66.filter { it.toString().contains("Block 1") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(3, 5, 0, 1, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest2() {

        val blocks = sst66.filter { it.toString().contains("Block 2") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(0, 2, 2, 3, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest3() {

        val blocks = sst66.filter { it.toString().contains("Block 3") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(3, 5, 2, 3, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest4() {

        val blocks = sst66.filter { it.toString().contains("Block 4") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(0, 2, 4, 5, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest5() {

        val blocks = sst66.filter { it.toString().contains("Block 5") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(3, 5, 4, 5, blocks[0]).`should be true`()
    }


    private fun allPosWithinBounds(
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        c: Constraint
    ): Boolean = c.all { it.x in minX..maxX && it.y in (minY..maxY) }

    @Test
    fun blockSizeTest() {
            val p = sst66.blockSize
            p.x.`should be`(3)
            p.y.`should be`(2)
        }

    @Test
    fun enumTypeTest() {
        sst66.enumType.`should be`(SudokuTypes.standard6x6)
    }
}