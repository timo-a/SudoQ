package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.LastCandidateDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Position
import java.util.*

class LastCandidateHelper(sudoku: SolverSudoku, complexity: Int) : SolveHelper(sudoku, complexity) {

    override fun update(buildDerivation: Boolean): Boolean {

        //iterate through all positions of all constraints, none twice
        val seen = Vector<Position>()
        for (c in sudoku.sudokuType) if (c.hasUniqueBehavior()) for (p in c) if (p !in seen) {
            seen.add(p)
            if (sudoku.getCurrentCandidates(p).cardinality() == 1) {
                val lastNote = sudoku.getCurrentCandidates(p).nextSetBit(0)
                derivation = LastCandidateDerivation(p, lastNote)
                return true
            }
        }
        return false
    }

    init {
        hintType = HintTypes.LastCandidate
    }
}