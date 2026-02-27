package de.sudoq.model.sudoku

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test

class UniqueConstraintBehaviorTests {

    @Test
    fun constraint() {

        val sudoku = mockk<Sudoku>(relaxed = true)

        every { sudoku.getCurrentValue(Position[0, 0]) } returns 1
        every { sudoku.getCurrentValue(Position[0, 1]) } returns 2
        every { sudoku.getCurrentValue(Position[0, 2]) } returns 3
        every { sudoku.getCurrentValue(Position[1, 0]) } returns 4
        every { sudoku.getCurrentValue(Position[1, 1]) } returns 5
        every { sudoku.getCurrentValue(Position[1, 2]) } returns 6
        every { sudoku.isSolved(any()) } returns true

        val constraint = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE,
            Position[0, 0], Position[0, 1], Position[0, 2],
            Position[1, 0], Position[1, 1], Position[1, 2])
        constraint.hasUniqueBehavior().`should be true`()
        constraint.isSaturated(sudoku).`should be true`()

        // WHEN we change a value so the constraint is no longer satisfied
        every { sudoku.getCurrentValue(Position[0, 0]) } returns 2

        // THEN the constraint is no longer saturated
        constraint.isSaturated(sudoku).`should be false`()
    }
}
