package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;

public class StandardSudokuType4x4Tests {

	SudokuType sst44 = TypeBuilder.getType(SudokuTypes.standard4x4);

	@Test
	public void constraintsTest() {

		assertTrue(sst44.constraints.size() == 4 + 4 + 4);

		for (Constraint c : sst44)
			assertTrue(c.getSize() == 4);

		BitSet b = new BitSet(4);
		for (Constraint c : sst44) {
			if (c.toString().contains("Block 0")) {
				b.flip(0);
				assertTrue(allPosWithinBounds(0, 1, 0, 1, c));
			}

			if (c.toString().contains("Block 1")) {
				b.flip(1);
				assertTrue(allPosWithinBounds(2, 3, 0, 1, c));
			}

			if (c.toString().contains("Block 2")) {
				b.flip(2);
				assertTrue(allPosWithinBounds(0, 1, 2, 3, c));
			}

			if (c.toString().contains("Block 3")) {
				b.flip(3);
				assertTrue(allPosWithinBounds(2, 3, 2, 3, c));
			}
		}
		b.flip(0, 4);
		assertTrue(b.isEmpty());
	}

	private boolean allPosWithinBounds(int minX, int maxX, int minY, int maxY, Constraint c) {
		for (Position p : c)
			if (p.getX() < minX || p.getX() > maxX || p.getY() < minY || p.getY() > maxY)
				return false;
		return true;
	}

	@Test
	public void getAllocationFactorTest() {
		assertTrue(sst44.getStandardAllocationFactor() == 0.25f);
	}
	
	@Test
	public void getEnumTypeTest() {
		assertTrue(sst44.getEnumType() == SudokuTypes.standard4x4);
	}

	//@Test ignored no point in specification by test
	public void buildComplexityConstraintTest() {
		SudokuType standard4x4 = TypeBuilder.getType(SudokuTypes.standard4x4);

		ComplexityConstraint comCo = standard4x4.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 12, 100, 130, 2);

		comCo = standard4x4.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 9, 125, 160, 3);

		comCo = standard4x4.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 7, 150, 190, Integer.MAX_VALUE);

		comCo = standard4x4.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 5, 175, 2500, Integer.MAX_VALUE);

		comCo = standard4x4.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 10, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertNull(standard4x4.buildComplexityConstraint(null));

	}
}
