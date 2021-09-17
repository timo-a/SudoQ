package de.sudoq.model.actionTree

import de.sudoq.model.sudoku.Cell
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NoteActionTest {
    val cell = Cell(0, 2)
    private val noteAction = NoteAction(5, NoteAction.Action.SET, cell)

    @Nested
    inner class Execute {
        @Test
        fun `should set value`() {
            Assertions.assertFalse(cell.isNoteSet(5))
            noteAction.execute()
            Assertions.assertTrue(cell.isNoteSet(5))
        }

        @Test
        fun `should not change note if called again`() {
            Assertions.assertFalse(cell.isNoteSet(5))
            noteAction.execute()
            Assertions.assertTrue(cell.isNoteSet(5))
            noteAction.execute()
            Assertions.assertTrue(cell.isNoteSet(5))
        }
    }

    @Nested
    inner class Undo {
        @Test
        fun `should unset value`() {
            Assertions.assertFalse(cell.isNoteSet(5))
            noteAction.execute()
            noteAction.undo()
            Assertions.assertFalse(cell.isNoteSet(5))
        }

        @Test
        fun `should not change anything if called on unset note`() {
            Assertions.assertFalse(cell.isNoteSet(5))
            noteAction.undo()
            Assertions.assertFalse(cell.isNoteSet(5))
        }
    }
//todo test inverse

}