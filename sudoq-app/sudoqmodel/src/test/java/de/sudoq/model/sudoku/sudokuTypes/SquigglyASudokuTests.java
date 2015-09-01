package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;
import de.sudoq.model.sudoku.sudokuTypes.SquigglyASudokuType9x9;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;

public class SquigglyASudokuTests {

	TypeBasic squigglyA = new SquigglyASudokuType9x9();
	
	
	@Test
	public void buildComplexityConstraintTest() {
		
		ComplexityConstraint comCo = squigglyA.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 40, 500, 800, 2);

		comCo = squigglyA.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 32, 750, 1050, 3);

		comCo = squigglyA.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE);

		comCo = squigglyA.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE);

		comCo = squigglyA.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertTrue(squigglyA.buildComplexityConstraint(null) == null);

	}

	@Test
	public void getEnumTypeTests() {
		assertTrue(squigglyA.getEnumType() == SudokuTypes.squigglya);
	}
	
	@Test
	public void getAllocationFactorTest() {
		assertTrue(squigglyA.getStandardAllocationFactor() == 0.25f);
	}
}
