package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;


import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;
import de.sudoq.model.sudoku.sudokuTypes.StandardSudokuType6x6;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;

public class StandardSudokuType6x6Tests {

	TypeBasic sst66 = new StandardSudokuType6x6();

	@Test
	public void constraintsTest() {

		assertTrue(sst66.constraints.size() == 6 + 6 + 6);

		for (Constraint c : sst66)
			assertTrue(c.getSize() == 6);

		BitSet b = new BitSet(6);
		for (Constraint c : sst66) {
			if (c.toString().contains("Block 0")) {
				b.flip(0);
				assertTrue(allPosWithinBounds(0, 2, 0, 1, c));
			}

			if (c.toString().contains("Block 1")) {
				b.flip(1);
				assertTrue(allPosWithinBounds(3, 5, 0, 1, c));
			}

			if (c.toString().contains("Block 2")) {
				b.flip(2);
				assertTrue(allPosWithinBounds(0, 2, 2, 3, c));
			}

			if (c.toString().contains("Block 3")) {
				b.flip(3);
				assertTrue(allPosWithinBounds(3, 5, 2, 3, c));
			}

			if (c.toString().contains("Block 4")) {
				b.flip(4);
				assertTrue(allPosWithinBounds(0, 2, 4, 5, c));
			}

			if (c.toString().contains("Block 5")) {
				b.flip(5);
				assertTrue(allPosWithinBounds(3, 5, 4, 5, c));
			}
		}
		b.flip(0, 6);
		assertTrue(b.isEmpty());
	}

	private boolean allPosWithinBounds(int minX, int maxX, int minY, int maxY, Constraint c) {
		for (Position p : c)
			if (p.getX() < minX || p.getX() > maxX || p.getY() < minY || p.getY() > maxY)
				return false;
		return true;
	}

	@Test
	public void getBlockSizeTest() {
		Position p = sst66.getBlockSize();

		assertTrue("instead: " + p.getX() + ", " + p.getY(), p.getX() == 3 && p.getY() == 2);
	}

	@Test
	public void getEnumTypeTest() {
		assertTrue(sst66.getEnumType() == SudokuTypes.standard6x6);
	}

	@Test
	public void getStandartAllocationFactorTest() {
		assertTrue(sst66.getStandardAllocationFactor() == 0.35f);
	}

	@Test
	public void buildComplexityConstraintTest() {
		sst66 = new StandardSudokuType6x6();

		ComplexityConstraint comCo = sst66.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 26, 200, 350, 2);

		comCo = sst66.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 17, 350, 550, 3);

		comCo = sst66.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 12, 550, 800, Integer.MAX_VALUE);

		comCo = sst66.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 10, 800, 10000, Integer.MAX_VALUE);

		comCo = sst66.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 10, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertTrue(sst66.buildComplexityConstraint(null) == null);

	}

}
