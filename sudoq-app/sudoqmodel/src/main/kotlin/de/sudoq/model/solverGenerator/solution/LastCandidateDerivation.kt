package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.IntermediateSudoku
import de.sudoq.model.sudoku.Position

class LastCandidateDerivation(val position: Position, private val remainingNote: Int) :
    SolveDerivation(HintTypes.LastCandidate) {

    private val actionlist: MutableList<Action> = ArrayList()

    init {
        hasActionListCapability = true
    }

    override fun getActionList(sudoku: IntermediateSudoku): List<Action> {
        val af = SolveActionFactory()
        actionlist.add(af.createAction(remainingNote, sudoku.getCell(position)))
        return actionlist
    }

}