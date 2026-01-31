package de.sudoq.model.solvingAssistant

import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import java.util.Vector

//TODO make an optional<SolveAction>
object LastDigit {
    fun findOne(sudoku: Sudoku): SolveAction? {
        /* for every constraint */
        for (c in sudoku.sudokuType) if (c.hasUniqueBehavior()) {
            val v = Vector<Position>(c.getPositions().filter { sudoku.getCell(it).isNotSolved })
            if (v.size == 1) {
                /* We found an instance where only one field is empty */
                //
                val solutionField = v.first() //position that needs to be filled
                //make List with all values entered in this constraint
                val otherSolutions: List<Int> = c.getPositions()
                    .filter { it !== solutionField }
                    .map { sudoku.getCell(it).currentValue }

                //make list with all possible values
                val possibleSolutions: MutableList<Int> = sudoku.sudokuType.symbolIterator.toMutableList()
                /* cut away all other solutions */
                possibleSolutions.removeAll(otherSolutions)
                if (possibleSolutions.size == 1) {
                    /* only one solution remains -> there were no doubles */
                    val solutionValue = possibleSolutions[0]
                    return SolveActionLastDigit(solutionField, solutionValue, c.getPositions())
                }
            }
        }
        return null
    }
}