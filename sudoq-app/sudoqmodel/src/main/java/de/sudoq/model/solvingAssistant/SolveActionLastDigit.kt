package de.sudoq.model.solvingAssistant

import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Utils.positionToRealWorld
import de.sudoq.model.sudoku.Utils.symbolToRealWorld

class SolveActionLastDigit(target: Position, solution: Int, list: List<Position>) : SolveAction() {
    init {
        val rwPosition = positionToRealWorld(target)
        val rwSolution = symbolToRealWorld(solution)
        /* are all x values the same? */
        val xPo = list[0].x
        var xSame = true
        for (p in list) if (p.x != xPo) {
            xSame = false
            break
        }
        /* are all Y values the same? */
        val yPo = list[0].y
        var ySame = true
        for (p in list) if (p.y != yPo) {
            ySame = false
            break
        }
        val structure = if (xSame) "column" else if (ySame) "row" else "block"
        message = "Field at row " + rwPosition.x + ", column " + rwPosition.y + " must be " + rwSolution + ", because all other fields in it's " + structure + " are occupied"
    }
}