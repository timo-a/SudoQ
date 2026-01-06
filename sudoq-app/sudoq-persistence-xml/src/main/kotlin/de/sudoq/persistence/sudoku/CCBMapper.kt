package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder

object CCBMapper {

    fun toBE(ccb:ComplexityConstraintBuilder) : CCBBE {
        return CCBBE(ccb.specimen.toMutableMap())
    }

    fun fromBE(ccbbe: CCBBE) : ComplexityConstraintBuilder {
        return ComplexityConstraintBuilder(ccbbe.specimen)
    }

}