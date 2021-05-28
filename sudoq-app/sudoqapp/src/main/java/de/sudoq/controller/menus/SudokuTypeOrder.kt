package de.sudoq.controller.menus

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

object SudokuTypeOrder {
    fun getKey(s: SudokuTypes?): Int {
        return ordinals[s!!.ordinal]
    }

    private var ordinals: IntArray

    init {
        val order = arrayOf(SudokuTypes.standard9x9,
                SudokuTypes.standard4x4,
                SudokuTypes.standard6x6,
                SudokuTypes.standard16x16,
                SudokuTypes.samurai,
                SudokuTypes.Xsudoku,
                SudokuTypes.HyperSudoku,
                SudokuTypes.squigglya,
                SudokuTypes.squigglyb,
                SudokuTypes.stairstep)
        ordinals = IntArray(SudokuTypes.values().size)
        for (i in de.sudoq.controller.menus.order.indices) {
            ordinals[de.sudoq.controller.menus.order.get(i).ordinal] = i
        }
    }
}