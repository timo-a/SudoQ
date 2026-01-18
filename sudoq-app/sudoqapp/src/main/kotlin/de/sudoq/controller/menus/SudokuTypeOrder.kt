package de.sudoq.controller.menus

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

object SudokuTypeOrder {

    //for a sudoku type, get its position in the drop down menu
    fun getKey(s: SudokuTypes): Int {
        return ordinals[s.ordinal]
    }

    //type enum index -> position in ui
    private var ordinals: IntArray

    init {
        // position in ui -> enum
        val order = arrayOf(
            SudokuTypes.standard9x9,
            SudokuTypes.standard4x4,
            SudokuTypes.standard6x6,
            SudokuTypes.standard16x16,
            SudokuTypes.samurai,
            SudokuTypes.Xsudoku,
            SudokuTypes.HyperSudoku,
            SudokuTypes.squigglya,
            SudokuTypes.squigglyb,
            SudokuTypes.stairstep
        )
        //enum id -> ui pos
        ordinals = IntArray(SudokuTypes.entries.size)
        for ((uiPos, enum) in order.withIndex()) {
            ordinals[enum.ordinal] = uiPos
        }
    }
}