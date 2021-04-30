package de.sudoq.model.solverGenerator.solution;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Test;

import de.sudoq.model.sudoku.Position;

public class DerivationCellTests {

	@Test
	public void standardTest() {
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

	@Test(expected = NullPointerException.class)
	public void positionNull() {
		new DerivationCell(null, new BitSet(), new BitSet());
	}

	@Test(expected = NullPointerException.class)
	public void candidatesNull() {
		new DerivationCell(Position.get(1, 1), null, null);
	}

}
