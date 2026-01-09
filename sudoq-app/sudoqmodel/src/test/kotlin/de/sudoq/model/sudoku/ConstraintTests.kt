package de.sudoq.model.sudoku

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should be false`
import org.junit.jupiter.api.Test

class ConstraintTests {

    @Test
    fun initialisation() {
        val uc = UniqueConstraintBehavior()
        val c1 = Constraint(uc, ConstraintType.LINE)
        c1.hasUniqueBehavior().`should be true`()
        c1.toString().`should be equal to`("Constraint with $uc")
        c1.type.`should be equal to`(ConstraintType.LINE)
        val c2 = Constraint(SumConstraintBehavior(9), ConstraintType.LINE)
        c2.hasUniqueBehavior().`should be false`()
    }

    @Test
    fun addPositionAndIterate() {
        val p1 = Position[1, 1]
        val p2 = Position[2, 2]
        val p3 = Position[42, 42]
        val c = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE, p1,p2,p3,p1)
        c.size.`should be equal to`(3)
        c.includes(p1).`should be true`()
        c.includes(p2).`should be true`()
        c.includes(p3).`should be true`()
        val i = c.iterator()
        var p = i.next()
        p.`should be equal to`(p1)
        p = i.next()
        p.`should be equal to`(p2)
        p = i.next()
        p.`should be equal to`(p3)
    }

    @Test
    fun saturation() {
        val posA = Position[0, 0]
        val posB = Position[0, 1]
        val posC = Position[0, 2]

        val sudo = mockk<Sudoku>()

        fun mkCell(id: Int, currentValue: Int): Cell {
            val c = Cell(id,9)
            c.currentVal = currentValue
            return c
        }

        every { sudo.getCell(posA) } returns mkCell(0,0)
        every { sudo.getCell(posB) } returns mkCell(1,4)
        every { sudo.getCell(posC) } returns mkCell(2,4)

        fun mkConstraint(vararg p: Position) = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE, *p)

        mkConstraint()
            .isSaturated(sudo).`should be true`()
        mkConstraint(posA)
            .isSaturated(sudo).`should be true`()
        mkConstraint(posA, posB)
            .isSaturated(sudo).`should be true`()
        mkConstraint(posA, posB, posC)
            .isSaturated(sudo).`should be false`()
    }

}