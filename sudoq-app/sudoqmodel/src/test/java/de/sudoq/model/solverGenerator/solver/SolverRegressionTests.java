package de.sudoq.model.solverGenerator.solver;

import org.junit.Before;
import org.junit.Test;


import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

//import de.sudoq.external;

public class SolverRegressionTests {

    private Sudoku sudoku;
    private Sudoku sudoku16x16;
    private Solver solver;
    private PositionMap<Integer> solution16x16;

    private static final boolean PRINT_SOLUTIONS = false;

    @Before
    public void before() {
        FileManagerTests.init();
        sudoku = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
        sudoku.setComplexity(Complexity.arbitrary);
        solver = new Solver(sudoku);
        sudoku16x16 = new SudokuBuilder(SudokuTypes.standard16x16).createSudoku();
        sudoku16x16.setComplexity(Complexity.arbitrary);
        solution16x16 = new PositionMap<Integer>(sudoku16x16.getSudokuType().getSize());
    }


    @Test
    public void testRegression1on9x9() {
        String r2 = ". . 7 9 . 1 8 2 6 \n"
                  + "2 8 4 5 6 7 . . 3 \n"
                  + ". . 1 . . 8 7 . . \n"
                  + "6 . 3 7 . . 2 . . \n"
                  + ". . . . . . 3 6 4 \n"
                  + "4 . 9 . 3 . . . . \n"
                  + "7 9 . . . 3 . . . \n"
                  + "8 3 . 1 . . . . . \n"
                  + "1 4 5 . 7 . . 3 . \n";


        Sudoku s = SudokuMockUps.stringTo9x9Sudoku(r2);
        Solver solver = new Solver(s);

        solver.solveAll(true, false, false);
        System.out.println(solver.getSolutions());
        System.out.println(solver.getHintCountString());
    }
    @Test
    public void testRegression1on16x16() {

        /* I dont know at this point whether this should be solvable because our solver is so slow...
         *
         * */

        //MyClass m = new MyClass();

        String r2 =  ".  .  .  .  .  . 15  .  6  .  4  .  .  .  .  .\n"
                  + " .  .  2  .  .  .  .  3  .  .  9  . 12  .  .  .\n"
                  + " . 13  .  .  . 14 10  .  .  . 15  .  5  .  1  4\n"
                  + " .  .  .  .  .  .  .  6  .  .  5  .  .  .  .  .\n"
                  + " .  9  .  .  .  8  .  .  .  .  . 10  1  .  .  .\n"
                  + " .  .  .  .  .  . 12  2  .  6  .  5  .  8  .  .\n"
                  + " .  .  .  .  .  .  .  1  .  .  .  .  .  . 10  .\n"
                  + "10  .  5  .  .  6  .  .  .  9  .  8  .  .  . 12\n"
                  + " 7  .  .  .  .  .  .  .  3  .  .  .  .  .  .  1\n"
                  + " .  .  .  .  .  .  .  . 11 16  .  .  .  . 12  .\n"
                  + " .  .  .  . 13  .  .  5  .  .  1  .  7  .  .  8\n"
                  + " .  .  . 10 16  7  .  .  .  2  8  .  .  . 13  .\n"
                  + " .  .  .  .  1  .  . 14  .  .  .  .  .  .  .  .\n"
                  + " .  .  .  .  .  .  .  .  . 12  6  .  4 10  . 13\n"
                  + " .  . 13  .  .  . 16  .  .  .  .  .  .  . 14  .\n"
                  + " . 11  .  .  .  .  .  .  . 10  .  .  .  9 15  .\n";

        Sudoku s = SudokuMockUps.stringTo16x16Sudoku(r2);

        boolean q = FastSolverFactory.getSolver(s).isAmbiguous();
        System.out.println(q);

        s.setComplexity(Complexity.easy);
        Solver solver = new Solver(s);

        solver.solveAll(true, false, false);
        System.out.println(solver.getSolutions());
        System.out.println(solver.getHintCountString());
    }



}