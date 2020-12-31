package de.sudoq.model.solverGenerator.solution;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;
import java.util.Iterator;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;

public class SolveDerivationTests {

	@Test
	public void standardTest() {
		DerivationBlock[] blocks = new DerivationBlock[2];
		SolveDerivation sd = new SolveDerivation();
		Constraint constr = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
		blocks[0] = new DerivationBlock(constr);
		sd.addDerivationBlock(blocks[0]);
		blocks[1] = new DerivationBlock(constr);
		sd.addDerivationBlock(blocks[1]);
		sd.addDerivationBlock(null);

		DerivationCell[] fields = new DerivationCell[3];
		BitSet relevantCandidates = new BitSet();
		relevantCandidates.set(5);
		relevantCandidates.set(3);
		BitSet irrelevantCandidates = new BitSet();
		irrelevantCandidates.set(1);
		irrelevantCandidates.set(2);
		fields[0] = new DerivationCell(Position.get(1, 1), relevantCandidates, irrelevantCandidates);
		sd.addDerivationCell(fields[0]);
		fields[1] = new DerivationCell(Position.get(2, 2), irrelevantCandidates, relevantCandidates);
		sd.addDerivationCell(fields[1]);
		fields[2] = new DerivationCell(Position.get(3, 5), new BitSet(), new BitSet());
		sd.addDerivationCell(fields[2]);
		sd.addDerivationCell(null);

		int counter = 0;
		Iterator<DerivationBlock> itBlock = sd.getBlockIterator();
		while (itBlock.hasNext()) {
			assertEquals(itBlock.next(), blocks[counter]);
			counter++;
		}

		counter = 0;
		Iterator<DerivationCell> itField = sd.getCellIterator();
		while (itField.hasNext()) {
			assertEquals(itField.next(), fields[counter]);
			counter++;
		}
	}

}
