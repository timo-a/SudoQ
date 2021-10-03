package de.sudoq.model.sudoku

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.*

class UniqueConstraintBehaviorTests {

    @Test
    fun testConstraint() {

        val sudoku = mockk<Sudoku>()

        fun mkCell(id: Int, currentValue: Int): Cell {
            val c = Cell(id,9)
            c.currentVal = currentValue
            return c
        }

        every { sudoku.getCell(Position[0, 0]) } returns mkCell(0, 1)
        every { sudoku.getCell(Position[0, 1]) } returns mkCell(1, 2)
        every { sudoku.getCell(Position[0, 2]) } returns mkCell(2, 3)
        every { sudoku.getCell(Position[1, 0]) } returns mkCell(3, 4)
        every { sudoku.getCell(Position[1, 1]) } returns mkCell(4, 5)
        every { sudoku.getCell(Position[1, 2]) } returns mkCell(5, 6)
        val constraint = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE)
        constraint.addPosition(Position[0, 0])
        constraint.addPosition(Position[0, 1])
        constraint.addPosition(Position[0, 2])
        constraint.addPosition(Position[1, 0])
        constraint.addPosition(Position[1, 1])
        constraint.addPosition(Position[1, 2])
        constraint.hasUniqueBehavior().`should be true`()
        constraint.isSaturated(sudoku).`should be true`()
        sudoku.getCell(Position[0, 0])!!.currentValue = 2
        constraint.isSaturated(sudoku).`should be false`()
    }
}