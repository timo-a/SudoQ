package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import java.util.*

/**
 * Builder for Complexity constraints.
 * todo vielleicht war das mal ein builder, aber jetzt wirkt es nutzlos, prüfen ob er entfernt werden kann
 */
class ComplexityConstraintBuilder(//todo this is not a "builder", rather a "holder", add assertions
    val specimen: Map<Complexity, ComplexityConstraint>
) {

    fun getComplexityConstraint(complexity: Complexity): ComplexityConstraint? {
        return if (specimen.containsKey(complexity)) {
            specimen[complexity]!!.clone() as ComplexityConstraint
        } else null
    }

}