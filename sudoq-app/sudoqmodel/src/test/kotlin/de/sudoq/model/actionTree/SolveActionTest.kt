package de.sudoq.model.actionTree

import de.sudoq.model.sudoku.Cell
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SolveActionTest {
    private val factory = SolveActionFactory()
    val cell = Cell(0, 9)
    private val solveAction = factory.createAction(5, cell)

    @Nested
    inner class Execute {
        @Test
        fun `should set value`() {

            cell.currentValue `should be equal to` (-1)

            solveAction.execute()

            cell.currentValue `should be equal to` 5
        }

        @Test
        fun `should be (but currently isn't) idempotent`() {

            cell.currentValue `should be equal to` (-1)

            val solveAction = factory.createAction(1, cell)

            solveAction.execute()
            solveAction.execute()

            cell.currentValue `should be equal to` 3//1 if idempotent it would be 1
        }
    }

    @Nested
    inner class Undo {
        @Test
        fun `should set old value again`() {

            cell.currentValue `should be equal to` (-1)

            solveAction.execute()
            cell.currentValue `should not be equal to` (-1)

            solveAction.undo()

            cell.currentValue `should be equal to` (-1)
        }

        @Test
        fun `should not (but currently does) change anything if called on unset note`() {
            cell.currentValue `should be equal to` (-1)

            invoking { solveAction.undo() } `should throw` IllegalArgumentException::class

            cell.currentValue `should be equal to` (-1)
        }

        @Test
        fun `should be (but currently isn't) idempotent`() {
            cell.currentValue `should be equal to` (-1)

            solveAction.execute()
            solveAction.undo()

            invoking { solveAction.undo() } `should throw` IllegalArgumentException::class

            cell.currentValue `should be equal to` (-1)
        }
    }

    @Test
    fun `should not equal a NoteAction`(){

        SolveAction(1, cell) `should not be equal to` NoteAction(1, NoteAction.Action.SET, cell)
    }

    @Test
    fun `should equal if same values`() {
        SolveAction(1, cell) `should be equal to`
            SolveAction(1, Cell(0,9))
    }

    @Test
    fun `should not equal if deltas differ`() {
        SolveAction(1, cell) `should not be equal to`
                SolveAction(2, cell)
    }

    @Test
    fun `should not equal if cells differ`() {
        val c1 = Cell(0,4)
        val c2 = Cell(1,4)

        c1 `should not be equal to` c2

        SolveAction(1, c1) `should not be equal to` SolveAction(1, c2)
    }

    @Test
    fun `should keep cell id`() {
        val c = Cell(1, 9)
        val a = SolveAction(1, c)

        a.cellId `should be equal to` 1
    }


}