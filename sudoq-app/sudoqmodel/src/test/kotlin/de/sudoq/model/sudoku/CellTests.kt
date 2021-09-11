package de.sudoq.model.sudoku

import org.amshove.kluent.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CellTests {


    @Test
    fun `should hold the solution that is passed`() {
        val cell = Cell(false, solution=5, -1, 5)
        cell.currentValue `should be equal to` 5
    }

    @Test
    fun `should not be possible to initialize with negative solution`() {
        invoking { Cell(false, solution=-2, -1, 3) } `should throw`
        IllegalArgumentException::class
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 5, 10, 15])
    fun `should retain given solution`(solution: Int) {
        val f = Cell(false, solution, -1, solution)
        f.solution `should be equal to` solution
    }

    @Nested
    inner class SetCurrentValue {

        @Test
        fun `should not accept negative current value`() {
            val f = Cell(true, 8, -1, 20)
            invoking { run { f.currentValue = (Cell.EMPTYVAL - 1) } } `should throw`
                    IllegalArgumentException::class
        }

        @Test
        fun `should not accept negative current value with other method`() {
            val f = Cell(true, 8, -1, 20)
            invoking { f.setCurrentValue(Cell.EMPTYVAL - 1, false) } `should throw`
                    IllegalArgumentException::class
        }

        @ParameterizedTest
        @ValueSource(ints = [0,5,10,15])
        fun `should set value`(i: Int) {
            val f = Cell(true, 8, -1, 20)

            f.currentValue = i
			f.currentValue `should be equal to` i
		}
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class ToggleNote {
        var f = Cell(-1, 1)

        @Order(1)
        @ParameterizedTest
        @ValueSource(ints = [0,1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,17,18,19])
        fun `should toggle`(i: Int) {
            f.toggleNote(i)
            f.isNoteSet(i).`should be true`()
        }

        @Order(2)
        @ParameterizedTest
        @ValueSource(ints = [0,1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,17,18,19])
        fun `should untoggle`(i: Int) {
            f.toggleNote(i)
            f.isNoteSet(i). `should be false`()
        }
    }

    @Test
    fun `should return the values it is initialized it with`() {
        val f = Cell(true, 6, -1, 9)
        f.isEditable.`should be true`()
        f.solution `should be equal to` 6
        f.id `should be equal to` (-1)
        f.numberOfValues `should be equal to` 9

        val g = Cell(false, 6, -1, 9)
        g.isEditable.`should be false`()
    }

    @Test
    fun `should not change when not editable`() {
        val f = Cell(false, 6, -1, 9)

        val initialValue = f.currentValue

        f.isEditable.`should be false`()

		f.currentValue = 3

        f.currentValue `should be` initialValue

        f.isNotWrong.`should be true`()

		f.currentValue = -1
        f.currentValue `should be` initialValue
        f.isNotWrong.`should be true`()
    }

    @Test
    fun testIsSolvedCorrect() {
        val f = Cell(true, 5, -1, 9)
        f.isSolvedCorrect.`should be false`()

        f.currentValue = 5
        f.isSolvedCorrect.`should be true`()

        f.currentValue = 4
        f.isSolvedCorrect.`should be false`()

        f.currentValue = 5
        f.isSolvedCorrect.`should be true`()
    }

    @Test
    fun testIsNotWrong() {
        val f = Cell(true, 4, -1, 9)
        f.currentValue = 0
        f.isNotWrong.`should be false`()
    }
    ///

    @Test
    fun testEqual() {
        val f = Cell(true, 3, -1, 9)
        val g = Cell(true, 3, -1, 9)

        f `should be equal to` g

        g.toggleNote(2)
        f `should not be equal to` g

        g.toggleNote(2)//revert
        f `should be equal to` g

        g.currentValue = 4
        f `should not be equal to` g
        f `should not be` null

        val h = Cell(false, 2, -1, 9)
        f `should not be equal to` h
    }

    @Test
    fun testClearIsEmpty() {
        val f = Cell(true, 3, -1, 9)
        f.currentValue = 5

        f.currentValue `should be` 5
        f.isNotSolved.`should be false`()

        f.clearCurrentValue()

        f.currentValue `should be` Cell.EMPTYVAL
        f.isNotSolved.`should be true`()
    }

    @Test
    fun `should throw when invoked with notify`() {
        val cell = Cell(true, 0, 0, 9)
        invoking { cell.setCurrentValue(-2, true) } `should throw` IllegalArgumentException::class
    }

    @Test
    fun testToString() {
        val cell = Cell(true, 0, 0, 21)
        cell.currentValue = 20
        cell.toString() `should be equal to` "20"
    }

    @Test
    open fun testValueTooHigh() {
        val cell = Cell(true, 2, 0, 4)
        invoking { run { cell.currentValue = 4 } } `should throw` IllegalArgumentException::class
    }
}