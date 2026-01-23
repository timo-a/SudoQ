package de.sudoq.model.solverGenerator.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.BitSet;

import org.junit.jupiter.api.Test;

import de.sudoq.model.sudoku.Position;

class DerivationCellTests {

    @Test
    void standardTest() {
		BitSet relevantCandidates = new BitSet();
		relevantCandidates.set(5);
		relevantCandidates.set(3);
		BitSet irrelevantCandidates = new BitSet();
		irrelevantCandidates.set(1);
		irrelevantCandidates.set(2);
		DerivationCell derivation = new DerivationCell(Position.get(1, 1), relevantCandidates, irrelevantCandidates);
		assertEquals(derivation.getPosition(), Position.get(1, 1));
		assertEquals(derivation.getRelevantCandidates(), relevantCandidates);
		assertEquals(derivation.getIrrelevantCandidates(), irrelevantCandidates);
	}

    @Test
    void positionNull() {
		assertThrows(NullPointerException.class, () -> new DerivationCell(null, new BitSet(), new BitSet()));
	}

    @Test
    void candidatesNull() {
		assertThrows(NullPointerException.class, () -> new DerivationCell(Position.get(1, 1), null, null));
	}

}
