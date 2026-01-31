package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SumConstraintBehavior
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.amshove.kluent.`should not be equal to`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SolverSudokuTests {
    lateinit var sudoku: SolverSudoku

    @BeforeEach
    fun before() {
        sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
    }

    @Test
    fun killBranchWhenThereAreNone() {
        //GIVEN
        val sudoku = SolverSudoku(Sudoku(TypeBuilder.get99()))
        //WHEN
        invoking { sudoku.killCurrentBranch() } `should throw` IllegalArgumentException::class
    }

    @Test
    fun killBranchShouldRemoveTheGuess() {
        //GIVEN
        val s = Sudoku(TypeBuilder.get99())
        val p = Position[5, 7]
        s.getCell(p).toggleNote(2)
        s.getCell(p).toggleNote(3)
        val sudoku = SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING)

        //WHEN
        sudoku.startNewBranch(p, 2)
        sudoku.killCurrentBranch()

        //THEN
        sudoku.getCurrentCandidates(p).isSet(3) `should be` true //3 should remain
        sudoku.getCurrentCandidates(p).isSet(2) `should be` false //2 has been proven wrong, should be removed
        sudoku.getCurrentCandidates(p).cardinality() `should be` 1 //no other candidates
    }

    @Test
    fun killBranchShouldRemoveTheGuess2() {
        //GIVEN
        val s = Sudoku(TypeBuilder.get99())
        val p1 = Position[5, 7]
        s.getCell(p1).toggleNote(2)
        s.getCell(p1).toggleNote(3)
        val p2 = Position[8, 4]
        s.getCell(p2).toggleNote(4)
        s.getCell(p2).toggleNote(5)
        val sudoku = SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING)

        //WHEN
        sudoku.startNewBranch(p1, 2)
        sudoku.startNewBranch(p2, 4)
        sudoku.killCurrentBranch()

        //THEN
        sudoku.getCurrentCandidates(p2).isSet(5) `should be` true //5 should remain
        sudoku.getCurrentCandidates(p2).cardinality() `should be` 1 //no other candidates

        //WHEN 2 (I'm too lazy right now, should probably be two tests...)
        sudoku.killCurrentBranch()

        //THEN 2
        sudoku.getCurrentCandidates(p1).isSet(3) `should be` true //3 should remain
        sudoku.getCurrentCandidates(p1).cardinality() `should be` 1 //no other candidates
    }

    @Test
    fun standardSudoku() {
        val firstPos = Position[5, 7]
        sudoku.getCurrentCandidates(firstPos).clear()
        sudoku.getCurrentCandidates(firstPos).set(2)
        sudoku.getCurrentCandidates(firstPos).set(3)

        val secondPos = Position[8, 4]
        sudoku.getCurrentCandidates(secondPos).clear()
        sudoku.getCurrentCandidates(secondPos).set(0)

        val thirdPos = Position[3, 2]


        // Verify test initialization: sudoku should have no branch
        sudoku.hasBranch() `should be` false

        sudoku.getCurrentCandidates(firstPos).cardinality() `should be equal to` 2
        sudoku.startNewBranch(firstPos, 2)
        //new branch only one possible candidate
        sudoku.getCurrentCandidates(firstPos).cardinality() `should be equal to` 1
        // Other candidate 4 should not be available on new branch
        sudoku.getCurrentCandidates(firstPos).get(4) `should be` false
        sudoku.getCurrentCandidates(firstPos).get(2) `should be` true // "Only 2"

        val candidatesOnTopBranch = sudoku.lastBranch.candidates[firstPos]
        candidatesOnTopBranch `should not be` sudoku.getCurrentCandidates(firstPos)
        candidatesOnTopBranch!!.isSet(2) `should be` true
        candidatesOnTopBranch.isSet(3) `should be` true


        sudoku.startNewBranch(secondPos, 0)
        sudoku.branchLevel `should be equal to` 2
        sudoku.killCurrentBranch()
        sudoku.branchLevel `should be equal to` 1
        sudoku.getCurrentCandidates(secondPos).cardinality() `should be equal to` 0

        sudoku.killCurrentBranch()
        sudoku.hasBranch() `should be` false
        // after killing the branch the stashed away possibility should be available again
        sudoku.getCurrentCandidates(firstPos).get(3) `should be` true
        // after killing the branch the wrong guess should no longer be a candidate
        sudoku.getCurrentCandidates(firstPos).get(2) `should be` false

        sudoku.startNewBranch(thirdPos, 0)
        sudoku.getCurrentCandidates(firstPos).cardinality() `should be equal to` 1
        sudoku.resetCandidates()
    }

    // TODO Tests for a sudoku with at least one constraint behavior that is not the unique one

    @Test
    fun invalidArguments() {
        sudoku.updateCandidates(null, 1)
        sudoku.setSolution(null, 1)
        sudoku.setSolution(Position[1, 0], 7)
        sudoku.setSolution(Position[1, 0], -1)
    }

    @Test
    fun constraintSaturationChecks() {
        sudoku.setSolution(Position[0, 0], 1)
        sudoku.setSolution(Position[0, 1], 1)
    }

    @Test
    fun resetCandidatesStack() {
        sudoku.startNewBranch(Position[1, 1], 1)
        sudoku.resetCandidates()
        sudoku.hasBranch() `should be` false
        for (p in sudoku.positions) {
            if (sudoku.getCell(p).currentValue != -1) {
                sudoku.getCurrentCandidates(p).cardinality() `should be equal to` 0
            } else {
                var currentCandidate = -1
                for (i in 0..<sudoku.getCurrentCandidates(p).cardinality()) {
                    currentCandidate = sudoku.getCurrentCandidates(p).nextSetBit(currentCandidate + 1)
                    for (c in sudoku.constraints[p]!!) for (pos in c) {
                        sudoku.getCell(pos).currentValue `should not be equal to` currentCandidate
                    }
                }
            }
        }
    }

    @Test
    fun branchNonExistingPosition() {
        invoking { sudoku.startNewBranch(Position[10, 4], 1)
        } `should throw` IllegalArgumentException::class
    }

    @Test
    fun addNegaitveComplexity() {
        sudoku.addComplexityValue(-5, true)
    }

    @Test
    fun nonUniqueConstraints() {
        // Create new type with a sum constraint
        val c = Constraint(
            SumConstraintBehavior(10), ConstraintType.LINE,
            Position[0, 0], Position[1, 0], Position[2, 0],
            Position[3, 0]
        )

        val type = SudokuType(
            SudokuTypes.standard4x4,
            4,
            0f,
            Position[4, 4],
            Position[1, 1],
            listOf(c),
            ArrayList(),
            ArrayList(),
            ComplexityConstraintBuilder(HashMap())
        )

        val sudoku = SolverSudoku(Sudoku(type))
        sudoku.sudokuType.numberOfSymbols `should be equal to` 4
        sudoku.getCurrentCandidates(Position[0, 0]).cardinality() `should be equal to` 4

        sudoku.setSolution(Position[0, 0], 3)
        sudoku.setSolution(Position[1, 0], 2)
        sudoku.startNewBranch(Position[2, 0], 3)
        sudoku.getCell(Position[2, 0]).currentValue = 3
        sudoku.updateCandidates()
        sudoku.getCurrentCandidates(Position[3, 0]).cardinality() `should be equal to` 1
        sudoku.getCurrentCandidates(Position[3, 0]).nextSetBit(0) `should be equal to` 2
        sudoku.killCurrentBranch()
        sudoku.setSolution(Position[2, 0], 3)
        sudoku.getCurrentCandidates(Position[3, 0]).cardinality() `should be equal to` 1
        sudoku.getCurrentCandidates(Position[3, 0]).nextSetBit(0) `should be equal to` 2
        sudoku.setSolution(Position[3, 0], 2)
        sudoku.updateCandidates()
        sudoku.sudokuType.checkSudoku(sudoku) `should be` true
    }
}
