package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;

public class StairStepSudokuTests {

	SudokuType stair = TypeBuilder.getType(SudokuTypes.stairstep);

	@Test
	public void getEnumTypeTests() {
		assertTrue(stair.getEnumType() == SudokuTypes.stairstep);
	}
	
	@Test
	public void buildComplexityConstraintTest() {
		
		ComplexityConstraint comCo = stair.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 40, 500, 800, 2);

		comCo = stair.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 32, 750, 1050, 3);

		comCo = stair.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE);

		comCo = stair.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE);

		comCo = stair.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertTrue(stair.buildComplexityConstraint(null) == null);

	}

	@Test
	public void getAllocationFactorTest() {
		assertTrue(stair.getStandardAllocationFactor() == 0.25f);
	}
}
