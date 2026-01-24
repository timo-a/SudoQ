package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.solverGenerator.solver.helper.LeftoverNoteHelper
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

/**
 * Created by timo on 17.03.17.
 */
internal class LeftoverNoteTests {
    @Test
    fun illegalArgumentComplexity() {
        invoking { LeftoverNoteHelper(SolverSudoku(Sudoku(TypeBuilder.get99())), -1)
        } `should throw` IllegalArgumentException::class
    }

    @Test
    fun LeftoverTest() {
        //GIVEN
        val pattern = ("¹²³⁴ .     .   . \n"
                + ".    .     .   . \n"

                + ".    .     .   . \n"
                + "1    .     .   . \n")

        val s = SudokuMockUps.stringToSudoku(SudokuTypes.standard4x4, pattern)
        val ss = SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING)
        val loh = LeftoverNoteHelper(ss, 0)

        //WHEN
        val result = loh.update(true)

        //THEN
        result `should be` true
        loh.hintType `should be equal to` HintTypes.LeftoverNote
        loh.derivation!!.getActionList(ss) shouldHaveSize 1
    }
}
