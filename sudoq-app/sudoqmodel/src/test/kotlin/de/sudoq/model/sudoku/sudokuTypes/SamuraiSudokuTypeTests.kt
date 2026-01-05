package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Constraint
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import java.util.*

class SamuraiSudokuTypeTests {

    private val samurai: SudokuType = TypeBuilder.getType(SudokuTypes.samurai)

    @Test
    fun constraintsTest() {

        samurai.constraints.shouldHaveSize(5 * 9 + 5 * 9 + 5 * 9 - 4)

        for (c in samurai) c.shouldHaveSize(9)
        checkBlockOfRows(0, 0, 0)
        checkBlockOfRows(12, 0, 9)
        checkBlockOfRows(0, 12, 18)
        checkBlockOfRows(12, 12, 27)
        checkBlockOfRows(6, 6, 36)
        checkBlockOfCols(0, 0, 0)
        checkBlockOfCols(12, 0, 9)
        checkBlockOfCols(0, 12, 18)
        checkBlockOfCols(12, 12, 27)
        checkBlockOfCols(6, 6, 36)
        checkBlockofBlocks(0, 0, 0)
        checkBlockofBlocks(12, 0, 9)
        checkBlockofBlocks(0, 12, 18)
        checkBlockofBlocks(12, 12, 27)
        singleBlocktest(9, 6, 36)
        singleBlocktest(6, 9, 37)
        singleBlocktest(9, 9, 38)
        singleBlocktest(12, 9, 39)
        singleBlocktest(9, 12, 40)
    }

    private fun checkBlockOfRows(x: Int, y: Int, startIndex: Int) {
        var counter = 0
        val b = BitSet(9)
        for (c in samurai) {
            for (i in 0..8) {
                if (c.toString() == "Row " + (startIndex + i)) {
                    b.flip(i)
                    counter++
                    allPosWithinBounds(x + 0, x + 8, y + i, y + i, c).`should be true`()
                }
            }
        }
        b.flip(0, 9)
        b.isEmpty.`should be true`()
        counter.`should be`(9)
    }

    private fun checkBlockOfCols(x: Int, y: Int, startIndex: Int) {
        var counter = 0
        val b = BitSet(9)
        for (c in samurai) {
            for (i in 0..8) {
                if (c.toString() == "Column " + (startIndex + i)) {
                    b.flip(i)
                    counter++
                    allPosWithinBounds(
                        x + i,
                        x + i,
                        y + 0,
                        y + 8, c).`should be true`()
                }
            }
        }
        b.flip(0, 9)
        b.isEmpty.`should be true`()
        counter.`should be`(9)
    }

    private fun checkBlockofBlocks(x: Int, y: Int, startIndex: Int) {
        var counter = 0
        val b = BitSet(9)
        for (c in samurai) {
            for (i in 0..8) {
                if (c.toString() == "Block " + (startIndex + i)) {
                    b.flip(i)
                    counter++
                    allPosWithinBounds(
                        x + i % 3 * 3,
                        x + i % 3 * 3 + 3,
                        y + i / 3 * 3,
                        y + i / 3 * 3 + 3,
                        c).`should be true`()
                }
            }
        }
        b.flip(0, 9)
        b.isEmpty.`should be true`()
        counter.`should be`(9)
    }

    private fun singleBlocktest(x: Int, y: Int, startIndex: Int) {
        var counter = 0
        for (c in samurai) {
            if (c.toString() == "Block $startIndex") {
                counter++
                allPosWithinBounds(x, x + 3, y, y + 3, c).`should be true`()
            }
        }
        counter `should be` 1
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
        samurai.enumType.`should be`(SudokuTypes.samurai)
    }

    @Test
    fun buildComplexityConstraintTest() {
        samurai.buildComplexityConstraint(null).`should be null`()
    }
}