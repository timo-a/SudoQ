package de.sudoq.model.solvingAssistant

import de.sudoq.model.solverGenerator.solution.BacktrackingDerivation
import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solverGenerator.solver.helper.*
import de.sudoq.model.sudoku.Sudoku
import java.util.*

object SolvingAssistant {
    @JvmStatic
    fun giveAHint(sudoku: Sudoku): SolveDerivation {
        val s = SolverSudoku(sudoku, SolverSudoku.Initialization.USE_EXISTING)
        val helpers: Queue<SolveHelper> = LinkedList()
        helpers.add(LastDigitHelper(s, 0))
        helpers.add(LastCandidateHelper(s, 0))
        helpers.add(LeftoverNoteHelper(s, 0))
        helpers.add(NakedHelper(s, 1, 0))
        helpers.add(NakedHelper(s, 2, 0))
        helpers.add(NakedHelper(s, 3, 0))
        helpers.add(NakedHelper(s, 4, 0))
        helpers.add(NakedHelper(s, 5, 0))
        helpers.add(HiddenHelper(s, 1, 0))
        helpers.add(HiddenHelper(s, 2, 0))
        helpers.add(HiddenHelper(s, 3, 0))
        helpers.add(HiddenHelper(s, 4, 0))
        helpers.add(HiddenHelper(s, 5, 0))
        helpers.add(LockedCandandidatesHelper(s, 0))
        helpers.add(XWingHelper(s, 0))
        helpers.add(NoNotesHelper(s, 0))
        for (sh in helpers) if (sh.update(true)) {
            //System.out.println("SolvingAssistant finds: " + sh.getClass());
            return sh.derivation
        }
        return BacktrackingDerivation()
    }
}