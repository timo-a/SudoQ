package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.LeftoverNoteDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.Constraint

/**
 * @param complexity desired complexity for the final sudoku. Must be `>= 0`.
 */
open class LeftoverNoteHelper(sudoku: SolverSudoku, complexity: Int) : SolveHelper(sudoku, complexity) {

    override fun update(buildDerivation: Boolean): Boolean {
        var foundOne = false
        for (c in sudoku.sudokuType!!) if (c.hasUniqueBehavior() && hasLeftoverNotes(c)) {
            foundOne = true
            val leftover = getLeftoverNote(c)
            deleteNote(c, leftover)
            if (buildDerivation) {
                derivation = LeftoverNoteDerivation(c, leftover)
            }
            break
        }
        return foundOne
    }

    protected fun hasLeftoverNotes(c: Constraint): Boolean {
        val filled = CandidateSet()
        val notes = CandidateSet()
        for (p in c) {
            if (sudoku.getCell(p)!!.isNotSolved)
                notes.or(sudoku.getCurrentCandidates(p)) //collect all notes
            else
                filled.set(sudoku.getCell(p)!!.currentValue) //collect all entered solution
        }
        return filled.hasCommonElement(notes)
    }

    private fun getLeftoverNote(c: Constraint): Int {
        val filled = CandidateSet()
        val notes = CandidateSet()
        for (p in c) {
            if (sudoku.getCell(p)!!.isNotSolved)
                notes.or(sudoku.getCurrentCandidates(p))
            else
                filled.set(sudoku.getCell(p)!!.currentValue)
        }
        filled.and(notes)
        return filled.nextSetBit(0)
    }

    private fun deleteNote(c: Constraint, note: Int) {
        for (p in c)
            if (sudoku.getCell(p)!!.isNotSolved && sudoku.getCell(p)!!.isNoteSet(note))
                sudoku.getCurrentCandidates(p).clear(note)
    }

    init {
        hintType = HintTypes.LeftoverNote
    }
}