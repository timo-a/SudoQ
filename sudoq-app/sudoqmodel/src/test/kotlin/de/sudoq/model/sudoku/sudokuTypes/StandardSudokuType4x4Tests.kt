package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class StandardSudokuType4x4Tests {

    var sst44 = TypeBuilder.getType(SudokuTypes.standard4x4)

    @Test
    fun constraintsCountTest() {
        sst44.constraints.shouldHaveSize(4 + 4 + 4)
    }

    @Test
    fun constraintsSizeTest() {
        sst44.forEach { it.shouldHaveSize(4) }
    }

    @Test
    fun constraintsTest0() {

        val blocks = sst44.filter { it.toString().contains("Block 0") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(0, 1, 0, 1, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest1() {

        val blocks = sst44.filter { it.toString().contains("Block 1") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(2, 3, 0, 1, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest2() {

        val blocks = sst44.filter { it.toString().contains("Block 2") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(0, 1, 2, 3, blocks[0]).`should be true`()
    }

    @Test
    fun constraintsTest3() {

        val blocks = sst44.filter { it.toString().contains("Block 3") }
        blocks.shouldHaveSingleItem()
        allPosWithinBounds(2, 3, 2, 3, blocks[0]).`should be true`()
    }

    private fun allPosWithinBounds(
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        c: Constraint
    ): Boolean = c.all { it.x in minX..maxX && it.y in (minY..maxY) }



    @Test
    fun enumTypeTest() {
        sst44.enumType.`should be`(SudokuTypes.standard4x4)
    }
}