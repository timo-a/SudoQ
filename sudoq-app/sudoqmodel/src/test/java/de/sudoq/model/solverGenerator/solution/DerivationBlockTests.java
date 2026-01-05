package de.sudoq.model.solverGenerator.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;

class DerivationBlockTests {

    @Test
    void standardTest() {
		Constraint constr = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
		DerivationBlock block = new DerivationBlock(constr);
		assertEquals(block.getBlock(), constr);
	}

    @Test
    void constraintNull() {
		assertThrows(NullPointerException.class, () -> new DerivationBlock(null));
	}

}
