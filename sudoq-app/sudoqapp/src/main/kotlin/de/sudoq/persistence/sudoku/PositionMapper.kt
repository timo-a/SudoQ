package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.Position

object PositionMapper {

    fun toBE(p: Position): PositionBE {
        return PositionBE(p.x, p.y)
    }

    fun fromBE(pBE: PositionBE): Position {
        return Position[pBE.x, pBE.y]
    }

}