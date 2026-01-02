package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.Sudoku

object SudokuMapper {

    fun toBE(sudoku: Sudoku): SudokuBE {
        return SudokuBE(
            sudoku.id,
            sudoku.transformCount,
            sudoku.sudokuType,
            sudoku.complexity!!,
            sudoku.cells!!
        )
    }

    fun fromBE(sudokuBE: SudokuBE): Sudoku {
        return Sudoku(
            sudokuBE.id,
            sudokuBE.transformCount,
            sudokuBE.sudokuType!!,
            sudokuBE.complexity!!,
            sudokuBE.cells!!
        )
    }
}