package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.sudokuTypes.StairStepSudokuType9x9;
import de.sudoq.model.sudoku.sudokuTypes.TypeSquiggly;

public class SquigglySudokuTypesTest {

	@Test
	public void initialiser() {
		TypeSquiggly stair = new StairStepSudokuType9x9();
		ConstraintTest(stair);
	}

	public void ConstraintTest(TypeSquiggly squig) {
		int counter = 0;
		boolean touched[] = { false, false, false, false, false, false, false, false, false };// schonmal
																								// da
																								// gewesen
																								// true,
																								// sonst
																								// false.
		for (Constraint c : squig) {
			assertTrue(c.getSize() == 9);// jeder constraint hat länge 9
			counter++;

			if (c.toString().startsWith("Block")) {
				if (c.toString().contains("Block A")) {
					assertTrue(!touched[0]);// es soll jeden Constraint nur
											// einmal geben
					constraintsA(c);
					touched[0] = true;
				}
				if (c.toString().contains("Block B")) {
					assertTrue(!touched[1]);
					constraintsB(c);
					touched[1] = true;
				}
				if (c.toString().contains("Block C")) {
					assertTrue(!touched[2]);
					constraintsC(c);
					touched[2] = true;
				}
				if (c.toString().contains("Block D")) {
					assertTrue(!touched[3]);
					constraintsD(c);
					touched[3] = true;
				}
				if (c.toString().contains("Block E")) {
					assertTrue(!touched[4]);
					constraintsE(c);
					touched[4] = true;
				}
				if (c.toString().contains("Block F")) {
					assertTrue(!touched[5]);
					constraintsF(c);
					touched[5] = true;
				}
				if (c.toString().contains("Block G")) {
					assertTrue(!touched[6]);
					constraintsG(c);
					touched[6] = true;
				}
				if (c.toString().contains("Block H")) {
					assertTrue(!touched[7]);
					constraintsH(c);
					touched[7] = true;
				}
				if (c.toString().contains("Block I")) {
					assertTrue(!touched[8]);
					constraintsI(c);
					touched[8] = true;
				}
			}

		}
		assertTrue("counter is " + counter, counter == 27);// wir wollen genau
															// 27
															// constraints haben
		for (boolean b : touched)
			assertTrue(b); // jeder Constraint soll gefunden worden sein. keiner
							// 2mal siehe oben
	}

	protected void constraintsA(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(0, 0));
		m.add(Position.get(1, 0));
		m.add(Position.get(2, 0));
		m.add(Position.get(3, 0));
		m.add(Position.get(0, 0 + 1));
		m.add(Position.get(1, 0 + 1));
		m.add(Position.get(2, 0 + 1));
		m.add(Position.get(0, 0 + 2));
		m.add(Position.get(1, 0 + 2));

		assertions(m, c);
	}

	protected void constraintsB(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(4, 1 - 1));
		m.add(Position.get(5, 1 - 1));
		m.add(Position.get(6, 1 - 1));
		m.add(Position.get(3, 1));
		m.add(Position.get(4, 1));
		m.add(Position.get(5, 1));
		m.add(Position.get(2, 1 + 1));
		m.add(Position.get(3, 1 + 1));
		m.add(Position.get(4, 1 + 1));

		assertions(m, c);
	}

	protected void constraintsC(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(7, 2 - 2));
		m.add(Position.get(8, 2 - 2));
		m.add(Position.get(6, 2 - 1));
		m.add(Position.get(7, 2 - 1));
		m.add(Position.get(8, 2 - 1));
		m.add(Position.get(5, 2));
		m.add(Position.get(6, 2));
		m.add(Position.get(7, 2));
		m.add(Position.get(8, 2));

		assertions(m, c);
	}

	protected void constraintsD(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(0, 3));
		m.add(Position.get(1, 3));
		m.add(Position.get(2, 3));
		m.add(Position.get(3, 3));
		m.add(Position.get(0, 3 + 1));
		m.add(Position.get(1, 3 + 1));
		m.add(Position.get(2, 3 + 1));
		m.add(Position.get(0, 3 + 2));
		m.add(Position.get(1, 3 + 2));

		assertions(m, c);
	}

	protected void constraintsE(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(4, 4 - 1));
		m.add(Position.get(5, 4 - 1));
		m.add(Position.get(6, 4 - 1));
		m.add(Position.get(3, 4));
		m.add(Position.get(4, 4));
		m.add(Position.get(5, 4));
		m.add(Position.get(2, 4 + 1));
		m.add(Position.get(3, 4 + 1));
		m.add(Position.get(4, 4 + 1));

		assertions(m, c);
	}

	protected void constraintsF(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(7, 5 - 2));
		m.add(Position.get(8, 5 - 2));
		m.add(Position.get(6, 5 - 1));
		m.add(Position.get(7, 5 - 1));
		m.add(Position.get(8, 5 - 1));
		m.add(Position.get(5, 5));
		m.add(Position.get(6, 5));
		m.add(Position.get(7, 5));
		m.add(Position.get(8, 5));

		assertions(m, c);
	}

	protected void constraintsG(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(0, 6));
		m.add(Position.get(1, 6));
		m.add(Position.get(2, 6));
		m.add(Position.get(3, 6));
		m.add(Position.get(0, 6 + 1));
		m.add(Position.get(1, 6 + 1));
		m.add(Position.get(2, 6 + 1));
		m.add(Position.get(0, 6 + 2));
		m.add(Position.get(1, 6 + 2));

		assertions(m, c);
	}

	protected void constraintsH(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(4, 7 - 1));
		m.add(Position.get(5, 7 - 1));
		m.add(Position.get(6, 7 - 1));
		m.add(Position.get(3, 7));
		m.add(Position.get(4, 7));
		m.add(Position.get(5, 7));
		m.add(Position.get(2, 7 + 1));
		m.add(Position.get(3, 7 + 1));
		m.add(Position.get(4, 7 + 1));

		assertions(m, c);
	}

	protected void constraintsI(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(7, 8 - 2));
		m.add(Position.get(8, 8 - 2));
		m.add(Position.get(6, 8 - 1));
		m.add(Position.get(7, 8 - 1));
		m.add(Position.get(8, 8 - 1));
		m.add(Position.get(5, 8));
		m.add(Position.get(6, 8));
		m.add(Position.get(7, 8));
		m.add(Position.get(8, 8));

		assertions(m, c);
	}

	protected void assertions(List<Position> m, Constraint c) {
		int counter = 0;

		for (Position p : c) {
			assertTrue("in Constraint " + c + ". Position " + p + "fälschlicherweise enthalten", m.contains(p));
			counter++;
		}
		assertTrue(counter == m.size());
	}

}