package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.solverGenerator.utils.parser.StandardParser
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

class PrettyStandard16x16Repo: PrettySudokuRepo(SudokuTypes.standard16x16) {

    override fun parseSudoku(
        id: Int,
        complexity: Complexity,
        ls: List<List<String>>
    ): Sudoku {
        val parser = StandardParser()
        val sudoku = parser.parseSudoku(id, type, complexity, ls)
        return sudoku
    }


}