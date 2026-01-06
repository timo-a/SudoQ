package de.sudoq.persistence.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.sudoku.ISudokuRepoProvider
import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.io.File

class SudokuRepoProvider(private val outerSudokusDir: File,
                         private val sudokuTypeRepo: ReadRepo<SudokuType>
)
    : ISudokuRepoProvider {

    override fun getRepo(type: SudokuTypes, complexity: Complexity): IRepo<Sudoku> {
        return SudokuRepo(outerSudokusDir, type, complexity, sudokuTypeRepo)
    }

    override fun getRepo(sudoku: Sudoku): IRepo<Sudoku> {
        return SudokuRepo(outerSudokusDir, sudoku, sudokuTypeRepo)
    }
}