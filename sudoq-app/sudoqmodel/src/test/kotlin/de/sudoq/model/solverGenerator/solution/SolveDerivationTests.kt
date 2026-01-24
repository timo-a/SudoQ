package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.UniqueConstraintBehavior
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.BitSet

internal class SolveDerivationTests {
    @Test
    fun standardTest() {
        val blocks = arrayOfNulls<DerivationBlock>(2)
        val sd = SolveDerivation()
        val constr = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE)
        blocks[0] = DerivationBlock(constr)
        sd.addDerivationBlock(blocks[0]!!)
        blocks[1] = DerivationBlock(constr)
        sd.addDerivationBlock(blocks[1]!!)

        val fields = arrayOfNulls<DerivationCell>(3)
        val relevantCandidates = BitSet()
        relevantCandidates.set(5)
        relevantCandidates.set(3)
        val irrelevantCandidates = BitSet()
        irrelevantCandidates.set(1)
        irrelevantCandidates.set(2)
        fields[0] = DerivationCell(Position[1, 1], relevantCandidates, irrelevantCandidates)
        sd.addDerivationCell(fields[0]!!)
        fields[1] = DerivationCell(Position[2, 2], irrelevantCandidates, relevantCandidates)
        sd.addDerivationCell(fields[1]!!)
        fields[2] = DerivationCell(Position[3, 5], BitSet(), BitSet())
        sd.addDerivationCell(fields[2]!!)

        var counter = 0
        val itBlock: Iterator<DerivationBlock> = sd.blockIterator
        while (itBlock.hasNext()) {
            Assertions.assertEquals(itBlock.next(), blocks[counter])
            counter++
        }

        counter = 0
        val itField: Iterator<DerivationCell> = sd.cellIterator
        while (itField.hasNext()) {
            Assertions.assertEquals(itField.next(), fields[counter])
            counter++
        }
    }
}
