package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test
import java.util.BitSet

/**
 * Created by timo on 15.10.16.
 */
internal class NakedHelperTests {
    @Test
    fun illegalArgumentLevelTooLow() {
        invoking { NakedHelper(SolverSudoku(Sudoku(TypeBuilder.get99())), 0, 20)
        } `should throw` IllegalArgumentException::class
    }

    @Test
    fun illegalArgumentComplexityTooLow() {
        invoking {//todo wo wird es geworfen? rest nach oben ziehen
            NakedHelper(SolverSudoku(Sudoku(TypeBuilder.get99())), 1, -1)
        } `should throw` IllegalArgumentException::class
    }

    @Test
    fun NakedSingleTest() {
        val pattern = ("¹²³⁴ ¹²³⁴  ¹²³⁴ ¹ \n"
                + "1    2     3    4 \n"

                + "1    2     3    4 \n"
                + "1    2     3    4 \n")

        val s = SudokuMockUps.stringToSudoku(SudokuTypes.standard4x4, pattern)
        val ss = SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING)
        val nh = NakedHelper(ss, 1, 0)
        nh.update(true) `should be` true
    }


    private fun prepareSudoku(sudoku: SolverSudoku) {
        // 1 _ 3 4 5 6 7 8 _
        // 4 _ 5 6 7 8 1 3 _
        //

        val indx: IntArray = intArrayOf(1, 3, 4, 5, 6, 7, 8)
        val row2: IntArray = intArrayOf(4, 5, 6, 7, 8, 1, 3)

        for (x in indx.indices) {
            SubsetHelperTests.setVal(
                sudoku,
                indx[x],
                1,
                indx[x]
            ) // row 1 all filled except 2,9
            SubsetHelperTests.setVal(
                sudoku,
                indx[x],
                2,
                row2[x]
            ) // row 2 all filled except 2,9
        }

        sudoku.resetCandidates() //candidates are also recalculated
    }


    /**
     * leave 2 fields each in the upper 2 rows. -> 2 naked pairs
     * then test if they are removed from other field as candidates
     */
    @Test
    fun nakedUpdateOne() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))

        prepareSudoku(sudoku)


        val helper: SubsetHelper = NakedHelper(sudoku, 2, 21)
        helper.complexityScore `should be equal to` 21

        getNumberOfNotes(sudoku, 2, 1) `should be equal to` 2 //2 candidates each are expected as all others are set in the constraint.
        getNumberOfNotes(sudoku, 2, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2

        getNumberOfNotes(sudoku, 1, 3) `should be equal to` 5 //5 candidates are expected: 3 in row 3 + 2 from empty neighbours
        getNumberOfNotes(sudoku, 2, 3) `should be equal to` 5 //                              in case of (7,3) keep in mind that only the right block deletes candidates in 87,3)
        getNumberOfNotes(sudoku, 3, 3) `should be equal to` 5 //row 1: 1_3  456  78_
        getNumberOfNotes(sudoku, 7, 3) `should be equal to` 5 //row 2: 4_5  678  13_
        getNumberOfNotes(sudoku, 8, 3) `should be equal to` 5 //row 3: abc  ___  de_

        // Use helper 4 times: 2 for updating columns (1 and 8), 2 for updating
        // blocks (0 and 2)
        helper.update(true)
        helper.update(false)
        helper.update(false)
        helper.update(false)

        getNumberOfNotes(sudoku, 2, 1) `should be equal to` 2 //should still be 2
        getNumberOfNotes(sudoku, 2, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2

        getNumberOfNotes(sudoku, 1, 3) `should be equal to` 3 //"2" and "9" should not be possible anymore
        getNumberOfNotes(sudoku, 2, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 3, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 7, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 8, 3) `should be equal to` 3

        sudoku.getCurrentCandidates(Position[2 - 1, 3 - 1]).get(1) `should be` false
        sudoku.getCurrentCandidates(Position[2 - 1, 3 - 1]).get(8) `should be` false
    }

    @Test
    fun nakedUpdateAll() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))

        prepareSudoku(sudoku)

        val helper: SubsetHelper = NakedHelper(sudoku, 2, 21)

        getNumberOfNotes(sudoku, 2, 1) `should be equal to` 2
        getNumberOfNotes(sudoku, 2, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 2, 3) `should be equal to` 5
        getNumberOfNotes(sudoku, 1, 3) `should be equal to` 5
        getNumberOfNotes(sudoku, 3, 3) `should be equal to` 5
        getNumberOfNotes(sudoku, 7, 3) `should be equal to` 5
        getNumberOfNotes(sudoku, 8, 3) `should be equal to` 5

        val derivations: MutableList<SolveDerivation?> = ArrayList<SolveDerivation?>()
        while (helper.update(true)) derivations.add(helper.derivation)


        derivations.size `should be equal to` 4
        getNumberOfNotes(sudoku, 2, 1) `should be equal to` 2
        getNumberOfNotes(sudoku, 2, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 9, 2) `should be equal to` 2
        getNumberOfNotes(sudoku, 2, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 1, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 3, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 7, 3) `should be equal to` 3
        getNumberOfNotes(sudoku, 8, 3) `should be equal to` 3

        sudoku.getCurrentCandidates(Position[1 - 1, 2 - 1]).get(0) `should be` false
        sudoku.getCurrentCandidates(Position[1 - 1, 2 - 1]).get(2) `should be` false
    }

    @Test
    fun nakedInvalidCandidateLists() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        for (p in sudoku.positions) sudoku.getCurrentCandidates(p).clear()


        val nakedDouble = BitSet()
        nakedDouble.set(0, 2)
        sudoku.getCurrentCandidates(Position[0, 0]).or(nakedDouble)
        sudoku.getCurrentCandidates(Position[0, 1]).or(nakedDouble)
        sudoku.getCurrentCandidates(Position[0, 2]).or(nakedDouble)

        val helper: SubsetHelper = NakedHelper(sudoku, 2, 21)

        while (helper.update(false));

        sudoku.getCurrentCandidates(Position[0, 0]) `should be equal to` nakedDouble
        sudoku.getCurrentCandidates(Position[0, 1]) `should be equal to` nakedDouble
        sudoku.getCurrentCandidates(Position[0, 2]) `should be equal to` nakedDouble
    }


    private fun getNumberOfNotes(s: SolverSudoku, x: Int, y: Int): Int {
        return s.getCurrentCandidates(Position[x - 1, y - 1]).cardinality()
    }
}
