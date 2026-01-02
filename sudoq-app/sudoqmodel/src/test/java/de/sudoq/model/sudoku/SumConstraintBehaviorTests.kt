package de.sudoq.model.sudoku

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.*
import java.lang.IllegalArgumentException

class SumConstraintBehaviorTests {

    @Test
    fun testIllegalValue() {
        invoking { SumConstraintBehavior(-1) }
            .`should throw`(IllegalArgumentException::class)
    }

    @Test
    fun testConstraint() {

        val sudoku = mockk<Sudoku>(relaxed = true);

        fun mkCell(id: Int, currentValue: Int): Cell {
            val c = Cell(id,9)
            c.currentVal = currentValue
            return c
        }

        every { sudoku.getCell(Position[0, 0]) } returns mkCell(0,1)
        every { sudoku.getCell(Position[0, 1]) } returns mkCell(1,2)
        every { sudoku.getCell(Position[0, 2]) } returns mkCell(2,3)
        every { sudoku.getCell(Position[1, 0]) } returns mkCell(3,1)
        every { sudoku.getCell(Position[1, 1]) } returns mkCell(4,2)
        every { sudoku.getCell(Position[1, 2]) } returns mkCell(5,3)

        val constraint = Constraint(SumConstraintBehavior(12), ConstraintType.LINE)

        constraint.addPosition(Position[0, 0])
        constraint.addPosition(Position[0, 1])
        constraint.addPosition(Position[0, 2])
        constraint.addPosition(Position[1, 0])
        constraint.addPosition(Position[1, 1])
        constraint.addPosition(Position[1, 2])

        constraint.hasUniqueBehavior().`should be false`()
        constraint.isSaturated(sudoku).`should be true`()
        sudoku.getCell(Position[1, 1])!!.currentValue = 3
        constraint.isSaturated(sudoku).`should be false`()
        sudoku.getCell(Position[1, 2])!!.currentValue = 2
        constraint.isSaturated(sudoku).`should be true`()
    }

}