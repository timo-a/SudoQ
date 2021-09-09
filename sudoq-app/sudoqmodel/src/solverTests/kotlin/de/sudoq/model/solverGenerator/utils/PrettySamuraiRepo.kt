package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.solverGenerator.utils.parser.SamuraiParser
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

class PrettySamuraiRepo : PrettySudokuRepo(SudokuTypes.samurai) {

    override fun parseSudoku(
        id: Int,
        complexity: Complexity,
        ls: List<List<String>>
    ): Sudoku {
        val parser = SamuraiParser()
        val sudoku = parser.parseSudoku(id, type, complexity, ls)
        return sudoku
    }



}