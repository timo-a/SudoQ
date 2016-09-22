package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.BitSet;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
//TODO since we no longer have classes maybe we can merge some of these tests?
public class HyperSudokuTests {

	SudokuType stHy = TypeBuilder.getType(SudokuTypes.HyperSudoku);

	@Test
	public void ConstraintsTest() {

		BitSet b = new BitSet(4);

		ArrayList<Position> b0 = new ArrayList<Position>();
		for (int i = 0; i < 9; i++) {
			b0.add(Position.get(1 + i % 3, 1 + i / 3));
		}
		ArrayList<Position> b1 = new ArrayList<Position>();
		for (int i = 0; i < 9; i++) {
			b1.add(Position.get(5 + i % 3, 1 + i / 3));
		}
		ArrayList<Position> b2 = new ArrayList<Position>();
		for (int i = 0; i < 9; i++) {
			b2.add(Position.get(1 + i % 3, 5 + i / 3));
		}
		ArrayList<Position> b3 = new ArrayList<Position>();
		for (int i = 0; i < 9; i++) {
			b3.add(Position.get(5 + i % 3, 5 + i / 3));
		}

		int counter = 0;
		for (Constraint c : stHy) {
			counter++;
			if (c.toString().equals("Extra block 0")) {
				b.flip(0);
				assertTrue(c.getSize() == 9);
				for (Position p : c) {
					assertTrue(b0.contains(p));
					b0.remove(p);
				}
				assertTrue(b0.size() == 0);
			}
			if (c.toString().equals("Extra block 1")) {
				b.flip(1);
				assertTrue(c.getSize() == 9);
				for (Position p : c) {
					assertTrue(b1.contains(p));
					b1.remove(p);
				}
				assertTrue(b1.size() == 0);
			}
			if (c.toString().equals("Extra block 2")) {
				b.flip(2);
				assertTrue(c.getSize() == 9);
				for (Position p : c) {
					assertTrue(b2.contains(p));
					b2.remove(p);
				}
				assertTrue(b2.size() == 0);
			}
			if (c.toString().equals("Extra block 3")) {
				b.flip(3);
				assertTrue(c.getSize() == 9);
				for (Position p : c) {
					assertTrue(b3.contains(p));
					b3.remove(p);
				}
				assertTrue(b3.size() == 0);
			}

		}
		b.flip(0, 4);

		assertTrue(b.isEmpty());
		assertTrue(counter == 9 + 9 + 9 + 4);

	}

	@Test
	public void getEnumTypeTest() {
		assertTrue(stHy.getEnumType() == SudokuTypes.HyperSudoku);
	}

	@Test
	public void getAllocationFactorTest() {
		assertTrue(stHy.getStandardAllocationFactor() == 0.25f);
	}

	@Test
	public void buildComplexityConstraintTest() {
		assertTrue(stHy.buildComplexityConstraint(null) == null);
		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.easy), new ComplexityConstraint(
				Complexity.easy, 40, 500, 800, 2)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.medium), new ComplexityConstraint(
				Complexity.medium, 32, 750, 1050, 3)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.easy), new ComplexityConstraint(
				Complexity.easy, 40, 500, 800, 2)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.difficult), new ComplexityConstraint(
				Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.infernal), new ComplexityConstraint(
				Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.arbitrary), new ComplexityConstraint(
				Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE)));
	}

	private boolean complexityEqual(ComplexityConstraint c1, ComplexityConstraint c2) {
		return c1.getComplexity() == c2.getComplexity() && c1.getAverageFields() == c2.getAverageFields()
				&& c1.getMinComplexityIdentifier() == c2.getMinComplexityIdentifier()
				&& c1.getMaxComplexityIdentifier() == c2.getMaxComplexityIdentifier()
				&& c1.getNumberOfAllowedHelpers() == c2.getNumberOfAllowedHelpers();
	}
}
