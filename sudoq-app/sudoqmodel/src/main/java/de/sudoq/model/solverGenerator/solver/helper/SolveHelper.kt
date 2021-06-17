package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes

/**
 * A Solution Technique for a Sudoku.
 * If application is successful the candidates of the cells are reduced with can help other helpers.
 *
 * @property sudoku SolverSudoku on which SolveHelper operates
 * @param complexity desired complexity for the final sudoku. Must be `>= 0`.
 */
abstract class SolveHelper protected constructor(
    @JvmField protected var sudoku: SolverSudoku,
    complexity: Int
) {

    /**
     * Difficulty score of this helper e.g.
     *  1 for `naked single` and
     *  100 for `xwing` added up they give a score for the difficulty of a sudoku
     */
    val complexityScore: Int

    init {
        require(complexity >= 0) { "complexity < 0 : $complexity" }
        complexityScore = complexity
    }

    /**
     * The derivation of the last update step if
     * it was called with buildDerivation=true and if it was successful.
     *
     * @return Derivation of the last update step, ofr null if not available
     */
    var derivation: SolveDerivation? = null
        protected set

    var hintType: HintTypes? = null //public only for debugging

    /**
     * Tries to apply this helper to the Sudoku until it succeeds for the first time or cannot be applied.
     * If the helper is successful and was called with `buildDerivation == true` the a derivation is saved.
     * It can be queried with getDerivation.
     *
     * @param buildDerivation Specifies if a derivation should be stored.
     * @return true, falls the helper could be applied(i.e. change the sudoku), false if not
     */
    abstract fun update(buildDerivation: Boolean): Boolean
}