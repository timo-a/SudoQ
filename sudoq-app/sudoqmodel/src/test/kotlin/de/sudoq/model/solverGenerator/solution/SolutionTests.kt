package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.UniqueConstraintBehavior
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.BitSet

internal class SolutionTests {
    @Test
    fun standardTest() {
        val sol = Solution()
        val act = SolveActionFactory().createAction(5, Cell(true, 3, -1, 9))
        sol.action = act
        sol.action `should be equal to` act
        sol.action = null
        sol.action `should be equal to` act

        val derivs = arrayOfNulls<SolveDerivation>(3)
        derivs[0] = SolveDerivation()
        derivs[0]!!.addDerivationBlock(
            DerivationBlock(
                Constraint(
                    UniqueConstraintBehavior(),
                    ConstraintType.LINE
                )
            )
        )
        derivs[1] = SolveDerivation()
        derivs[1]!!.addDerivationCell(DerivationCell(Position[1, 1], BitSet(), BitSet()))
        derivs[2] = SolveDerivation()
        sol.addDerivation(derivs[0]!!)
        sol.addDerivation(derivs[1]!!)
        sol.addDerivation(derivs[2]!!)

        var counter = 0
        for (sd in sol.getDerivations()) {
            Assertions.assertEquals(sd, derivs[counter])
            counter++
        }
    }
}