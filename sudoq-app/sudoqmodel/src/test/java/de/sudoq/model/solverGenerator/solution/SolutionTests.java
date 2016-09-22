package de.sudoq.model.solverGenerator.solution;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;
import java.util.Iterator;

import org.junit.Test;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;

public class SolutionTests {

	@Test
	public void standardTest() {
		Solution sol = new Solution();
		Action act = new SolveActionFactory().createAction(5, new Field(true, 3, -1, 9));
		sol.setAction(act);
		assertEquals(sol.getAction(), act);
		sol.setAction(null);
		assertEquals(sol.getAction(), act);

		SolveDerivation[] derivs = new SolveDerivation[3];
		derivs[0] = new SolveDerivation();
		derivs[0].addDerivationBlock(new DerivationBlock(new Constraint(new UniqueConstraintBehavior(),
				ConstraintType.LINE)));
		derivs[1] = new SolveDerivation();
		derivs[1].addDerivationField(new DerivationField(Position.get(1, 1), new BitSet(), new BitSet()));
		derivs[2] = new SolveDerivation();
		sol.addDerivation(derivs[0]);
		sol.addDerivation(derivs[1]);
		sol.addDerivation(derivs[2]);

		int counter = 0;
		for (SolveDerivation sd: sol.getDerivations()){
			assertEquals(sd, derivs[counter]);
			counter++;
		}
	}
}