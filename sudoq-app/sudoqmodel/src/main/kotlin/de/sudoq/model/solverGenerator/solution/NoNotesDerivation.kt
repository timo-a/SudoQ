package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.Sudoku

/**
 * In case the user doesn't specify any notes, we find fields that have none
 */
class NoNotesDerivation : SolveDerivation(HintTypes.NoNotes) {

    /* creates a list of actions in case the user want the app to execute the hints */
    override fun getActionList(sudoku: Sudoku): List<Action> {
        val actionlist: MutableList<Action> = ArrayList()
        val af = NoteActionFactory()
        val dfi = cellIterator
        while (dfi.hasNext()) {
            val df = dfi.next()
            val cs = CandidateSet()
            cs.assignWith(df.relevantCandidates)
            for (i in cs.setBits) {
                actionlist.add(af.createAction(i, sudoku.getCell(df.position)))
            }
        }
        return actionlist
    }

    init {
        hasActionListCapability = true
    }
}