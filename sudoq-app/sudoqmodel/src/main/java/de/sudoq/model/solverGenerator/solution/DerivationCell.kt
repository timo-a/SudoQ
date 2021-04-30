package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * A Cell that is relevant to a Derivation.
 *
 * @property position [Position] of the [de.sudoq.model.sudoku.Cell]
 * @param relevantCandidates relevant Candidates as BitSet
 * @param irrelevantCandidates irrelevant Candidates as BitSet
 */
class DerivationCell(val position: Position, relevantCandidates: BitSet, irrelevantCandidates: BitSet) {

    /**
     * Candidates relevant to the Derivation.
     *
     * @return A clone of the [BitSet] representing candidates relevant to the Derivation.
     */
    public val relevantCandidates: BitSet = relevantCandidates.clone() as BitSet
        get() {
            return field.clone() as BitSet
        }

    /**
     * Candidates not relevant to the Derivation.
     *
     * @return A clone of the [BitSet] representing candidates not relevant to the Derivation.
     */
    public val irrelevantCandidates: BitSet = irrelevantCandidates.clone() as BitSet
        get() {
            return field.clone() as BitSet
        }
}