package de.sudoq.model.persistence.xml.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

interface ISudokuRepoProvider {

    fun getRepo(type: SudokuTypes, complexity: Complexity): IRepo<Sudoku>
    fun getRepo(sudoku: Sudoku): IRepo<Sudoku>


}