package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import java.util.*

/**
 * A Solution step for the [Sudoku]. Comprises
 * - a concrete [Action] that if applied to a [Sudoku] solves a [Cell]
 * - [Derivation]s that lead to the solution (see [SolveDerivation] und [Action]).
 */
class Solution {

    /**
     * The [Action] that solves the [Cell] that belongs to the [Solution]
     * or null if the Action doesn't solve a [Cell].
     */
    var action: Action? = null
        set(action) {
            if (action != null) field = action
        }

    /**
     * A list of [SolveDerivation]s that derive the [Action].
     */
    private val derivations: MutableList<SolveDerivation> = ArrayList()


    /**
     * Adds a [SolveDerivation]
     *
     * @param derivation A SolveDerivation to add
     */
    fun addDerivation(derivation: SolveDerivation) {
        derivations.add(derivation)
    }

    /**
     * Iterator over the SolveDerivations.
     *
     * @return An Iterator over the SolveDerivations
     */
    val derivationIterator: Iterator<SolveDerivation>
        get() = derivations.iterator()

    fun getDerivations(): List<SolveDerivation> {
        return derivations
    }
}