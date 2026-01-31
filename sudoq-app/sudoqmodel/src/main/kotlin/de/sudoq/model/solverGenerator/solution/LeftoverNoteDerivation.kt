package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.Utils
import de.sudoq.model.sudoku.getGroupShape

/**
 * Created by timo on 04.10.16.
 */
class LeftoverNoteDerivation(val constraint: Constraint, val note: Int) :
    SolveDerivation(HintTypes.LeftoverNote) {

    private val actionlist: MutableList<Action> = ArrayList()

    val constraintShape: Utils.ConstraintShape
        get() = getGroupShape(constraint)

    override fun getActionList(sudoku: Sudoku): List<Action> {
        val af = NoteActionFactory()
        for (p in constraint) {
            val f = sudoku.getCell(p)
            if (f.isNoteSet(note) && f.isNotSolved) actionlist.add(af.createAction(note, f))
        }
        return actionlist
    }

    init {
        hasActionListCapability = true
    }
}