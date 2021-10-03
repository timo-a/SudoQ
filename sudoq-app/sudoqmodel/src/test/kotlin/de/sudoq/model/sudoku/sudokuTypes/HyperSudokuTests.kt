package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Position
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

//TODO since we no longer have classes maybe we can merge some of these tests?
class HyperSudokuTests {

    var stHy = TypeBuilder.getType(SudokuTypes.HyperSudoku)

    @Test
    fun counstraintCountTest() {
        stHy.shouldHaveSize(9 + 9 + 9 + 4)
    }

    @Test
    fun constraintsTest0() {
        val reference = (0..8).map { Position[1 + it % 3, 1 + it / 3] }

        val blocks = stHy.filter { it.toString() == "Extra block 0"}
        blocks.shouldHaveSingleItem()
        val c = blocks[0]
        c.shouldHaveSize(9)
        c.shouldContainAll(reference)
        //we know reference to be size 9 and all different -> c must also be all different
    }

    @Test
    fun constraintsTest1() {
        val reference = (0..8).map { Position[5 + it % 3, 1 + it / 3] }

        val blocks = stHy.filter { it.toString() == "Extra block 1"}
        blocks.shouldHaveSingleItem()
        val c = blocks[0]
        c.shouldHaveSize(9)
        c.shouldContainAll(reference)
        //we know reference to be size 9 and all different -> c must also be all different
    }

    @Test
    fun constraintsTest2() {
        val reference = (0..8).map { Position[1 + it % 3, 5 + it / 3] }

        val blocks = stHy.filter { it.toString() == "Extra block 2"}
        blocks.shouldHaveSingleItem()
        val c = blocks[0]
        c.shouldHaveSize(9)
        c.shouldContainAll(reference)
        //we know reference to be size 9 and all different -> c must also be all different
    }

    @Test
    fun constraintsTest3() {
        val reference = (0..8).map { Position[5 + it % 3, 5 + it / 3] }

        val blocks = stHy.filter { it.toString() == "Extra block 3"}
        blocks.shouldHaveSingleItem()
        val c = blocks[0]
        c.shouldHaveSize(9)
        c.shouldContainAll(reference)
        //we know reference to be size 9 and all different -> c must also be all different
    }


    @Test
    fun getEnumTypeTest() {
        stHy.enumType.`should be`(SudokuTypes.HyperSudoku)
    }

    @Test
    fun buildComplexityConstraintTest() {
        stHy.buildComplexityConstraint(null).`should be null`()
    }

}