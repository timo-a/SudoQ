package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.complexity.ComplexityConstraint

object ComplexityConstraintMapper {

    fun toBE(complexityConstraint: ComplexityConstraint) : ComplexityConstraintBE {
        return ComplexityConstraintBE(
            complexityConstraint.complexity,
            complexityConstraint.averageCells,
            complexityConstraint.minComplexityIdentifier,
            complexityConstraint.minComplexityIdentifier,
            complexityConstraint.numberOfAllowedHelpers)
    }

    fun fromBE(complexityConstraintBE: ComplexityConstraintBE) : ComplexityConstraint {
        return ComplexityConstraint(
            complexityConstraintBE.complexity,
            complexityConstraintBE.averageCells,
            complexityConstraintBE.minComplexityIdentifier,
            complexityConstraintBE.minComplexityIdentifier,
            complexityConstraintBE.numberOfAllowedHelpers
        )
    }
}