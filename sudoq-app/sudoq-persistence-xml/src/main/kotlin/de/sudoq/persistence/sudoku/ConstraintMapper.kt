package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.UniqueConstraintBehavior

object ConstraintMapper {

    fun fromBE(cBE: ConstraintBE) = Constraint(cBE.behavior, cBE.type, cBE.name, *cBE.positions.toTypedArray())

    fun toBE(c: Constraint): ConstraintBE {
        require(c.hasUniqueBehavior())
        //Todo can mapstruct access private types?
        //ultimately an actual behaviour should be passed
        return ConstraintBE(UniqueConstraintBehavior(),
            c.type,
            c.name,
            c.getPositions().toMutableList())
    }


}