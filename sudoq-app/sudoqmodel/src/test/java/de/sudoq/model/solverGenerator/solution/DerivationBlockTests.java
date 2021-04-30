package de.sudoq.model.solverGenerator.solution;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;

public class DerivationBlockTests {

	@Test
	public void standardTest() {
		Constraint constr = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
		DerivationBlock block = new DerivationBlock(constr);
		assertEquals(block.getBlock(), constr);
	}

	@Test(expected = NullPointerException.class)
	public void constraintNull() {
		new DerivationBlock(null);
	}

}
