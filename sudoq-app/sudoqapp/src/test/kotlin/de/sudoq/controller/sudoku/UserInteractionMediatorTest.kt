package de.sudoq.controller.sudoku

import android.gesture.GestureStore
import de.sudoq.controller.sudoku.board.CellViewStates
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.game.Game
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.view.GestureInputOverlay
import de.sudoq.view.SudokuCellView
import de.sudoq.view.SudokuLayout
import de.sudoq.view.VirtualKeyboardLayout
import io.mockk.*
import org.junit.jupiter.api.Test

class UserInteractionMediatorTest {
    private val sudoku = loadSudoku(SudokuTypes.standard4x4)
    private val game = Game(1, sudoku)
    private val keyboard = mockk<VirtualKeyboardLayout>(relaxed = true)
    private val board = mockk<SudokuLayout>(relaxed = true)
    private val selectedView = mockk<SudokuCellView>(relaxed = true)
    private val selectedCell = sudoku.first { it.solution == 0 }
    private val mediator: UserInteractionMediator

    init {
        every { board.currentCellView } returns selectedView
        every { selectedView.cell } returns selectedCell
        mediator = UserInteractionMediator(
            keyboard, board, game, mockk<GestureInputOverlay>(relaxed = true),
            mockk<GestureStore>(), mockk<ProfileManager>()
        )
        val actions = mockk<ActionListener>(relaxed = true)
        every { actions.onAddEntry(any(), any()) } answers {
            game.addAndExecute(SolveActionFactory().createAction(secondArg(), firstArg()))
        }
        every { actions.onDeleteEntry(any()) } answers {
            game.addAndExecute(SolveActionFactory().createAction(Cell.EMPTYVAL, firstArg()))
        }
        mediator.registerListener(actions)
        sudoku.filter { it.solution == 0 && it != selectedCell }.forEach { it.currentValue = 0 }
    }

    @Test
    fun `completion follows entry deletion undo and redo while preserving selection`() {
        mediator.updateKeyboard()
        verify { keyboard.setSymbolCompleted(0, false) }

        clearMocks(keyboard, answers = false)
        mediator.onInput(0)
        verify {
            keyboard.setSymbolCompleted(0, true)
            keyboard.markCell(0, CellViewStates.SELECTED_INPUT_BORDER)
        }

        clearMocks(keyboard, answers = false)
        game.undo()
        mediator.updateKeyboard() // The undo control refreshes the keyboard after changing the game.
        verify { keyboard.setSymbolCompleted(0, false) }

        clearMocks(keyboard, answers = false)
        game.redo()
        mediator.updateKeyboard()
        verify { keyboard.setSymbolCompleted(0, true) }

        clearMocks(keyboard, answers = false)
        mediator.onInput(0) // Tapping the selected completed symbol must still delete it.
        verify { keyboard.setSymbolCompleted(0, false) }
        verify(exactly = 0) { keyboard.disableButton(any()) }
    }

    @Test
    fun `changes from other cells refresh completion without a selected cell`() {
        selectedCell.currentValue = 0
        every { board.currentCellView } returns null
        mediator.onCellChanged(selectedView)
        verify {
            keyboard.setSymbolCompleted(0, true)
            keyboard.markCell(0, CellViewStates.DEFAULT_BORDER)
        }

        clearMocks(keyboard, answers = false)
        sudoku.first { it.solution == 0 && it != selectedCell }.clearCurrentValue()
        mediator.onCellChanged(mockk())
        verify { keyboard.setSymbolCompleted(0, false) }
    }

    @Test
    fun `note selection and candidate restrictions remain independent of completion`() {
        selectedCell.currentValue = 0
        selectedCell.toggleNote(0)
        mediator.onCellSelected(selectedView, CellInteractionListener.SelectEvent.Long)
        verify {
            keyboard.setSymbolCompleted(0, true)
            keyboard.markCell(0, CellViewStates.SELECTED_NOTE_BORDER)
        }
        verify(exactly = 0) { keyboard.disableButton(any()) }
    }
}
