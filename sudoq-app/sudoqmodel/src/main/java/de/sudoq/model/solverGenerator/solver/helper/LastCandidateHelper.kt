package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.LastCandidateDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * Created by timo on 20.10.16.
 */
class LastCandidateHelper(sudoku: SolverSudoku?, complexity: Int) : SolveHelper(sudoku!!, complexity) {
    override var derivation: LastCandidateDerivation? = null
    override fun update(buildDerivation: Boolean): Boolean {

        //iterate through all positions of all constraints, none twice
        val seen = Vector<Position>()
        for (c in sudoku.sudokuType!!) if (c.hasUniqueBehavior()) for (p in c) if (!seen.contains(p)) {
            seen.add(p)
            if (sudoku.getCurrentCandidates(p).cardinality() == 1) {
                val lastNote = sudoku.getCurrentCandidates(p).nextSetBit(0)
                derivation = LastCandidateDerivation(p, lastNote)
                derivation = derivation
                return true
            }
        }
        return false
    }

    init {
        hintType = HintTypes.LastCandidate
    }
}