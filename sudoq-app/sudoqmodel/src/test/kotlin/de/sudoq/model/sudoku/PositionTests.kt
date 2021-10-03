package de.sudoq.model.sudoku

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PositionTests {

    @Test // constructor
    fun test() {
        val iList = intArrayOf(Int.MIN_VALUE, -73, -1, 0, 1, 64, Int.MAX_VALUE)
        val jList = intArrayOf(Int.MIN_VALUE, -83, -1, 0, 1, 63, Int.MAX_VALUE)
        for (i in iList) {
            for (j in jList) {
                if (i < 0 || j < 0) {
                    invoking { Position[i, j] }.`should throw`(IllegalArgumentException::class)
                } else {
                    val pos = Position[i, j]
                    pos.x.`should be equal to`(i)
                    pos.y.`should be equal to`(j)
                }
            }
        }
    }

    @ParameterizedTest
    @CsvSource(
        "1, 2, 3, 4, 0",
        "0, 0, 0, 0, 1",
        "4, 4, 4, 3, 0",
        "4, 5, 4, 5, 1",
        "7987, 21523, 7987, 21523, 1",
        "7987, 21523, 7988, 21521, 0")
    fun equalTest(pX: Int, pY: Int, qX: Int, qY: Int, expected: Int) {
        val posA = Position[pX, pY]
        val posB = Position[qX, qY]
        //posA.`should be equal to`(posB)
        val result = posA == posB
        Assert.assertTrue(
            "equals() works not correct.",
            result && expected == 1 || !result && expected == 0
        )
    }

    @Test
    fun equalTest2() {
        val pos = Position[1, 0]
        Assert.assertFalse("equal accepts null", pos.equals(null))
        Assert.assertFalse("equal accepts int", pos.equals(9))
    }

    @ParameterizedTest
    @CsvSource("1, 2, 3, 4, 0",
        "0, 0, 0, 0, 1",
        "4, 4, 4, 3, 0",
        "4, 5, 4, 5, 1",
        "7987, 21523, 7987, 21523, 1",
        "7987, 21523, 7988, 21521, 0"
    )
    fun hashCodeTest(pX: Int, pY: Int, qX: Int, qY: Int, expected: Int) {
        val posA = Position[2, 2]
        val posB = Position[2, 2]

        val eq = posA == posB
        val hash = posA.hashCode() == posB.hashCode()
        Assertions.assertFalse { eq && !hash }
    }

    @Test
    fun testToString() {
        val p = Position[5, 9]
        Assert.assertEquals(p.toString(), "5, 9")
    }

}