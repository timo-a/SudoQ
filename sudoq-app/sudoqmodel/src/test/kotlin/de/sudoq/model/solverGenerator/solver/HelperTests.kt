package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper
import de.sudoq.model.solverGenerator.solver.helper.LastDigitHelper
import de.sudoq.model.solverGenerator.solver.helper.LeftoverNoteHelper
import de.sudoq.model.solverGenerator.solver.helper.LockedCandandidatesHelper
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper
import de.sudoq.model.solverGenerator.solver.helper.XWingHelper
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test
import java.util.BitSet

internal class HelperTests {
    @Test
    fun xWing() {
        val sudoku = SolverSudoku(SudokuMockUps.xWing)

        val helper: SolveHelper = XWingHelper(sudoku, sudoku.complexityValue)

        val sdlist: MutableList<SolveDerivation> = ArrayList()

        sudoku.getCurrentCandidates(Position[4, 3]).get(4) `should be` true


        while (helper.update(true)) {
            sdlist.add(helper.derivation!!)
            println("print derivation:")
            println(sdlist.last())
        }
        println("sdlist " + sdlist.size)

        sdlist.`should not be empty`()

        sudoku.getCurrentCandidates(Position[4, 3]).get(4) `should be` false


        /* make sure the solution where "5" is removed from field "5,4" is among the found solutions */
    }

    /* test if xwing is really the first helper that can be applied */
    @Test
    fun xWing2() {
        val sudoku = SolverSudoku(SudokuMockUps.xWing)

        val helperList: List<SolveHelper> = listOf(
            LastDigitHelper(sudoku, 1),
            LeftoverNoteHelper(sudoku, 1),
            NakedHelper(sudoku, 1, 1),
            NakedHelper(sudoku, 2, 1),
            NakedHelper(sudoku, 3, 1),
            NakedHelper(sudoku, 4, 1),
            HiddenHelper(sudoku, 1, 1),
            HiddenHelper(sudoku, 2, 1),
            HiddenHelper(sudoku, 3, 1),
            HiddenHelper(sudoku, 4, 1),
            LockedCandandidatesHelper(sudoku, 1),
            XWingHelper(sudoku, 1)
        )

        val sdlist: MutableList<SolveDerivation> = ArrayList()

        sudoku.getCurrentCandidates(Position[4, 3]).get(4) `should be` true

        for (sh in helperList) if (sh.update(true)) {
            println("" + sh.derivation!!.type + sh.derivation)
        }

        /*while (helper.update(true)){
			sdlist.add(helper.getDerivation());
			System.out.println("print derivation:");
			System.out.println(sdlist.get(sdlist.size()-1));

		}
		System.out.println("sdlist "+sdlist.size());

		assertTrue(sdlist.size() >= 1);

		assertFalse(sudoku.getCurrentCandidates(Position.get(4,3)).get(4));
        */

        /* make sure the solution where "5" is removed from field "5,4" is among the found solutions */
    }


    @Test
    fun nakedInvalidCandidateLists() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        for (p in sudoku.positions) {
            sudoku.getCurrentCandidates(p).clear()
        }

        val nakedDouble = BitSet()
        nakedDouble.set(0, 2)
        sudoku.getCurrentCandidates(Position[0, 0]).or(nakedDouble)
        sudoku.getCurrentCandidates(Position[0, 1]).or(nakedDouble)
        sudoku.getCurrentCandidates(Position[0, 2]).or(nakedDouble)

        val helper: SubsetHelper = NakedHelper(sudoku, 2, 21)

        while (helper.update(false)); //assertion that one will be found and if none is found endless loop -> timeout

        sudoku.getCurrentCandidates(Position[0, 0]) `should be equal to` nakedDouble
        sudoku.getCurrentCandidates(Position[0, 1]) `should be equal to` nakedDouble
        sudoku.getCurrentCandidates(Position[0, 2]) `should be equal to` nakedDouble
    }

    @Test
    fun illegalArgumentLevelTooLow() {
        invoking { NakedHelper(SolverSudoku(Sudoku(TypeBuilder.get99())), 0, 20)
        } `should throw` IllegalArgumentException::class
    }

    @Test
    fun illegalArgumentComplexityTooLow() {
        invoking { NakedHelper(SolverSudoku(Sudoku(TypeBuilder.get99())), 1, -1)
        } `should throw` IllegalArgumentException::class
    }

    private fun setVal(s: Sudoku, x: Int, y: Int, `val`: Int) {
        s.getCell(Position[x - 1, y - 1])!!.currentValue = `val` - 1
    }
}
