package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.*

class LastDigitDerivation(val constraint: Constraint, private val emptyPosition: Position, private val solution: Int) : SolveDerivation(HintTypes.LastDigit) {

    val constraintShape: Utils.ConstraintShape
        get() = getGroupShape(constraint)

    override fun getActionList(sudoku: Sudoku): List<Action> {
        val af = SolveActionFactory()
        return listOf(af.createAction(solution, sudoku.getCell(emptyPosition)!!))
    }

    init {
        hasActionListCapability = true
    }
}