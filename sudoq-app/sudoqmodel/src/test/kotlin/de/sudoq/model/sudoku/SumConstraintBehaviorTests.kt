package de.sudoq.model.sudoku

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.invoking
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test

class SumConstraintBehaviorTests {

    @Test
    fun illegalValue() {
        invoking { SumConstraintBehavior(-1) }
            .`should throw`(IllegalArgumentException::class)
    }

    @Test
    fun constraint() {

        val sudoku = mockk<Sudoku>();

        every { sudoku.getCurrentValue(Position[0, 0]) } returns 1
        every { sudoku.getCurrentValue(Position[0, 1]) } returns 2
        every { sudoku.getCurrentValue(Position[0, 2]) } returns 3
        every { sudoku.getCurrentValue(Position[1, 0]) } returns 1
        every { sudoku.getCurrentValue(Position[1, 1]) } returns 2
        every { sudoku.getCurrentValue(Position[1, 2]) } returns 3
        every { sudoku.isSolved(any()) } returns true

        val constraint = Constraint(SumConstraintBehavior(12), ConstraintType.LINE, Position[0, 0],
            Position[0, 1], Position[0, 2], Position[1, 0], Position[1, 1], Position[1, 2])
        constraint.hasUniqueBehavior().`should be false`()
        constraint.isSaturated(sudoku).`should be true`()
        every { sudoku.getCurrentValue(Position[1, 1]) } returns 3
        constraint.isSaturated(sudoku).`should be false`()
        every { sudoku.getCurrentValue(Position[1, 2]) } returns 2
        constraint.isSaturated(sudoku).`should be true`()
    }

}