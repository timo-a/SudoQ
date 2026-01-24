package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class SubsetHelperTests : HiddenHelper(SolverSudoku(Sudoku(TypeBuilder.get99())), 4, 0) {
    @Test
    fun testGetNextSubset() {
        for (i in intArrayOf(0, 1, 2, 3, 5, 7)) constraintSet.set(i)

        for (i in intArrayOf(0, 1, 2, 3)) currentSet.set(i)

        assert(
            super.level == 4 //just to be sure
        )

        evaluateGetNext()
    }


    fun evaluateGetNext() {
        do println(currentSet)
        while (getNextSubset())
    }


    @Test
    fun hiddenUpdateOne() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        setVal(sudoku, 1, 1, 1)
        setVal(sudoku, 3, 1, 3)
        setVal(sudoku, 4, 1, 4)
        setVal(sudoku, 5, 1, 5)
        setVal(sudoku, 6, 1, 6)
        setVal(sudoku, 7, 1, 7)
        setVal(sudoku, 8, 1, 8)
        setVal(sudoku, 1, 2, 4)
        setVal(sudoku, 3, 2, 5)
        setVal(sudoku, 4, 2, 6)
        setVal(sudoku, 5, 2, 7)
        setVal(sudoku, 6, 2, 8)
        setVal(sudoku, 7, 2, 1)
        setVal(sudoku, 8, 2, 3)
        setVal(sudoku, 2, 3, 6)

        sudoku.resetCandidates()

        val helper: SubsetHelper = HiddenHelper(sudoku, 2, 22)
        helper.complexityScore `should be equal to` 22

        getCardinality(sudoku, 1, 0) `should be equal to` 2
        getCardinality(sudoku, 1, 1) `should be equal to` 2
        getCardinality(sudoku, 8, 1) `should be equal to` 2
        getCardinality(sudoku, 8, 1) `should be equal to` 2
        getCardinality(sudoku, 0, 2) `should be equal to` 4
        getCardinality(sudoku, 2, 2) `should be equal to` 4

        // Use helper 1 time to remove candidates 1 and 8 from Positions 0,2 and
        // 2,2
        helper.update(true)

        getCardinality(sudoku, 1, 0) `should be equal to` 2
        getCardinality(sudoku, 1, 1) `should be equal to` 2
        getCardinality(sudoku, 0, 2) `should be equal to` 2
        getCardinality(sudoku, 2, 2) `should be equal to` 2

        getCandidate(sudoku, 0, 2, 0) `should be` false
        getCandidate(sudoku, 0, 2, 2) `should be` false
        getCandidate(sudoku, 2, 2, 0) `should be` false
        getCandidate(sudoku, 2, 2, 2) `should be` false
    }

    @Test
    fun hiddenUpdateAll() {
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        for (i in intArrayOf(1, 3, 4, 5, 6, 7, 8)) setVal(sudoku, i, 1, i)

        setVal(sudoku, 1, 2, 4)
        setVal(sudoku, 3, 2, 5)
        setVal(sudoku, 4, 2, 6)
        setVal(sudoku, 5, 2, 7)
        setVal(sudoku, 6, 2, 8)
        setVal(sudoku, 7, 2, 1)
        setVal(sudoku, 8, 2, 3)

        setVal(sudoku, 2, 3, 6)

        sudoku.resetCandidates()

        val helper: SubsetHelper = HiddenHelper(sudoku, 2, 22)
        helper.complexityScore `should be equal to` 22

        getCardinality(sudoku, 1, 0) `should be equal to` 2
        getCardinality(sudoku, 1, 1) `should be equal to` 2
        getCardinality(sudoku, 8, 1) `should be equal to` 2
        getCardinality(sudoku, 8, 1) `should be equal to` 2
        getCardinality(sudoku, 0, 2) `should be equal to` 4
        getCardinality(sudoku, 2, 2) `should be equal to` 4

        while (helper.update(true));

        getCardinality(sudoku, 1, 0) `should be equal to` 2
        getCardinality(sudoku, 1, 1) `should be equal to` 2
        getCardinality(sudoku, 0, 2) `should be equal to` 2
        getCardinality(sudoku, 2, 2) `should be equal to` 2

        getCandidate(sudoku, 0, 2, 0) `should be` false
        getCandidate(sudoku, 0, 2, 2) `should be` false
        getCandidate(sudoku, 2, 2, 0) `should be` false
        getCandidate(sudoku, 2, 2, 2) `should be` false
    }

    companion object {
        /**
         *
         * @param s
         * @param x number of column starting with 1
         * @param y number of row starting with 1
         * @param val value starting with 1
         */
        fun setVal(s: Sudoku, x: Int, y: Int, `val`: Int) {
            s.getCell(Position[x - 1, y - 1])!!.currentValue = `val` - 1
        }

        fun getCardinality(s: SolverSudoku, x: Int, y: Int): Int {
            return s.getCurrentCandidates(Position[x, y]).cardinality()
        }

        fun getCandidate(s: SolverSudoku, x: Int, y: Int, candidate: Int): Boolean {
            return s.getCurrentCandidates(Position[x, y]).get(candidate)
        }
    }
}
