package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.UniqueConstraintBehavior
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

internal class DerivationBlockTests {
    @Test
    fun standardTest() {
        val constr = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE)
        val block = DerivationBlock(constr)
        constr `should be equal to` block.block
    }
}
