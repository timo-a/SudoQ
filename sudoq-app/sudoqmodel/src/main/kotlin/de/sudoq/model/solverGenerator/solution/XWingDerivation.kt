package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import java.util.*

class XWingDerivation : SolveDerivation(HintTypes.XWing) {

    private val lockedConstraints: MutableList<Constraint> = Stack()
    private val reducibleConstraints: MutableList<Constraint> = Stack()

    var note = 0

    fun setLockedConstraints(c1: Constraint, c2: Constraint) {
        lockedConstraints.add(c1)
        lockedConstraints.add(c2)
    }

    fun setReducibleConstraints(c1: Constraint, c2: Constraint) {
        reducibleConstraints.add(c1)
        reducibleConstraints.add(c2)
    }

    fun getReducibleConstraints(): List<Constraint> {
        return reducibleConstraints
    }

    fun getLockedConstraints(): List<Constraint> {
        return lockedConstraints
    }

    init {
        setDescription("XWing")
    }
}