package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.BitSet;

import org.junit.Test;


import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;

public class XSudokuTests {

	SudokuType stX = TypeBuilder.getType(SudokuTypes.Xsudoku);

	@Test
	public void ConstraintsTest() {

		BitSet b = new BitSet(2);

		ArrayList<Position> ddr = new ArrayList<Position>();
		for (int i = 0; i < 9; i++) {
			ddr.add(Position.get(i, i));
		}

		ArrayList<Position> dur = new ArrayList<Position>();
		for (int i = 0; i < 9; i++) {
			dur.add(Position.get(i, 8 - i));
		}

		int counter = 0;
		for (Constraint c : stX) {
			counter++;
			if (c.toString().contains("diagonal up right")) {
				b.flip(0);
				assertTrue(c.getSize() == 9);
				for (Position p : c) {
					assertTrue(dur.contains(p));
					dur.remove(p);
				}
				assertTrue(dur.size() == 0);
			}
			if (c.toString().contains("diagonal down right")) {
				b.flip(1);
				assertTrue(c.getSize() == 9);
				for (Position p : c) {
					assertTrue(ddr.contains(p));
					ddr.remove(p);
				}
				assertTrue(ddr.size() == 0);
			}
		}
		b.flip(0, 2);
		assertTrue(b.isEmpty());

		assertTrue(counter == 9 + 9 + 9 + 2);

	}

	@Test
	public void getEnumTypeTest() {
		assertTrue(stX.getEnumType() == SudokuTypes.Xsudoku);
	}

	@Test
	public void buildComplexityConstraintTest() {
		SudokuType xSudo = TypeBuilder.getType(SudokuTypes.Xsudoku);

		ComplexityConstraint comCo = xSudo.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 40, 450, 750, 2);

		comCo = xSudo.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 32, 700, 1050, 3);

		comCo = xSudo.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE);

		comCo = xSudo.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE);

		comCo = xSudo.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertNull(xSudo.buildComplexityConstraint(null));

	}

	@Test
	public void getAllocationFactorTest() {
		assertTrue(stX.getStandardAllocationFactor() == 0.25f);
	}

	
}
