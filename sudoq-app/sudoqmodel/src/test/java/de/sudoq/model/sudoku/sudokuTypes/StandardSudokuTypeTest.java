package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

public class StandardSudokuTypeTest {

	SudokuType sst = TypeBuilder.getType(SudokuTypes.standard9x9);

	@Test
	public void test() {

		Position p = sst.getSize();
		assertNotNull("getSize returns null!", p);
		assertEquals("getX does not return 9!", 9, p.getX());
		assertEquals("getY does not return 9!", 9, p.getY());
	}

	@Test
	public void nonQuadraticBlocksTest() {
		/*SudokuType ss18 = new SST18x18();
		boolean bounds = true;
		Position p0 = Position.get(0, 0);
		Position p1 = Position.get(5, 0);
		Position p2 = Position.get(0, 2);
		Position p3 = Position.get(5, 2);

		for (Constraint c : ss18) {
			if (c.toString().contains("Block 0")) {

				boolean exists = false;
				for (Position p : c)
					if (p.equals(p0))
						exists = true;
				bounds &= exists;

				exists = false;
				for (Position p : c)
					if (p.equals(p1))
						exists = true;
				bounds &= exists;

				exists = false;
				for (Position p : c)
					if (p.equals(p2))
						exists = true;
				bounds &= exists;

				exists = false;
				for (Position p : c)
					if (p.equals(p3))
						exists = true;
				assertTrue(bounds &= exists);
			}

		}*/
	}

	public class SST18x18 extends SudokuType {
		public SST18x18() {
			//TODO somehow create this super(18);
		}

		@Override
		public SudokuTypes getEnumType() {
			return null;
		}

		@Override
		public ComplexityConstraint buildComplexityConstraint(Complexity complexity) {
			return null;
		}

		@Override
		public float getStandardAllocationFactor() {
			return 0;
		}
	}

	@Test
	public void getEnumTypeTest() {
		assertTrue(sst.getEnumType() == SudokuTypes.standard9x9);
	}

	@Test
	public void getStandartAllocationFactorTest() {
		assertTrue(sst.getStandardAllocationFactor() == 0.35f);
	}

	@Test
	public void complexityTest() {
		SudokuType type = TypeBuilder.getType(SudokuTypes.standard9x9);
		testComplexity(type, Complexity.easy,      35, 45,  400,  1200, 2);
		testComplexity(type, Complexity.medium,    27, 35, 1200,  2500, 3);
		testComplexity(type, Complexity.difficult, 22, 28, 2500,  4000, Integer.MAX_VALUE);
		testComplexity(type, Complexity.infernal,  17, 24, 4000, 25000,	Integer.MAX_VALUE);
		assertNull(type.buildComplexityConstraint(null));
	}

	private void testComplexity(SudokuType type,
								Complexity complexity,
	                            int minFields, int maxFields,
	                            int minComplexityIdentifier,
	                            int maxComplexityIdentifier,
	                            int numberOfAllowedHelpers) {
		ComplexityConstraint constraint = type.buildComplexityConstraint(complexity);
		assertEquals("expected complexity is: ", constraint.getComplexity(),              complexity);
		assertEquals("expected minComplexity:", constraint.getMinComplexityIdentifier(), minComplexityIdentifier);
		assertEquals("expected maxComplexity:", constraint.getMaxComplexityIdentifier(), maxComplexityIdentifier);
		assertEquals("expected number of allowed Helpers:", constraint.getNumberOfAllowedHelpers(),  numberOfAllowedHelpers);
	}

}
