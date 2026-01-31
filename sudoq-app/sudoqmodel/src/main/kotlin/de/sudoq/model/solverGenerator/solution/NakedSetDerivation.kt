package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.CandidateSet.Companion.fromBitSet
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Sudoku
import java.util.Stack

/**
 * Created by timo on 04.10.16.
 */
class NakedSetDerivation(technique: HintTypes) : SolveDerivation(technique) {
    var constraint: Constraint? = null
    private val subsetMembers: MutableList<DerivationCell>
    private val externalCells: MutableList<DerivationCell>
    var subsetCandidates: CandidateSet? = null
        private set

    fun setSubsetCandidates(bs: CandidateSet) {
        subsetCandidates = bs.clone() as CandidateSet
    }

    fun addExternalCell(f: DerivationCell) {
        externalCells.add(f)
    }

    fun addSubsetCell(f: DerivationCell) {
        subsetMembers.add(f)
    }

    fun getSubsetMembers(): List<DerivationCell> {
        return subsetMembers
    }

    val externalCellsMembers: List<DerivationCell>
        get() = externalCells

    /* creates a list of actions in case the user want the app to execute the hints */
    override fun getActionList(sudoku: Sudoku): List<Action> {
        val actionlist: MutableList<Action> = ArrayList()
        val af = NoteActionFactory()
        for (df in externalCells) for (note in fromBitSet(df.relevantCandidates).setBits) actionlist.add(
            af.createAction(note, sudoku.getCell(df.position))
        )
        return actionlist
    }

    init {
        subsetMembers = Stack()
        externalCells = Stack()
        hasActionListCapability = true
    }
}