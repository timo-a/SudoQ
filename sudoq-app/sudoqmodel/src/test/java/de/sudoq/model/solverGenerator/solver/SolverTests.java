package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SolverTests {

	private Sudoku sudoku;
	private Sudoku sudoku16x16;
	private Solver solver;
	private PositionMap<Integer> solution16x16;

	private static final boolean PRINT_SOLUTIONS = false;

	@Before
	public void before() {
		sudoku = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
		sudoku.setComplexity(Complexity.arbitrary);
		solver = new Solver(sudoku);
		sudoku16x16 = new SudokuBuilder(SudokuTypes.standard16x16).createSudoku();
		sudoku16x16.setComplexity(Complexity.arbitrary);
		solution16x16 = new PositionMap<Integer>(sudoku16x16.getSudokuType().getSize());
	}

	@Test
	public void testSolveOneAutomaticallyApplied() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 1)).setCurrentValue(2);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(1);
		sudoku.getField(Position.get(8, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(2, 2)).setCurrentValue(8);
		sudoku.getField(Position.get(3, 2)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 2)).setCurrentValue(4);
		sudoku.getField(Position.get(2, 3)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 3)).setCurrentValue(2);
		sudoku.getField(Position.get(6, 3)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 4)).setCurrentValue(0);
		sudoku.getField(Position.get(4, 4)).setCurrentValue(7);
		sudoku.getField(Position.get(8, 4)).setCurrentValue(1);
		sudoku.getField(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getField(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getField(Position.get(0, 6)).setCurrentValue(2);
		sudoku.getField(Position.get(7, 6)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 7)).setCurrentValue(3);
		sudoku.getField(Position.get(8, 7)).setCurrentValue(6);
		sudoku.getField(Position.get(2, 8)).setCurrentValue(6);
		sudoku.getField(Position.get(6, 8)).setCurrentValue(2);

		Solution solution = new Solution();
		while (solution != null) {
			solution = solver.solveOne(true);
			if (solution == null)
				break;
			Iterator<SolveDerivation> it = solution.getDerivationIterator();
			SolveDerivation sd = null;
			while (it.hasNext()) {
				sd = it.next();
			}
			if (solution.getAction() != null) {
				assertTrue(this.sudoku.getField(sd.getFieldIterator().next().getPosition()).getCurrentValue() != Field.EMPTYVAL);
			} else {
				solution = null;
			}
		}

		for (Field f : this.sudoku) {
			assertTrue(f.getCurrentValue() != -1);
		}
	}

	@Test
	public void testSolveOneManuallyApplied() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 1)).setCurrentValue(2);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(1);
		sudoku.getField(Position.get(8, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(2, 2)).setCurrentValue(8);
		sudoku.getField(Position.get(3, 2)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 2)).setCurrentValue(4);
		sudoku.getField(Position.get(2, 3)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 3)).setCurrentValue(2);
		sudoku.getField(Position.get(6, 3)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 4)).setCurrentValue(0);
		sudoku.getField(Position.get(4, 4)).setCurrentValue(7);
		sudoku.getField(Position.get(8, 4)).setCurrentValue(1);
		sudoku.getField(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getField(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getField(Position.get(0, 6)).setCurrentValue(2);
		sudoku.getField(Position.get(7, 6)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 7)).setCurrentValue(3);
		sudoku.getField(Position.get(8, 7)).setCurrentValue(6);
		sudoku.getField(Position.get(2, 8)).setCurrentValue(6);
		sudoku.getField(Position.get(6, 8)).setCurrentValue(2);

		Solution solution = new Solution();
		while (solution != null) {
			solution = solver.solveOne(false);
			if (solution == null)
				break;
			Iterator<SolveDerivation> it = solution.getDerivationIterator();
			SolveDerivation sd = null;
			while (it.hasNext()) {
				sd = it.next();
			}
			if (solution.getAction() != null) {
				solution.getAction().execute();
				assertTrue(this.sudoku.getField(sd.getFieldIterator().next().getPosition()).getCurrentValue() != Field.EMPTYVAL);
			} else {
				solution = null;
			}
		}

		for (Field f : this.sudoku) {
			assertTrue(f.getCurrentValue() != -1);
		}
	}

	@Test
	public void solveOneIncorrect() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 0)).setCurrentValue(0);
		assertTrue(solver.solveOne(true) == null);
	}

	@Test
	public void testSolveAllAutomaticallyApplied() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 1)).setCurrentValue(2);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(1);
		sudoku.getField(Position.get(8, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(2, 2)).setCurrentValue(8);
		sudoku.getField(Position.get(3, 2)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 2)).setCurrentValue(4);
		sudoku.getField(Position.get(2, 3)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 3)).setCurrentValue(2);
		sudoku.getField(Position.get(6, 3)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 4)).setCurrentValue(0);
		sudoku.getField(Position.get(4, 4)).setCurrentValue(7);
		sudoku.getField(Position.get(8, 4)).setCurrentValue(1);
		sudoku.getField(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getField(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getField(Position.get(0, 6)).setCurrentValue(2);
		sudoku.getField(Position.get(7, 6)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 7)).setCurrentValue(3);
		sudoku.getField(Position.get(8, 7)).setCurrentValue(6);
		sudoku.getField(Position.get(2, 8)).setCurrentValue(6);
		sudoku.getField(Position.get(6, 8)).setCurrentValue(2);

		solver.solveAll(true, true);
		List<Solution> solutions = solver.getSolutions();
		for (Solution solution : solutions) {
			assertTrue(solution.getAction() != null);
			Iterator<SolveDerivation> it = solution.getDerivationIterator();
			SolveDerivation sd = null;
			while (it.hasNext()) {
				sd = it.next();
				assertTrue(sd != null);
			}
		}

		for (Field f : this.sudoku) {
			assertTrue(f.getCurrentValue() != Field.EMPTYVAL);
		}
	}

	@Test
	public void testSolveAllManuallyApplied() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 1)).setCurrentValue(2);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(1);
		sudoku.getField(Position.get(8, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(2, 2)).setCurrentValue(8);
		sudoku.getField(Position.get(3, 2)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 2)).setCurrentValue(4);
		sudoku.getField(Position.get(2, 3)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 3)).setCurrentValue(2);
		sudoku.getField(Position.get(6, 3)).setCurrentValue(8);
		sudoku.getField(Position.get(1, 4)).setCurrentValue(0);
		sudoku.getField(Position.get(4, 4)).setCurrentValue(7);
		sudoku.getField(Position.get(8, 4)).setCurrentValue(1);
		sudoku.getField(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getField(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getField(Position.get(0, 6)).setCurrentValue(2);
		sudoku.getField(Position.get(7, 6)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 7)).setCurrentValue(3);
		sudoku.getField(Position.get(8, 7)).setCurrentValue(6);
		sudoku.getField(Position.get(2, 8)).setCurrentValue(6);
		sudoku.getField(Position.get(6, 8)).setCurrentValue(2);

		solver.solveAll(true, false);
		List<Solution> solutions = solver.getSolutions();
		for (Solution solution : solutions) {
			assertTrue(solution.getAction() != null);
			solution.getAction().execute();
			Iterator<SolveDerivation> it = solution.getDerivationIterator();
			SolveDerivation sd = null;
			while (it.hasNext()) {
				sd = it.next();
				assertTrue(sd != null);
			}
		}

		SudokuTestUtilities.printSudoku(sudoku);

		for (Field f : this.sudoku) {
			assertTrue(f.getCurrentValue() != Field.EMPTYVAL);
		}
	}

	@Test
	public void solveAllIncorrect() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 0)).setCurrentValue(0);
		assertFalse(solver.solveAll(true, false));
	}

	@Test(expected = IllegalArgumentException.class)
	public void solveAllIllegalComplexity() {
		solver.sudoku.setComplexity(null);
		solver.validate(null, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullSudoku() {
		new Solver(null);
	}

	@Test
	public void testHashing() {
		Map<Position, Integer> map = new HashMap<Position, Integer>();
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				map.put(Position.get(i, j), 16 * i + j);
			}
		}

		int count = 0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				assertEquals(map.get(Position.get(i, j)), new Integer(count));
				count++;
			}
		}
	}

	@Test
	public void testStandard16x16() {
		sudoku16x16.getField(Position.get(2, 0)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(4, 0)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(6, 0)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(7, 0)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(8, 0)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(9, 0)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(11, 0)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(12, 0)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(13, 0)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(0, 1)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(4, 1)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(11, 1)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(12, 1)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(13, 1)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(14, 1)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(1, 2)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(2, 2)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(3, 2)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(4, 2)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(6, 2)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(8, 2)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(11, 2)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(12, 2)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(13, 2)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(15, 2)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(0, 3)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(1, 3)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(2, 3)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(4, 3)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(5, 3)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(9, 3)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(11, 3)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(12, 3)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(14, 3)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(0, 4)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(2, 4)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(5, 4)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(6, 4)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(9, 4)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(11, 4)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(13, 4)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(0, 5)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(1, 5)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(5, 5)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(8, 5)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(11, 5)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(12, 5)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(0, 6)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(3, 6)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(4, 6)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(7, 6)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(9, 6)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(12, 6)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(15, 6)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(0, 7)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(1, 7)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(2, 7)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(3, 7)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(5, 7)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(8, 7)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(9, 7)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(10, 7)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(11, 7)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(0, 8)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(1, 8)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(2, 8)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(3, 8)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(5, 8)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(6, 8)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(10, 8)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(1, 9)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(2, 9)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(4, 9)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(11, 9)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(14, 9)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(1, 10)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(2, 10)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(5, 10)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(6, 10)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(7, 10)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(8, 10)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(9, 10)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(10, 10)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(11, 10)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(12, 10)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(3, 11)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(7, 11)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(9, 11)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(15, 11)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(0, 12)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(2, 12)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(4, 12)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(6, 12)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(8, 12)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(9, 12)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(13, 12)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(15, 12)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(2, 13)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(4, 13)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(5, 13)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(12, 13)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(6, 14)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(12, 14)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(13, 14)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(14, 14)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(15, 14)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(3, 15)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(5, 15)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(7, 15)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(9, 15)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(12, 15)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(14, 15)).setCurrentValue(14);

		sudoku16x16.setComplexity(Complexity.arbitrary);
		Solver solver = new Solver(sudoku16x16);
		ComplexityRelation cr = solver.validate(solution16x16, false); 
		assertEquals(cr, ComplexityRelation.CONSTRAINT_SATURATION);

		// copy solution to current value
		for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
			for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
				sudoku16x16.getField(Position.get(i, j)).setCurrentValue(solution16x16.get(Position.get(i, j)));
			}
		}

		// check constraints
		for (Constraint c : sudoku16x16.getSudokuType()) {
			assertTrue(c.isSaturated(sudoku16x16));
		}

		System.out.println("Solution (16x16) - Complexity: " + solver.sudoku.getComplexityValue());
		if (PRINT_SOLUTIONS) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
				for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
					int value = sudoku16x16.getField(Position.get(i, j)).getCurrentValue();
					String op = value + "";
					if (value < 10)
						op = " " + value;
					if (value == -1)
						op = " x";
					sb.append(op + ", ");
				}
				sb.append("\n");
			}
			System.out.println(sb);
		}
	}

	@Test
	public void testStandard16x16No2() {
		sudoku16x16.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(3, 0)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(4, 0)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(5, 0)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(8, 0)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(10, 0)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(14, 0)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(2, 1)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(6, 1)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(9, 1)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(12, 1)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(13, 1)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(14, 1)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(15, 1)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(1, 2)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(4, 2)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(7, 2)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(9, 2)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(11, 2)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(14, 2)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(0, 3)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(3, 3)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(4, 3)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(7, 3)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(11, 3)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(14, 3)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(0, 4)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(4, 4)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(7, 4)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(9, 4)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(10, 4)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(12, 4)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(13, 4)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(1, 5)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(2, 5)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(3, 5)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(7, 5)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(11, 5)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(14, 5)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(15, 5)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(3, 6)).setCurrentValue(9);
		sudoku16x16.getField(Position.get(5, 6)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(6, 6)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(9, 6)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(11, 6)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(14, 6)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(0, 7)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(3, 7)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(4, 7)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(5, 7)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(8, 7)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(14, 7)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(1, 8)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(7, 8)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(10, 8)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(11, 8)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(12, 8)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(15, 8)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(1, 9)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(4, 9)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(6, 9)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(9, 9)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(10, 9)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(12, 9)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(0, 10)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(1, 10)).setCurrentValue(7);
		sudoku16x16.getField(Position.get(4, 10)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(8, 10)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(12, 10)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(13, 10)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(14, 10)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(2, 11)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(3, 11)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(5, 11)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(6, 11)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(8, 11)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(11, 11)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(15, 11)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(1, 12)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(4, 12)).setCurrentValue(11);
		sudoku16x16.getField(Position.get(8, 12)).setCurrentValue(5);
		sudoku16x16.getField(Position.get(11, 12)).setCurrentValue(3);
		sudoku16x16.getField(Position.get(12, 12)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(15, 12)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(1, 13)).setCurrentValue(6);
		sudoku16x16.getField(Position.get(4, 13)).setCurrentValue(15);
		sudoku16x16.getField(Position.get(6, 13)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(8, 13)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(11, 13)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(14, 13)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(0, 14)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(1, 14)).setCurrentValue(0);
		sudoku16x16.getField(Position.get(2, 14)).setCurrentValue(14);
		sudoku16x16.getField(Position.get(3, 14)).setCurrentValue(8);
		sudoku16x16.getField(Position.get(6, 14)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(9, 14)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(13, 14)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(1, 15)).setCurrentValue(13);
		sudoku16x16.getField(Position.get(5, 15)).setCurrentValue(10);
		sudoku16x16.getField(Position.get(7, 15)).setCurrentValue(1);
		sudoku16x16.getField(Position.get(10, 15)).setCurrentValue(12);
		sudoku16x16.getField(Position.get(11, 15)).setCurrentValue(2);
		sudoku16x16.getField(Position.get(12, 15)).setCurrentValue(4);
		sudoku16x16.getField(Position.get(15, 15)).setCurrentValue(11);

		sudoku16x16.setComplexity(Complexity.arbitrary);
		Solver solver = new Solver(sudoku16x16);
		assertEquals(solver.validate(solution16x16, false), ComplexityRelation.CONSTRAINT_SATURATION);

		// copy solution to current value
		for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
			for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
				sudoku16x16.getField(Position.get(i, j)).setCurrentValue(solution16x16.get(Position.get(i, j)));
			}
		}

		// check constraints
		for (Constraint c : sudoku16x16.getSudokuType()) {
			assertTrue(c.isSaturated(sudoku16x16));
		}

		// print solution if wanted
		System.out.println("Solution (16x16) - Complexity: " + solver.sudoku.getComplexityValue());
		if (PRINT_SOLUTIONS) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
				for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
					int value = sudoku16x16.getField(Position.get(i, j)).getCurrentValue();
					String op = value + "";
					if (value < 10)
						op = " " + value;
					if (value == -1)
						op = " x";
					sb.append(op + ", ");
				}
				sb.append("\n");
			}
			System.out.println(sb);
		}
	}

	@Test
	public void testNoConstraintSaturation() {
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(1, 0)).setCurrentValue(0);

		sudoku.setComplexity(Complexity.arbitrary);
		Solver solver = new Solver(sudoku);
		assertEquals(solver.validate(null, false), ComplexityRelation.INVALID);
	}

}

// TEMPLATE 16x16
/*
 * sudoku16x16.getField(Position.get(0, 0)).setCurrentValue(0); sudoku16x16.getField(Position.get(1,
 * 0)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 0)).setCurrentValue(7); sudoku16x16.getField(new
 * Position(3, 0)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 0)).setCurrentValue(5);
 * sudoku16x16.getField(Position.get(5, 0)).setCurrentValue(12); sudoku16x16.getField(Position.get(6,
 * 0)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 0)).setCurrentValue(13); sudoku16x16.getField(new
 * Position(8, 0)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 0)).setCurrentValue(15);
 * sudoku16x16.getField(Position.get(10, 0)).setCurrentValue(8); sudoku16x16.getField(Position.get(11,
 * 0)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 0)).setCurrentValue(11); sudoku16x16.getField(new
 * Position(13, 0)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 0)).setCurrentValue(9);
 * sudoku16x16.getField(Position.get(15, 0)).setCurrentValue(4); sudoku16x16.getField(Position.get(0,
 * 1)).setCurrentValue(0); sudoku16x16.getField(Position.get(1, 1)).setCurrentValue(6); sudoku16x16.getField(new
 * Position(2, 1)).setCurrentValue(7); sudoku16x16.getField(Position.get(3, 1)).setCurrentValue(1);
 * sudoku16x16.getField(Position.get(4, 1)).setCurrentValue(5); sudoku16x16.getField(Position.get(5,
 * 1)).setCurrentValue(12); sudoku16x16.getField(Position.get(6, 1)).setCurrentValue(3); sudoku16x16.getField(new
 * Position(7, 1)).setCurrentValue(13); sudoku16x16.getField(Position.get(8, 1)).setCurrentValue(10);
 * sudoku16x16.getField(Position.get(9, 1)).setCurrentValue(15); sudoku16x16.getField(Position.get(10,
 * 1)).setCurrentValue(8); sudoku16x16.getField(Position.get(11, 1)).setCurrentValue(14); sudoku16x16.getField(new
 * Position(12, 1)).setCurrentValue(11); sudoku16x16.getField(Position.get(13, 1)).setCurrentValue(2);
 * sudoku16x16.getField(Position.get(14, 1)).setCurrentValue(9); sudoku16x16.getField(Position.get(15,
 * 1)).setCurrentValue(4); sudoku16x16.getField(Position.get(0, 2)).setCurrentValue(0); sudoku16x16.getField(new
 * Position(1, 2)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 2)).setCurrentValue(7);
 * sudoku16x16.getField(Position.get(3, 2)).setCurrentValue(1); sudoku16x16.getField(Position.get(4,
 * 2)).setCurrentValue(5); sudoku16x16.getField(Position.get(5, 2)).setCurrentValue(12); sudoku16x16.getField(new
 * Position(6, 2)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 2)).setCurrentValue(13);
 * sudoku16x16.getField(Position.get(8, 2)).setCurrentValue(10); sudoku16x16.getField(Position.get(9,
 * 2)).setCurrentValue(15); sudoku16x16.getField(Position.get(10, 2)).setCurrentValue(8); sudoku16x16.getField(new
 * Position(11, 2)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 2)).setCurrentValue(11);
 * sudoku16x16.getField(Position.get(13, 2)).setCurrentValue(2); sudoku16x16.getField(Position.get(14,
 * 2)).setCurrentValue(9); sudoku16x16.getField(Position.get(15, 2)).setCurrentValue(4); sudoku16x16.getField(new
 * Position(0, 3)).setCurrentValue(0); sudoku16x16.getField(Position.get(1, 3)).setCurrentValue(6);
 * sudoku16x16.getField(Position.get(2, 3)).setCurrentValue(7); sudoku16x16.getField(Position.get(3,
 * 3)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 3)).setCurrentValue(5); sudoku16x16.getField(new
 * Position(5, 3)).setCurrentValue(12); sudoku16x16.getField(Position.get(6, 3)).setCurrentValue(3);
 * sudoku16x16.getField(Position.get(7, 3)).setCurrentValue(13); sudoku16x16.getField(Position.get(8,
 * 3)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 3)).setCurrentValue(15); sudoku16x16.getField(new
 * Position(10, 3)).setCurrentValue(8); sudoku16x16.getField(Position.get(11, 3)).setCurrentValue(14);
 * sudoku16x16.getField(Position.get(12, 3)).setCurrentValue(11); sudoku16x16.getField(Position.get(13,
 * 3)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 3)).setCurrentValue(9); sudoku16x16.getField(new
 * Position(15, 3)).setCurrentValue(4); sudoku16x16.getField(Position.get(0, 4)).setCurrentValue(0);
 * sudoku16x16.getField(Position.get(1, 4)).setCurrentValue(6); sudoku16x16.getField(Position.get(2,
 * 4)).setCurrentValue(7); sudoku16x16.getField(Position.get(3, 4)).setCurrentValue(1); sudoku16x16.getField(new
 * Position(4, 4)).setCurrentValue(5); sudoku16x16.getField(Position.get(5, 4)).setCurrentValue(12);
 * sudoku16x16.getField(Position.get(6, 4)).setCurrentValue(3); sudoku16x16.getField(Position.get(7,
 * 4)).setCurrentValue(13); sudoku16x16.getField(Position.get(8, 4)).setCurrentValue(10); sudoku16x16.getField(new
 * Position(9, 4)).setCurrentValue(15); sudoku16x16.getField(Position.get(10, 4)).setCurrentValue(8);
 * sudoku16x16.getField(Position.get(11, 4)).setCurrentValue(14); sudoku16x16.getField(Position.get(12,
 * 4)).setCurrentValue(11); sudoku16x16.getField(Position.get(13, 4)).setCurrentValue(2); sudoku16x16.getField(new
 * Position(14, 4)).setCurrentValue(9); sudoku16x16.getField(Position.get(15, 4)).setCurrentValue(4);
 * sudoku16x16.getField(Position.get(0, 5)).setCurrentValue(0); sudoku16x16.getField(Position.get(1,
 * 5)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 5)).setCurrentValue(7); sudoku16x16.getField(new
 * Position(3, 5)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 5)).setCurrentValue(5);
 * sudoku16x16.getField(Position.get(5, 5)).setCurrentValue(12); sudoku16x16.getField(Position.get(6,
 * 5)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 5)).setCurrentValue(13); sudoku16x16.getField(new
 * Position(8, 5)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 5)).setCurrentValue(15);
 * sudoku16x16.getField(Position.get(10, 5)).setCurrentValue(8); sudoku16x16.getField(Position.get(11,
 * 5)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 5)).setCurrentValue(11); sudoku16x16.getField(new
 * Position(13, 5)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 5)).setCurrentValue(9);
 * sudoku16x16.getField(Position.get(15, 5)).setCurrentValue(4); sudoku16x16.getField(Position.get(0,
 * 6)).setCurrentValue(0); sudoku16x16.getField(Position.get(1, 6)).setCurrentValue(6); sudoku16x16.getField(new
 * Position(2, 6)).setCurrentValue(7); sudoku16x16.getField(Position.get(3, 6)).setCurrentValue(1);
 * sudoku16x16.getField(Position.get(4, 6)).setCurrentValue(5); sudoku16x16.getField(Position.get(5,
 * 6)).setCurrentValue(12); sudoku16x16.getField(Position.get(6, 6)).setCurrentValue(3); sudoku16x16.getField(new
 * Position(7, 6)).setCurrentValue(13); sudoku16x16.getField(Position.get(8, 6)).setCurrentValue(10);
 * sudoku16x16.getField(Position.get(9, 6)).setCurrentValue(15); sudoku16x16.getField(Position.get(10,
 * 6)).setCurrentValue(8); sudoku16x16.getField(Position.get(11, 6)).setCurrentValue(14); sudoku16x16.getField(new
 * Position(12, 6)).setCurrentValue(11); sudoku16x16.getField(Position.get(13, 6)).setCurrentValue(2);
 * sudoku16x16.getField(Position.get(14, 6)).setCurrentValue(9); sudoku16x16.getField(Position.get(15,
 * 6)).setCurrentValue(4); sudoku16x16.getField(Position.get(0, 7)).setCurrentValue(0); sudoku16x16.getField(new
 * Position(1, 7)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 7)).setCurrentValue(7);
 * sudoku16x16.getField(Position.get(3, 7)).setCurrentValue(1); sudoku16x16.getField(Position.get(4,
 * 7)).setCurrentValue(5); sudoku16x16.getField(Position.get(5, 7)).setCurrentValue(12); sudoku16x16.getField(new
 * Position(6, 7)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 7)).setCurrentValue(13);
 * sudoku16x16.getField(Position.get(8, 7)).setCurrentValue(10); sudoku16x16.getField(Position.get(9,
 * 7)).setCurrentValue(15); sudoku16x16.getField(Position.get(10, 7)).setCurrentValue(8); sudoku16x16.getField(new
 * Position(11, 7)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 7)).setCurrentValue(11);
 * sudoku16x16.getField(Position.get(13, 7)).setCurrentValue(2); sudoku16x16.getField(Position.get(14,
 * 7)).setCurrentValue(9); sudoku16x16.getField(Position.get(15, 7)).setCurrentValue(4); sudoku16x16.getField(new
 * Position(0, 8)).setCurrentValue(0); sudoku16x16.getField(Position.get(1, 8)).setCurrentValue(6);
 * sudoku16x16.getField(Position.get(2, 8)).setCurrentValue(7); sudoku16x16.getField(Position.get(3,
 * 8)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 8)).setCurrentValue(5); sudoku16x16.getField(new
 * Position(5, 8)).setCurrentValue(12); sudoku16x16.getField(Position.get(6, 8)).setCurrentValue(3);
 * sudoku16x16.getField(Position.get(7, 8)).setCurrentValue(13); sudoku16x16.getField(Position.get(8,
 * 8)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 8)).setCurrentValue(15); sudoku16x16.getField(new
 * Position(10, 8)).setCurrentValue(8); sudoku16x16.getField(Position.get(11, 8)).setCurrentValue(14);
 * sudoku16x16.getField(Position.get(12, 8)).setCurrentValue(11); sudoku16x16.getField(Position.get(13,
 * 8)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 8)).setCurrentValue(9); sudoku16x16.getField(new
 * Position(15, 8)).setCurrentValue(4); sudoku16x16.getField(Position.get(0, 9)).setCurrentValue(0);
 * sudoku16x16.getField(Position.get(1, 9)).setCurrentValue(6); sudoku16x16.getField(Position.get(2,
 * 9)).setCurrentValue(7); sudoku16x16.getField(Position.get(3, 9)).setCurrentValue(1); sudoku16x16.getField(new
 * Position(4, 9)).setCurrentValue(5); sudoku16x16.getField(Position.get(5, 9)).setCurrentValue(12);
 * sudoku16x16.getField(Position.get(6, 9)).setCurrentValue(3); sudoku16x16.getField(Position.get(7,
 * 9)).setCurrentValue(13); sudoku16x16.getField(Position.get(8, 9)).setCurrentValue(10); sudoku16x16.getField(new
 * Position(9, 9)).setCurrentValue(15); sudoku16x16.getField(Position.get(10, 9)).setCurrentValue(8);
 * sudoku16x16.getField(Position.get(11, 9)).setCurrentValue(14); sudoku16x16.getField(Position.get(12,
 * 9)).setCurrentValue(11); sudoku16x16.getField(Position.get(13, 9)).setCurrentValue(2); sudoku16x16.getField(new
 * Position(14, 9)).setCurrentValue(9); sudoku16x16.getField(Position.get(15, 9)).setCurrentValue(4);
 * sudoku16x16.getField(Position.get(0, 10)).setCurrentValue(0); sudoku16x16.getField(Position.get(1,
 * 10)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 10)).setCurrentValue(7); sudoku16x16.getField(new
 * Position(3, 10)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 10)).setCurrentValue(5);
 * sudoku16x16.getField(Position.get(5, 10)).setCurrentValue(12); sudoku16x16.getField(Position.get(6,
 * 10)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 10)).setCurrentValue(13); sudoku16x16.getField(new
 * Position(8, 10)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 10)).setCurrentValue(15);
 * sudoku16x16.getField(Position.get(10, 10)).setCurrentValue(8); sudoku16x16.getField(Position.get(11,
 * 10)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 10)).setCurrentValue(11); sudoku16x16.getField(new
 * Position(13, 10)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 10)).setCurrentValue(9);
 * sudoku16x16.getField(Position.get(15, 10)).setCurrentValue(4); sudoku16x16.getField(Position.get(0,
 * 11)).setCurrentValue(0); sudoku16x16.getField(Position.get(1, 11)).setCurrentValue(6); sudoku16x16.getField(new
 * Position(2, 11)).setCurrentValue(7); sudoku16x16.getField(Position.get(3, 11)).setCurrentValue(1);
 * sudoku16x16.getField(Position.get(4, 11)).setCurrentValue(5); sudoku16x16.getField(Position.get(5,
 * 11)).setCurrentValue(12); sudoku16x16.getField(Position.get(6, 11)).setCurrentValue(3); sudoku16x16.getField(new
 * Position(7, 11)).setCurrentValue(13); sudoku16x16.getField(Position.get(8, 11)).setCurrentValue(10);
 * sudoku16x16.getField(Position.get(9, 11)).setCurrentValue(15); sudoku16x16.getField(Position.get(10,
 * 11)).setCurrentValue(8); sudoku16x16.getField(Position.get(11, 11)).setCurrentValue(14); sudoku16x16.getField(new
 * Position(12, 11)).setCurrentValue(11); sudoku16x16.getField(Position.get(13, 11)).setCurrentValue(2);
 * sudoku16x16.getField(Position.get(14, 11)).setCurrentValue(9); sudoku16x16.getField(Position.get(15,
 * 11)).setCurrentValue(4); sudoku16x16.getField(Position.get(0, 12)).setCurrentValue(0); sudoku16x16.getField(new
 * Position(1, 12)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 12)).setCurrentValue(7);
 * sudoku16x16.getField(Position.get(3, 12)).setCurrentValue(1); sudoku16x16.getField(Position.get(4,
 * 12)).setCurrentValue(5); sudoku16x16.getField(Position.get(5, 12)).setCurrentValue(12); sudoku16x16.getField(new
 * Position(6, 12)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 12)).setCurrentValue(13);
 * sudoku16x16.getField(Position.get(8, 12)).setCurrentValue(10); sudoku16x16.getField(Position.get(9,
 * 12)).setCurrentValue(15); sudoku16x16.getField(Position.get(10, 12)).setCurrentValue(8); sudoku16x16.getField(new
 * Position(11, 12)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 12)).setCurrentValue(11);
 * sudoku16x16.getField(Position.get(13, 12)).setCurrentValue(2); sudoku16x16.getField(Position.get(14,
 * 12)).setCurrentValue(9); sudoku16x16.getField(Position.get(15, 12)).setCurrentValue(4); sudoku16x16.getField(new
 * Position(0, 13)).setCurrentValue(0); sudoku16x16.getField(Position.get(1, 13)).setCurrentValue(6);
 * sudoku16x16.getField(Position.get(2, 13)).setCurrentValue(7); sudoku16x16.getField(Position.get(3,
 * 13)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 13)).setCurrentValue(5); sudoku16x16.getField(new
 * Position(5, 13)).setCurrentValue(12); sudoku16x16.getField(Position.get(6, 13)).setCurrentValue(3);
 * sudoku16x16.getField(Position.get(7, 13)).setCurrentValue(13); sudoku16x16.getField(Position.get(8,
 * 13)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 13)).setCurrentValue(15); sudoku16x16.getField(new
 * Position(10, 13)).setCurrentValue(8); sudoku16x16.getField(Position.get(11, 13)).setCurrentValue(14);
 * sudoku16x16.getField(Position.get(12, 13)).setCurrentValue(11); sudoku16x16.getField(Position.get(13,
 * 13)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 13)).setCurrentValue(9); sudoku16x16.getField(new
 * Position(15, 13)).setCurrentValue(4); sudoku16x16.getField(Position.get(0, 14)).setCurrentValue(0);
 * sudoku16x16.getField(Position.get(1, 14)).setCurrentValue(6); sudoku16x16.getField(Position.get(2,
 * 14)).setCurrentValue(7); sudoku16x16.getField(Position.get(3, 14)).setCurrentValue(1); sudoku16x16.getField(new
 * Position(4, 14)).setCurrentValue(5); sudoku16x16.getField(Position.get(5, 14)).setCurrentValue(12);
 * sudoku16x16.getField(Position.get(6, 14)).setCurrentValue(3); sudoku16x16.getField(Position.get(7,
 * 14)).setCurrentValue(13); sudoku16x16.getField(Position.get(8, 14)).setCurrentValue(10); sudoku16x16.getField(new
 * Position(9, 14)).setCurrentValue(15); sudoku16x16.getField(Position.get(10, 14)).setCurrentValue(8);
 * sudoku16x16.getField(Position.get(11, 14)).setCurrentValue(14); sudoku16x16.getField(Position.get(12,
 * 14)).setCurrentValue(11); sudoku16x16.getField(Position.get(13, 14)).setCurrentValue(2); sudoku16x16.getField(new
 * Position(14, 14)).setCurrentValue(9); sudoku16x16.getField(Position.get(15, 14)).setCurrentValue(4);
 * sudoku16x16.getField(Position.get(0, 15)).setCurrentValue(0); sudoku16x16.getField(Position.get(1,
 * 15)).setCurrentValue(6); sudoku16x16.getField(Position.get(2, 15)).setCurrentValue(7); sudoku16x16.getField(new
 * Position(3, 15)).setCurrentValue(1); sudoku16x16.getField(Position.get(4, 15)).setCurrentValue(5);
 * sudoku16x16.getField(Position.get(5, 15)).setCurrentValue(12); sudoku16x16.getField(Position.get(6,
 * 15)).setCurrentValue(3); sudoku16x16.getField(Position.get(7, 15)).setCurrentValue(13); sudoku16x16.getField(new
 * Position(8, 15)).setCurrentValue(10); sudoku16x16.getField(Position.get(9, 15)).setCurrentValue(15);
 * sudoku16x16.getField(Position.get(10, 15)).setCurrentValue(8); sudoku16x16.getField(Position.get(11,
 * 15)).setCurrentValue(14); sudoku16x16.getField(Position.get(12, 15)).setCurrentValue(11); sudoku16x16.getField(new
 * Position(13, 15)).setCurrentValue(2); sudoku16x16.getField(Position.get(14, 15)).setCurrentValue(9);
 * sudoku16x16.getField(Position.get(15, 15)).setCurrentValue(4);
 */
