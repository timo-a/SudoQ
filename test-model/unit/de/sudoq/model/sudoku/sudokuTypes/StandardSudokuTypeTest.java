package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.sudokuTypes.StandardSudokuType9x9;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;
import de.sudoq.model.sudoku.sudokuTypes.TypeStandard;

public class StandardSudokuTypeTest {

	StandardSudokuType9x9 sst = new StandardSudokuType9x9();

	@Test
	public void test() {

		Position p = sst.getSize();
		assertNotNull("getSize returns null!", p);
		assertEquals("getX does not return 9!", 9, p.getX());
		assertEquals("getY does not return 9!", 9, p.getY());
	}

	@Test
	public void nonQuadraticBlocksTest() {
		TypeBasic ss18 = new SST18x18();
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

		}
	}

	public class SST18x18 extends TypeStandard {
		public SST18x18() {
			super(18);
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
		StandardSudokuType9x9 type = new StandardSudokuType9x9();
		testComplexity(type.buildComplexityConstraint(Complexity.easy), Complexity.easy, 35, 45, 600, 1100, 2);
		testComplexity(type.buildComplexityConstraint(Complexity.medium), Complexity.medium, 27, 35, 1100, 2050, 3);
		testComplexity(type.buildComplexityConstraint(Complexity.difficult), Complexity.difficult, 22, 28, 1600, 3000,
				Integer.MAX_VALUE);
		testComplexity(type.buildComplexityConstraint(Complexity.infernal), Complexity.infernal, 17, 24, 2400, 25000,
				Integer.MAX_VALUE);
		assertTrue(type.buildComplexityConstraint(null) == null);
	}

	private void testComplexity(ComplexityConstraint constraint, Complexity complexity, int minFields, int maxFields,
			int minComplexityIdentifier, int maxComplexityIdentifier, int numberOfAllowedHelpers) {
		assertEquals(constraint.getComplexity(), complexity);
		assertEquals(constraint.getMinComplexityIdentifier(), minComplexityIdentifier);
		assertEquals(constraint.getMaxComplexityIdentifier(), maxComplexityIdentifier);
		assertEquals(constraint.getNumberOfAllowedHelpers(), numberOfAllowedHelpers);
	}

}
