package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.solver.Solver
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku

object AmbiguityChecker {
    /**
     * If there are several solutions, this method holds the position of the first branch point.
     * Call only after an `isAmbiguous` call that returned `true`. Otherwise it is null.
     * @return position of the first branchingPoint, i.e. where backtracking was first applied or `null` if last call to `isAmbiguous` was unsuccessful.
     */
    private var firstBranchPosition: Position? = null
        private set

    /**
     * Determines whether there are multiple solutions to the passed sudoku.
     * @param sudoku Sudoku object to be tested for multiple solutions
     * @return true if there are several solutions false otherwise
     */
    @JvmStatic
    fun isAmbiguous(sudoku: Sudoku?): Boolean {
        val solver = Solver(sudoku!!)
        val result = solver.solveAll(buildDerivation = false, false, false)
        firstBranchPosition = if (solver.getSolverSudoku()
                .hasBranch()
        ) solver.getSolverSudoku().firstBranchPosition else null //lest old values persist
        return result && solver.severalSolutionsExist()
    }
}