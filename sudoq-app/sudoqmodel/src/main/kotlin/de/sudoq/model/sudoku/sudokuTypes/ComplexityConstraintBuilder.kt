package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import java.util.*

/**
 * Builder for Complexity constraints.
 */
class ComplexityConstraintBuilder {
    var specimen: MutableMap<Complexity?, ComplexityConstraint> = HashMap()

    constructor()
    constructor(specimen: MutableMap<Complexity?, ComplexityConstraint>) {
        this.specimen = specimen
    }

    fun getComplexityConstraint(complexity: Complexity?): ComplexityConstraint? {
        return if (complexity != null && specimen.containsKey(complexity)) {
            specimen[complexity]!!.clone() as ComplexityConstraint
        } else null
    }

}