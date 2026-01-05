package de.sudoq.model.solverGenerator.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static de.sudoq.model.sudoku.sudokuTypes.SudokuTypes.standard9x9;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;


import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

class SolverTests {

	private Sudoku sudoku;
	private Sudoku sudoku16x16;
	private Solver solver;
	private PositionMap<Integer> solution16x16;

	private static final boolean PRINT_SOLUTIONS = false;

	private IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo4Tests();


    @BeforeEach
    void before() {
		sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku();
		sudoku.setComplexity(Complexity.arbitrary);
		solver = new Solver(sudoku);
		sudoku16x16 = new SudokuBuilder(SudokuTypes.standard16x16, sudokuTypeRepo).createSudoku();
		sudoku16x16.setComplexity(Complexity.arbitrary);
		solution16x16 = new PositionMap<Integer>(sudoku16x16.getSudokuType().getSize());
	}

    @Test
    void test1(){
        Sudoku initialSudoku = new Sudoku(sudokuTypeRepo.read(standard9x9.ordinal()));
        for (int i=0; i < 8; i++)
            initialSudoku.getCell(Position.get(i,0)).setCurrentValue(i);
        Solver solver = new Solver(initialSudoku);
        solver.solveAll(true, false, true);
        List<Solution> ls = solver.getSolutions();
        System.out.println(ls.getFirst());
    }

	/** convenience init sudoku	 */
	private void initSudoku9x9(Sudoku sudoku){
		    
		String s= "0    52  "+
		          " 2  0  3 "+
		          "  84    6"+
		          "  52     "+
		          " 1  7    "+
		          "6    3   "+
		          "  48    2"+
		          "8     0  "+
		          " 7  1  6 ";

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
	}

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void solveOneAutomaticallyApplied() {
        Sudoku sudoku = new Sudoku(sudokuTypeRepo.read(standard9x9.ordinal()));
        initSudoku9x9(sudoku);
        Solver solver = new Solver(sudoku);
        SolverSudoku solverSudoku = solver.solverSudoku;
        Solution solution = solver.solveOne(true);
        while (solution != null && solution.getAction() != null) {
            System.out.println("loop in test");
            SolveDerivation sd = solution.getDerivations().getLast();
            Cell c = solverSudoku.getCell(sd.getCellIterator().next().getPosition());
            assertNotEquals(Cell.EMPTYVAL, c.getCurrentValue());
            solution = solver.solveOne(true);
        }

        // after solving everything, every cell should be filled
        for (Cell f : solverSudoku) {
            assertNotEquals(Cell.EMPTYVAL, f.getCurrentValue());
        }
    }


    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void solveOneManuallyApplied() {
        Sudoku sudoku = new Sudoku(sudokuTypeRepo.read(standard9x9.ordinal()));
        initSudoku9x9(sudoku);
        Solver solver = new Solver(sudoku);
        SolverSudoku solverSudoku = solver.solverSudoku;
        Solution solution = solver.solveOne(false);
        while (solution != null && solution.getAction() != null) {
            SolveDerivation sd = solution.getDerivations().getLast();
            solution.getAction().execute();
            Cell c = solverSudoku.getCell(sd.getCellIterator().next().getPosition());
            assertNotEquals(Cell.EMPTYVAL, c.getCurrentValue());
            solution = solver.solveOne(false);
        }

        for (Cell f : solverSudoku) {
            assertNotEquals(Cell.EMPTYVAL, f.getCurrentValue());
        }
    }

    /**
     * Unique behaviour constraint is expected to fail because of 2 zeroes in the first row
     */
    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void solveOneIncorrect() {
        // GIVEN
        Sudoku initialSudoku = new Sudoku(sudokuTypeRepo.read(standard9x9.ordinal()));
        for (int i=0; i < 8; i++)
            initialSudoku.getCell(Position.get(i,0)).setCurrentValue(i);
        initialSudoku.getCell(Position.get(1,0)).setCurrentValue(0);//set a second cell to 0
        Solver solver = new Solver(initialSudoku);

        // WHEN
        Solution solution = solver.solveOne(true);

        // THEN
        assertNull(solution);
	}

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void solveAll() {
        Sudoku sudoku = new Sudoku(sudokuTypeRepo.read(standard9x9.ordinal()));
        initSudoku9x9(sudoku);
        Solver solver = new Solver(sudoku);
        SolverSudoku solverSudoku = solver.solverSudoku;

        solver.solveAll(true, false, false);
        List<Solution> solutions = solver.getSolutions();
        for (Solution solution : solutions) {
            for (SolveDerivation sd: solution.getDerivations())
                assertNotNull(sd);
        }

        for (Cell f : solverSudoku)
            assertNotEquals(Cell.EMPTYVAL, f.getCurrentValue());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void solveAllIncorrect() {
        // GIVEN
        Sudoku initialSudoku = new Sudoku(sudokuTypeRepo.read(standard9x9.ordinal()));
        for (int i=0; i < 8; i++)
            initialSudoku.getCell(Position.get(i,0)).setCurrentValue(i);
        initialSudoku.getCell(Position.get(1,0)).setCurrentValue(0);//set a second cell to 0
        Solver solver = new Solver(initialSudoku);

        // WHEN
        boolean response = solver.solveAll(true, false);

        // THEN
        assertFalse(response);
    }

    @Test
    void solveAllIllegalComplexity() {
		solver.solverSudoku.setComplexity(null);
		assertThrows(IllegalArgumentException.class, () -> solver.validate(null));
	}

    @Test
    void nullSudoku() {
		assertThrows(NullPointerException.class, () -> new Solver(null));
	}

    @Test
    void hashing() {
		Map<Position, Integer> map = new HashMap<Position, Integer>();
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				map.put(Position.get(i, j), 16 * i + j);
			}
		}

		int count = 0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				assertEquals(map.get(Position.get(i, j)), Integer.valueOf(count));
				count++;
			}
		}
	}

    @Test
    void standard16x16() {
        List<String> pattern = Arrays.asList(//
                "__F_9_C734_5AE__",//
                "C___5______BD93_",//
                "_63A0_E_9__D5B_1",//
                "458_3D___2_0F_C_",//

                "F_5__46__D_3_2__",//
                "8B___C__2__41___",//
                "2__CE__A_0__3__9",//
                "391E_2__A786____",//

                "BE75_03___D_____",//
                "_8C_2______7__1_",//
                "_A9__1FD5B28E___",//
                "___D___B_C_____6",//

                "E_D_8_1_6A___5_F",//
                "__0_A9______2___",//
                "______D_____6C7A",//
                "___1_B_2_F__0_E_"
        );
        parse16x16(pattern);

		sudoku16x16.setComplexity(Complexity.arbitrary);
		Solver solver = new Solver(sudoku16x16);
		ComplexityRelation cr = solver.validate(solution16x16);
		//assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, cr); todo fix complexity determiation

		// copy solution to current value
		for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
			for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
				sudoku16x16.getCell(Position.get(i, j)).setCurrentValue(solution16x16.get(Position.get(i, j)));
			}
		}

		// check constraints
		for (Constraint c : sudoku16x16.getSudokuType()) {
			assertTrue(c.isSaturated(sudoku16x16));
		}

		System.out.println("Solution (16x16) - Complexity: " + solver.solverSudoku.getComplexityValue());
		if (PRINT_SOLUTIONS) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
				for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
					int value = sudoku16x16.getCell(Position.get(i, j)).getCurrentValue();
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

    private void parse16x16(List<String> pattern) {
        for(int row = 0; row < pattern.size(); row++) {
            String rowS = pattern.get(row);
            for(int col = 0; col < rowS.length(); col++) {
                char c = rowS.charAt(col);
                if (c == '_')
                    continue;
                solution16x16.put(Position.get(col, row), parseHex(c));
            }
        }
    }

    private static int parseHex(char c) {
            if ('0' <= c && c <= '9')
                return c - '0';
            else if ('A' <= c && c <= 'F'){
                return c - 'A';
            } else throw new IllegalArgumentException("illegal character: " + c);
    }

    @Test
    void standard16x16No2() {
        List<String> pattern = Arrays.asList(//
                "0__123__B_5___6_",
                "__7___6__2__895A",
                "_B__9__0_C_A__D_",
                "2__E1__D___8__B_",

                "C___7__9_B1_0E__",
                "A65____F___E__4C",
                "___9_4E__3_7__A_",
                "F__48B__0_____7_",

                "_1_____C__B47__2",
                "_C__E_2__D7_F___",
                "47__0___1___C8E_",
                "__B3_5F_C__6___4",

                "_2__B___5__3A__F",
                "_6__F_4_D__0__1_",
                "A0E8__C__1___D__",
                "_D___A_1__C24__B"
        );
        parse16x16(pattern);

		sudoku16x16.setComplexity(Complexity.arbitrary);
		Solver solver = new Solver(sudoku16x16);
        ComplexityRelation cr = solver.validate(solution16x16);
        //assertEquals(ComplexityRelation.CONSTRAINT_SATURATION, cr);

		// copy solution to current value
		for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
			for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
				sudoku16x16.getCell(Position.get(i, j)).setCurrentValue(solution16x16.get(Position.get(i, j)));
			}
		}

		// check constraints
		for (Constraint c : sudoku16x16.getSudokuType()) {
			assertTrue(c.isSaturated(sudoku16x16));
		}

		// print solution if wanted
		System.out.println("Solution (16x16) - Complexity: " + solver.solverSudoku.getComplexityValue());
		if (PRINT_SOLUTIONS) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < sudoku16x16.getSudokuType().getSize().getY(); j++) {
				for (int i = 0; i < sudoku16x16.getSudokuType().getSize().getX(); i++) {
					int value = sudoku16x16.getCell(Position.get(i, j)).getCurrentValue();
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
    void noConstraintSaturation() {
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(0);

		sudoku.setComplexity(Complexity.arbitrary);
		Solver solver = new Solver(sudoku);
		assertEquals(ComplexityRelation.INVALID, solver.validate(null));
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
