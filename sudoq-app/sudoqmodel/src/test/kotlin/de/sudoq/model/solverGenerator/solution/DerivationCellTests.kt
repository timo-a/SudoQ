package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.sudoku.Position
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.BitSet

internal class DerivationCellTests {
    @Test
    fun standardTest() {
        val relevantCandidates = BitSet()
        relevantCandidates.set(5)
        relevantCandidates.set(3)
        val irrelevantCandidates = BitSet()
        irrelevantCandidates.set(1)
        irrelevantCandidates.set(2)
        val derivation = DerivationCell(Position[1, 1], relevantCandidates, irrelevantCandidates)
        Assertions.assertEquals(derivation.position, Position[1, 1])
        derivation.relevantCandidates `should be equal to` relevantCandidates
        derivation.irrelevantCandidates `should be equal to` irrelevantCandidates
    }
}
