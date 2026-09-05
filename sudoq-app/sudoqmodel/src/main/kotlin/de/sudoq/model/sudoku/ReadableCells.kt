package de.sudoq.model.sudoku

interface ReadableCells {

    fun getCurrentValue(pos: Position): Int

    fun isSolved(pos: Position): Boolean
}