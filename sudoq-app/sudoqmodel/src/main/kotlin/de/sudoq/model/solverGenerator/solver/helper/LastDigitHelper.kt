package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationBlock
import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Utils.classifyGroup
import de.sudoq.model.sudoku.Utils.positionToRealWorld
import java.util.*

/**
 * Helper that searches for an `open Single`, a constraint in which exactly one field is not solved -&gt; can be be solved by principle of exclusion.
 * (update does not modify the sudoku passed in the constructor)
 * Difference to Naked Single: Naked single looks at candidates, LastDigitHelper does not.
 * if candidates are constantly updated, naked single catches everything LastDigitHelper catches and more:
 * e.g. if constraint A has 2 empty fields but intersects with constraint B which can exclude some candidates in A, naked single will catch that
 */
class LastDigitHelper(sudoku: SolverSudoku, complexity: Int) : SolveHelper(sudoku, complexity) {
    /**
     * Finds out if `positions` has only one empty field. if so return `position` and fill `remaining` with all other positions respectively
     * @param positions
     * @param remaining list that is filled with all solved positions if there is only one empty field left
     * if there are 2+ empty fields, remaining contains not neccessarily all solved positions
     * @return the only position in `positions` not solved by the user, if there is one
     * null otherwise
     */
    private fun onlyOneLeft(
        positions: List<Position>,
        remaining: MutableList<Position>
    ): Position? {
        assert(remaining.isEmpty())
        var candidate: Position? = null //no empty fields found
        for (p in positions) if (sudoku.getCell(p)!!.isNotSolved) {
            if (candidate == null) //found our first empty field
                candidate = p else {
                candidate = null //found 2nd empty -> break
                break
            }
        } else  //found
            remaining.add(p)
        return candidate
    }

    override fun update(buildDerivation: Boolean): Boolean {
        var foundOne = false
        var candidate: Position?
        val remaining = Vector<Position>()
        for (c in sudoku.sudokuType!!) if (c.hasUniqueBehavior()) {
            remaining.clear()
            candidate = onlyOneLeft(c.getPositions(), remaining)
            if (candidate != null) {
                /* We found an instance where only one field is empty */
                //
                val solutionField: Position = candidate //position that needs to be filled

                //make List with all values entered in this constraint
                val otherSolutions: MutableList<Int> = ArrayList()
                for (p in remaining) otherSolutions.add(sudoku.getCell(p!!)!!.currentValue)

                //make list with all possible values
                val possibleSolutions: MutableList<Int> =
                    ArrayList(sudoku.sudokuType!!.symbolIterator as AbstractList<Int>)

                /* cut away all other solutions */possibleSolutions.removeAll(otherSolutions)
                if (possibleSolutions.size == 1) {
                    /* only one solution remains -> there were no doubles */
                    foundOne = true
                    val solutionValue = possibleSolutions[0]
                    if (buildDerivation) {
                        derivation = LastDigitDerivation(c, solutionField, solutionValue)
                        val relevant = BitSet()
                        relevant.set(solutionValue) //set solution to 1
                        val irrelevant = BitSet()
                        irrelevant.xor(relevant) // create complement to relevant
                        derivation!!.addDerivationCell(
                            DerivationCell(
                                candidate,
                                relevant,
                                irrelevant
                            )
                        )
                        derivation!!.addDerivationBlock(DerivationBlock(c))
                        derivation!!.setDescription(
                            "Look at " + classifyGroup(c.getPositions()) + "! Only field " + positionToRealWorld(
                                candidate
                            ) + "is empty."
                        )
                    }
                }
            }
        }
        return foundOne
    }

    init {
        hintType = HintTypes.LastDigit
    }
}