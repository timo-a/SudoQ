package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SolverIntegrationTests {

	private Sudoku sudoku;
	private Sudoku sudoku16x16;
	private Solver solver;
	private PositionMap<Integer> solution;

	private static final boolean PRINT_SOLUTIONS = false ;

	//this is a dummy so it compiles todo use xmls from resources
	private IRepo<SudokuTypeBE> sudokuTypeRepo = new IRepo<SudokuTypeBE>() {
		@Override
		public void delete(int id) { throw new NotImplementedException(); }

		@Override
		public SudokuTypeBE update(SudokuTypeBE sudokuBE) { throw new NotImplementedException(); }

		@Override
		public SudokuTypeBE read(int id) {
			throw new NotImplementedException();
		}

		@Override
		public SudokuTypeBE create() { throw new NotImplementedException(); }

	};

	@Before
	public void before() {
		Utility.copySudokus();

		sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku();
		sudoku.setComplexity(Complexity.arbitrary);
		solver = new Solver(sudoku);
		sudoku16x16 = new SudokuBuilder(SudokuTypes.standard16x16, sudokuTypeRepo).createSudoku();
		sudoku16x16.setComplexity(Complexity.arbitrary);
		solution = new PositionMap<Integer>(sudoku.getSudokuType().getSize());
	}

	@Test(timeout = 3_000)
	public void testEasySudoku1() {
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 1)).setCurrentValue(5);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(7);
		sudoku.getCell(Position.get(4, 2)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(8);
		sudoku.getCell(Position.get(8, 2)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(0, 4)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(4);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(0);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 7)).setCurrentValue(5);
		sudoku.getCell(Position.get(8, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(0);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Easy 1) - Complexity: ");
	}

	private void skeleton(String title){

        Position size = sudoku.getSudokuType().getSize();

        // copy solution to current value
		for (int j = 0; j < size.getY(); j++) {
			for (int i = 0; i < size.getX(); i++) {
				sudoku.getCell(Position.get(i, j)).setCurrentValue(solution.get(Position.get(i, j)));
			}
		}

        // check constraints
		for (Constraint c : sudoku.getSudokuType()) {
			assertTrue(c.isSaturated(sudoku));
		}

        // print solution if wanted
        System.out.println(title + solver.solverSudoku.getComplexityValue());
		if (PRINT_SOLUTIONS) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < size.getY(); j++) {
				for (int i = 0; i < size.getX(); i++) {
					int value = sudoku.getCell(Position.get(i, j)).getCurrentValue();
					String op = value + "";
					if (String.valueOf(value).length() < 2)
						op = " " + value;
					if (value == -1)
						op = "--";
					sb.append(op).append(", ");
				}
				sb.append("\n");
			}
			System.out.println(sb);
		}

	}

	@Test(timeout = 3_000)
	public void testEasySudoku2() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(3);
		sudoku.getCell(Position.get(0, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 6)).setCurrentValue(0);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(3);
		sudoku.getCell(Position.get(1, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(2, 4)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 4)).setCurrentValue(2);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(2);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(4);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 7)).setCurrentValue(6);
		sudoku.getCell(Position.get(7, 8)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(0);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);
		solver.solverSudoku.setComplexity(Complexity.difficult);
		assertEquals(solver.validate(solution), ComplexityRelation.MUCH_TOO_EASY);

		skeleton("Solution (Easy 2) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testEasySudoku3() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(7);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(3, 1)).setCurrentValue(3);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 2)).setCurrentValue(8);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(7);
		sudoku.getCell(Position.get(8, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(0, 3)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(5);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(0, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(3);
		sudoku.getCell(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 5)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(8);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(1);
		sudoku.getCell(Position.get(3, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(0);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Easy 3) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testMediumSudoku1() {
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(1, 1)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(3, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(5, 2)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(0, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(5, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(2, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(2);
		sudoku.getCell(Position.get(6, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(3);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(3, 5)).setCurrentValue(0);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 7)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 7)).setCurrentValue(8);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(7);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Medium 1) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testMediumSudoku2() {
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(7);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(8, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(1, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(3, 4)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 5)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(8, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(3);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(0);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(2, 8)).setCurrentValue(3);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(5);
		sudoku.getCell(Position.get(8, 8)).setCurrentValue(1);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Medium 2) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testMediumSudoku3() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(5, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(1);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 4)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 4)).setCurrentValue(7);
		sudoku.getCell(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(3);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(7);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 7)).setCurrentValue(6);
		sudoku.getCell(Position.get(1, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 8)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 8)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 8)).setCurrentValue(2);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Medium 3) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testMediumSudoku4() {
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(5, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 1)).setCurrentValue(5);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(0);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(4, 3)).setCurrentValue(6);
		sudoku.getCell(Position.get(7, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 4)).setCurrentValue(7);
		sudoku.getCell(Position.get(0, 5)).setCurrentValue(4);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(8);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(5, 7)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(3);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);
		solver.solverSudoku.setComplexity(Complexity.easy);
		assertEquals(solver.validate(solution), ComplexityRelation.MUCH_TOO_DIFFICULT);

		skeleton("Solution (Medium 4) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testDifficultSudoku1() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 1)).setCurrentValue(8);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(6, 5)).setCurrentValue(4);
		sudoku.getCell(Position.get(8, 5)).setCurrentValue(6);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(7);
		sudoku.getCell(Position.get(5, 7)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 8)).setCurrentValue(2);

		assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, solver.validate(solution));

		skeleton("Solution (Difficult 1) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testDifficultSudoku2() {
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(7);
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(3, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 5)).setCurrentValue(6);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 6)).setCurrentValue(7);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(7);

		assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, solver.validate(solution));

		skeleton("Solution (Difficult 2) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testDifficultSudoku3() {
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 0)).setCurrentValue(5);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(7);
		sudoku.getCell(Position.get(8, 7)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(1);
		sudoku.getCell(Position.get(1, 8)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(8);

		assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, solver.validate(solution));

		skeleton("Solution (Difficult 3) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testDifficultSudoku4() {
		sudoku.getCell(Position.get(4, 0)).setCurrentValue(7);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(8);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 5)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(7);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 7)).setCurrentValue(6);
		sudoku.getCell(Position.get(1, 8)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 8)).setCurrentValue(6);

		assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, solver.validate(solution));
		solver.solverSudoku.setComplexity(Complexity.infernal);
		// assertEquals(solver.validate(solution), ComplexityRelation.TOO_EASY);

		skeleton("Solution (Difficult 4) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testDifficultSudoku5() {
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(1, 1)).setCurrentValue(6);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(2);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 3)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(8);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(2, 8)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(0);
		sudoku.getCell(Position.get(7, 8)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 8)).setCurrentValue(1);

		assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, solver.validate(solution));
		solver.solverSudoku.setComplexity(Complexity.easy);
		assertEquals(ComplexityRelation.TOO_DIFFICULT, solver.validate(solution));

		skeleton("Solution (Difficult 5) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testInfernalSudoku1() {
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(0);

		assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, solver.validate(solution));

		skeleton("Solution (Infernal 1) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testInfernalSudoku2() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 0)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(8);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(8);
		sudoku.getCell(Position.get(7, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(3);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 6)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 7)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(4);
		sudoku.getCell(Position.get(0, 8)).setCurrentValue(0);
		sudoku.getCell(Position.get(2, 8)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(5, 8)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 8)).setCurrentValue(5);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Infernal 2) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testInfernalSudoku3() {
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 1)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 2)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(1);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(8, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(2, 4)).setCurrentValue(7);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 4)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(3, 7)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 8)).setCurrentValue(6);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (Infernal 3) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testWorldsHardestSudoku() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(7, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(4, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(2, 2)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(2, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(7);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(0, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 6)).setCurrentValue(0);
		sudoku.getCell(Position.get(1, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 7)).setCurrentValue(6);
		sudoku.getCell(Position.get(2, 8)).setCurrentValue(6);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(2);

		solver.solverSudoku.setComplexity(Complexity.easy);
		assertEquals(solver.validate(solution), ComplexityRelation.INVALID);
		solver.solverSudoku.setComplexity(Complexity.arbitrary);
		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (world's hardest) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testWorldsHardestSudoku2() {
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(7);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(1);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(6);
		sudoku.getCell(Position.get(4, 2)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(0, 3)).setCurrentValue(3);
		sudoku.getCell(Position.get(5, 3)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(6);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 5)).setCurrentValue(2);
		sudoku.getCell(Position.get(3, 5)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 5)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(4);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(8);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(7, 7)).setCurrentValue(2);
		sudoku.getCell(Position.get(5, 8)).setCurrentValue(8);
		sudoku.getCell(Position.get(6, 8)).setCurrentValue(6);

		assertEquals(solver.validate(solution), ComplexityRelation.CONSTRAINT_SATURATION);

		skeleton("Solution (world's hardest 2) - Complexity: ");
	}

	@Test(timeout = 3_000)
	public void testNotSolvableSudoku() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(5, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(6, 1)).setCurrentValue(8);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(7, 2)).setCurrentValue(7);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(4, 4)).setCurrentValue(8);
		sudoku.getCell(Position.get(5, 4)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 4)).setCurrentValue(5);
		sudoku.getCell(Position.get(4, 5)).setCurrentValue(5);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(3);
		sudoku.getCell(Position.get(6, 5)).setCurrentValue(4);
		sudoku.getCell(Position.get(8, 5)).setCurrentValue(6);
		sudoku.getCell(Position.get(1, 6)).setCurrentValue(5);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(2);
		sudoku.getCell(Position.get(2, 7)).setCurrentValue(7);
		sudoku.getCell(Position.get(5, 7)).setCurrentValue(5);
		sudoku.getCell(Position.get(6, 7)).setCurrentValue(3);
		sudoku.getCell(Position.get(4, 8)).setCurrentValue(0);
		sudoku.getCell(Position.get(8, 8)).setCurrentValue(4);

		assertEquals(solver.validate(solution), ComplexityRelation.INVALID);

		while (solver.solveOne(true) != null)
			;
		assertFalse(solver.solveOne(true) != null);
	}

	@Test(timeout = 3_000)
	public void testAmbiguouslySolvable() {
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(6);
		sudoku.getCell(Position.get(3, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(6, 0)).setCurrentValue(5);
		sudoku.getCell(Position.get(2, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 1)).setCurrentValue(0);
		sudoku.getCell(Position.get(4, 2)).setCurrentValue(1);
		sudoku.getCell(Position.get(4, 3)).setCurrentValue(0);
		sudoku.getCell(Position.get(5, 3)).setCurrentValue(2);
		sudoku.getCell(Position.get(7, 3)).setCurrentValue(8);
		sudoku.getCell(Position.get(1, 4)).setCurrentValue(4);
		sudoku.getCell(Position.get(5, 5)).setCurrentValue(8);
		sudoku.getCell(Position.get(3, 6)).setCurrentValue(7);
		sudoku.getCell(Position.get(6, 6)).setCurrentValue(3);
		sudoku.getCell(Position.get(8, 6)).setCurrentValue(6);
		sudoku.getCell(Position.get(0, 7)).setCurrentValue(1);
		sudoku.getCell(Position.get(8, 7)).setCurrentValue(4);

		assertEquals(solver.validate(solution), ComplexityRelation.INVALID);
	}

}
