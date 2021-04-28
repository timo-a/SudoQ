package de.sudoq.model.solvingAssistant

import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import java.util.*

//TODO make an optional<SolveAction>
object LastDigit {
    fun findOne(sudoku: Sudoku): SolveAction? {
        /* for every constraint */
        for (c in sudoku.sudokuType!!) if (c.hasUniqueBehavior()) {
            val v = Vector<Position>()
            for (p in c.getPositions()) if (sudoku.getCell(p)!!.isNotSolved) v.add(p)
            if (v.size == 1) {
                /* We found an instance where only one field is empty */
                //
                val solutionField = v[0] //position that needs to be filled
                //make List with all values entered in this constraint
                val otherSolutions: MutableList<Int> = ArrayList()
                for (p in c.getPositions()) if (p !== solutionField) otherSolutions.add(sudoku.getCell(p)!!.currentValue)
                //make list with all possible values
                val possibleSolutions: MutableList<Int> = ArrayList()
                for (i in sudoku.sudokuType!!.symbolIterator) possibleSolutions.add(i)
                /* cut away all other solutions */possibleSolutions.removeAll(otherSolutions)
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