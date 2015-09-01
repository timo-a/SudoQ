package de.sudoq.model.sudoku.complexity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

public class ComplexityConstraintTests {

	@Test
	public void standardTest() {
		ComplexityConstraint com = new ComplexityConstraint(Complexity.medium, 32, 1000, 2000, 5);

		assertEquals(com.getComplexity(), Complexity.medium);
		assertEquals(com.getAverageFields(), 32);
		assertEquals(com.getMinComplexityIdentifier(), 1000);
		assertEquals(com.getMaxComplexityIdentifier(), 2000);
		assertEquals(com.getNumberOfAllowedHelpers(), 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullTest() {
		new ComplexityConstraint(null, 32, 1000, 2000, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIdentifierRange() {
		new ComplexityConstraint(Complexity.difficult, 5, 2000, 1000, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeMinIdentifier() {
		new ComplexityConstraint(Complexity.easy, 5, -5, 100, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeAverageFields() {
		new ComplexityConstraint(Complexity.easy, -5, 10, 100, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalNumberOfHelpers() {
		new ComplexityConstraint(Complexity.infernal, 5, 1000, 2000, -5);
	}

	public static void returnsValues(ComplexityConstraint c, Complexity complexity, int averageFields, int minComplexityIdentifier, int maxComplexityIdentifier, int numberOfAllowedHelpers) {
		assertEquals(c.getComplexity(), complexity);
		assertEquals(c.getAverageFields(), averageFields);
		assertEquals(c.getMinComplexityIdentifier(), minComplexityIdentifier);
		assertEquals(c.getMaxComplexityIdentifier(), maxComplexityIdentifier);
		assertEquals(c.getNumberOfAllowedHelpers(), numberOfAllowedHelpers);
	}
}
