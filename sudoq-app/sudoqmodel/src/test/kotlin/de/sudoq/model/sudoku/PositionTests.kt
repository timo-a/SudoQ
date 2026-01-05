package de.sudoq.model.sudoku

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.amshove.kluent.`should not be equal to`
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
        "0, 0, 0, 0",
        "4, 5, 4, 5",
        "7987, 21523, 7987, 21523")
    fun `same coordinates should be equal`(pX: Int, pY: Int, qX: Int, qY: Int) {
        val posA = Position[pX, pY]
        val posB = Position[qX, qY]
        posA `should be equal to` posB
        posA.hashCode() `should be equal to` posB.hashCode()
    }

    @ParameterizedTest
    @CsvSource(
        "1, 2, 3, 4",
        "4, 4, 4, 3",
        "7987, 21523, 7988, 21521")
    fun `different coordinates should not be equal`(pX: Int, pY: Int, qX: Int, qY: Int) {
        val posA = Position[pX, pY]
        val posB = Position[qX, qY]
        posA `should not be equal to` posB
        posA.hashCode() `should not be equal to` posB.hashCode()
    }

    @Test
    fun equalTest2() {
        val pos = Position[1, 0]
        pos `should not be equal to` null
        pos `should not be equal to` 9 //wrong type can be passed, but is not equal
    }

    @Test
    fun testToString() {
        Position[5, 9].toString() `should be equal to` "5, 9"
    }

}