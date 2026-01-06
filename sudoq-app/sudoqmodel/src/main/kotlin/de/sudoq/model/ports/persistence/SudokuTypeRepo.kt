package de.sudoq.model.ports.persistence

import de.sudoq.model.sudoku.sudokuTypes.SudokuType

interface SudokuTypeRepo {
    fun read(id: Int): SudokuType
}