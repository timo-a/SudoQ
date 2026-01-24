package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.solverGenerator.solver.helper.Backtracking
import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo2
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

internal class BacktrackingTests {
    private val sudokuRepo = PrettySudokuRepo2(sudokuTypeRepo)

    @Test
    fun initialisation() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        val back = Backtracking(sudoku, 10)
        back.complexityScore `should be equal to` 10
    }

    @Test
    fun iInitialisationWithInvalidComplexity() {
        invoking {
            Backtracking(SolverSudoku(Sudoku(TypeBuilder.get99())), -2)
        } `should throw` IllegalArgumentException::class
    }

    @Test
    fun updateOne() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        val back = Backtracking(sudoku, 10)

        sudoku.getCurrentCandidates(Position[1, 3]).clear(2, 8)
        back.update(true)
        val deriv = back.derivation
        sudoku.branchLevel `should be equal to` 1
        deriv!!.cellIterator.next().position `should be equal to` Position[1, 3]
    }

    @Test
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    fun alreadySolved() { //todo can we do this with 4x4 (too) so it is simpler?
        val sudokuPath = Paths.get("sudokus/x_easy_1.pretty")
        val sudoku = sudokuRepo.read(sudokuPath, Complexity.easy)
        val solver = Solver(sudoku)
        val solverSudoku =
            solver.getSolverSudoku() //todo once we have defined the output of the solver we can omit solving a soduku first
        solver.solveAll(false, false, false) `should be` true
        //TODO see if sudoku can be mocked better, use smaller sudoku, understand why no return false
        val back = Backtracking(solverSudoku, 10)
        solverSudoku.updateCandidates()
        // there should be no fields to solve any more
        val result = back.update(false)
        result `should be` false
    }

    companion object {
        private val sudokuTypeRepo = SudokuTypeRepo4Tests()
    }
}
