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

        fun mkCell(id: Int, currentValue: Int): Cell {
            val c = Cell(id,9)
            c.currentValue = currentValue
            return c
        }

        val cell00 = mkCell(0, 1)
        val cell01 = mkCell(1, 2)
        val cell02 = mkCell(2, 3)
        val cell10 = mkCell(3, 4)
        val cell11 = mkCell(4, 5)
        val cell12 = mkCell(5, 6)

        every { sudoku.getCell(Position[0, 0]) } returns cell00
        every { sudoku.getCell(Position[0, 1]) } returns cell01
        every { sudoku.getCell(Position[0, 2]) } returns cell02
        every { sudoku.getCell(Position[1, 0]) } returns cell10
        every { sudoku.getCell(Position[1, 1]) } returns cell11
        every { sudoku.getCell(Position[1, 2]) } returns cell12

        val constraint = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE,
            Position[0, 0], Position[0, 1], Position[0, 2],
            Position[1, 0], Position[1, 1], Position[1, 2])
        constraint.hasUniqueBehavior().`should be true`()
        constraint.isSaturated(sudoku).`should be true`()

        // WHEN we change a value so the constraint is no longer satisfied
        cell00.currentValue = 2

        // THEN the constraint is no longer saturated
        constraint.isSaturated(sudoku).`should be false`()
    }
}
