package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.CandidateSet.Companion.fromBitSet
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Sudoku
import java.util.*

class HiddenSetDerivation(technique: HintTypes) : SolveDerivation(technique) {

    var constraint: Constraint? = null //todo is it ever set?

    private val subsetMembers: MutableList<DerivationCell> = Stack()

    fun getSubsetMembers(): List<DerivationCell> {
        return subsetMembers
    }


    var subsetCandidates: CandidateSet? = null
        private set

    fun setSubsetCandidates(bs: BitSet) {
        subsetCandidates = bs.clone() as CandidateSet
    }


    init {
        hasActionListCapability = true
    }


    fun addSubsetCell(f: DerivationCell) {
        subsetMembers.add(f)
    }


    /* creates a list of actions in case the user want the app to execute the hints */
    override fun getActionList(sudoku: Sudoku): List<Action> {
        val actionlist: MutableList<Action> = ArrayList()
        val af = NoteActionFactory()
        val it = cellIterator
        while (it.hasNext()) {
            val df = it.next()
            for (note in fromBitSet(df.irrelevantCandidates).setBits)
                actionlist.add(af.createAction(note, sudoku.getCell(df.position)!!))
        }
        return actionlist
    }

}