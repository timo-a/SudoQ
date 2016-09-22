package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;

public class StandardSudokuType16x16Tests {

	SudokuType sst1616 = TypeBuilder.getType(SudokuTypes.standard16x16);

	@Test
	public void constraintsTest() {

		assertTrue(sst1616.constraints.size() == 16*3);

		for (Constraint c : sst1616)
			assertTrue(c.getSize() == 16);
	}
	
	@Test
	public void getEnumTypeTest() {
		assertTrue(sst1616.getEnumType() == SudokuTypes.standard16x16);
	}
	
	@Test
	public void getStandartAllocationFactorTest() {
		assertTrue(sst1616.getStandardAllocationFactor() == 0.25f);
	}

	@Test
	public void buildComplexityConstraintTest() {
		SudokuType standard16x16 = TypeBuilder.getType(SudokuTypes.standard16x16);

		ComplexityConstraint comCo = standard16x16.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 190, 900, 1300, 2);

		comCo = standard16x16.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 140, 1400, 2200, 3);

		comCo = standard16x16.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 120, 2400, 3000, Integer.MAX_VALUE);

		comCo = standard16x16.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 105, 3000, 25000, Integer.MAX_VALUE);

		comCo = standard16x16.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertTrue(standard16x16.buildComplexityConstraint(null) == null);

	}
}
