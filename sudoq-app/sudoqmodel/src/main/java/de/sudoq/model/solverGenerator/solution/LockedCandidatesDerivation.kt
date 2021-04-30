package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import java.util.*

/**
 * Created by timo on 04.10.16.
 */
class LockedCandidatesDerivation : SolveDerivation(HintTypes.LockedCandidatesExternal) {
    var lockedConstraint: Constraint? = null
    var reducibleConstraint: Constraint? = null
    private var removableNotes: BitSet? = null
    private var note = 0
    fun setRemovableNotes(i: BitSet?) {
        removableNotes = i
        note = removableNotes!!.nextSetBit(0)
    }

    fun getNote(): Int {
        return note + 1
    }
}