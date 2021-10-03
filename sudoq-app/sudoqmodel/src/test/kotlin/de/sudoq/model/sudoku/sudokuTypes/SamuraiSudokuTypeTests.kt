package de.sudoq.model.sudoku.sudokuTypes;

org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;

public class SamuraiSudokuTypeTests {

	SudokuType samurai = TypeBuilder.getType(SudokuTypes.samurai);

	@Test
	public void constraintsTest() {

		assertTrue("is just " + samurai.constraints.size(), samurai.constraints.size() == 5 * 9 + 5 * 9 + 5 * 9 - 4);

		for (Constraint c : samurai)
			assertTrue(c.getSize() == 9);

		checkBlockOfRows(0, 0, 0);
		checkBlockOfRows(12, 0, 9);
		checkBlockOfRows(0, 12, 18);
		checkBlockOfRows(12, 12, 27);
		checkBlockOfRows(6, 6, 36);

		checkBlockOfCols(0, 0, 0);
		checkBlockOfCols(12, 0, 9);
		checkBlockOfCols(0, 12, 18);
		checkBlockOfCols(12, 12, 27);
		checkBlockOfCols(6, 6, 36);

		checkBlockofBlocks(0, 0, 0);
		checkBlockofBlocks(12, 0, 9);
		checkBlockofBlocks(0, 12, 18);
		checkBlockofBlocks(12, 12, 27);

		singleBlocktest(9, 6, 36);
		singleBlocktest(6, 9, 37);
		singleBlocktest(9, 9, 38);
		singleBlocktest(12, 9, 39);
		singleBlocktest(9, 12, 40);

	}

	private void checkBlockOfRows(int x, int y, int startIndex) {

		int counter = 0;
		BitSet b = new BitSet(9);
		for (Constraint c : samurai) {
			for (int i = 0; i < 9; i++) {
				if (c.toString().equals("Row " + (startIndex + i))) {
					b.flip(i);
					counter++;
					assertTrue(allPosWithinBounds(x + 0, x + 8, y + i, y + i, c));
				}
			}
		}
		b.flip(0, 9);
		assertTrue(b.isEmpty());
		assertTrue(counter == 9);
	}

	private void checkBlockOfCols(int x, int y, int startIndex) {

		int counter = 0;
		BitSet b = new BitSet(9);
		for (Constraint c : samurai) {
			for (int i = 0; i < 9; i++) {
				if (c.toString().equals("Column " + (startIndex + i))) {
					b.flip(i);
					counter++;
					assertTrue(allPosWithinBounds(x + i, x + i, y + 0, y + 8, c));
				}
			}
		}
		b.flip(0, 9);
		assertTrue(b.isEmpty() && counter == 9);
	}

	private void checkBlockofBlocks(int x, int y, int startIndex) {

		int counter = 0;
		BitSet b = new BitSet(9);
		for (Constraint c : samurai) {
			for (int i = 0; i < 9; i++) {
				if (c.toString().equals("Block " + (startIndex + i))) {
					b.flip(i);
					counter++;
					assertTrue(allPosWithinBounds(x + i % 3 * 3, x + i % 3 * 3 + 3, y + i / 3 * 3, y + i / 3 * 3 + 3, c));
				}
			}
		}
		b.flip(0, 9);
		assertTrue(b.isEmpty() && counter == 9);
	}

	private void singleBlocktest(int x, int y, int startIndex) {
		int counter = 0;
		for (Constraint c : samurai) {
			if (c.toString().equals("Block " + startIndex)) {
				counter++;
				assertTrue(allPosWithinBounds(x, x + 3, y, y + 3, c));
			}

		}
		assertTrue(counter == 1);
	}

	private boolean allPosWithinBounds(int minX, int maxX, int minY, int maxY, Constraint c) {
		for (Position p : c) {
			if (p.getX() < minX || p.getX() > maxX || p.getY() < minY || p.getY() > maxY)
				return false;
		}
		return true;
	}

	@Test
	public void getEnumTypeTest() {
		assertTrue(samurai.getEnumType() == SudokuTypes.samurai);
	}

	//@Test ignored no point in specification by test
	public void getStandartAllocationFactorTest() {
		assertTrue(samurai.getStandardAllocationFactor() == 0.05f);
	}

	//@Test ignored no point in specification by test
	public void buildComplexityConstraintTest() {
		SudokuType samurai = TypeBuilder.getType(SudokuTypes.samurai);

		ComplexityConstraint comCo = samurai.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 70, 1000, 1500, 2);

		comCo = samurai.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 55, 1500, 2300, 3);

		comCo = samurai.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 40, 2300, 4000, Integer.MAX_VALUE);

		comCo = samurai.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 30, 4000, 25000, Integer.MAX_VALUE);

		comCo = samurai.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertTrue(samurai.buildComplexityConstraint(null) == null);

	}
}
