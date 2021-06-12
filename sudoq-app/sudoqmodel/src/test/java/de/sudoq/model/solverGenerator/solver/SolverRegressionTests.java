package de.sudoq.model.solverGenerator.solver;

import org.junit.Before;
import org.junit.Test;


import java.io.File;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory;
import de.sudoq.model.sudoku.Position;
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
    private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

    @Before
    public void before() {
        TestWithInitCleanforSingletons.legacyInit();
        sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuDir).createSudoku();
        sudoku.setComplexity(Complexity.arbitrary);
        solver = new Solver(sudoku);
        sudoku16x16 = new SudokuBuilder(SudokuTypes.standard16x16, sudokuDir).createSudoku();
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
    public void testSamurai1(){
        String pattern = ". 7 . . . 2 0 . 8       . 5 . . . . 3 . . "
                + "5 0 . 8 . 3 7 . .       0 . . 4 2 5 8 7 . "
                + "3 8 6 . 0 . . . 5       . . 6 8 3 . . . . "
                + "2 4 3 . 1 . . . .       . 6 5 7 . . 2 3 . "
                + ". . . . . 5 . . 0       . . . 3 4 . . . . "
                + ". . . . . . . . 2       3 . . . . 2 1 0 . "
                + ". 3 . . 7 6 . . 4 6 . . 5 3 . . . . . . . "
                + "7 2 4 . . 8 6 . . 8 5 . . 7 4 0 5 . . . . "
                + "1 . . . . . . . . 4 . . . . . . 7 . . 5 0 "
                + "            . 5 2 . . 1 3 . .             "
                + "            8 . . . 4 . . . 5             "
                + "            4 . 6 . . . . . .             "
                + "4 3 7 2 . . . . . . . . 4 . 3 8 1 . 2 6 . "
                + ". 5 6 4 7 . 3 . 8 . . . . 0 . . 2 3 5 . 8 "
                + "2 . . 3 . 6 7 . 5 . . . . 6 . 0 . 4 1 . . "
                + "0 1 5 7 4 . . . .       3 2 . . . . . . 7 "
                + ". . . 6 . 8 0 . .       5 . . 7 0 . 8 . . "
                + ". 4 . . . . . 7 3       . . 8 5 . . . . . "
                + ". 0 . 5 6 . . 3 .       . . 1 . . . 7 8 . "
                + ". 6 . . . . . . 7       0 . 6 . . . 4 . . "
                + "7 2 . . 0 . . . 6       . 4 . 2 . . 3 . . ";


        Sudoku s = SudokuMockUps.stringToSamuraiSudoku(SudokuMockUps.increase9By1(pattern));
        Solver solver = new Solver(s);
        assertTrue(solver.solveAll(true, false, false));

    }
    @Test
    public void testSamurai1_moreSolutions(){
        String pattern =             "4 7 1 . . 2 0 3 8       7 5 8 1 0 6 3 4 2 "
                                   + "5 0 2 8 4 3 7 . .       0 1 3 4 2 5 8 7 6 "
                                   + "3 8 6 7 0 1 4 2 5       4 2 6 8 3 7 0 1 5 "
                                   + "2 4 3 . 1 . 8 5 .       8 6 5 7 1 0 2 3 4 "
                                   + "6 1 7 2 8 5 3 4 0       1 0 2 3 4 8 5 6 7 "
                                   + "8 5 0 . . . 1 . 2       3 4 7 5 6 2 1 0 8 "
                                   + "0 3 5 1 7 6 2 8 4 6 1 7 5 3 0 6 8 4 7 2 1 "
                                   + "7 2 4 . . 8 6 . . 8 5 . 2 7 4 0 5 1 6 8 3 "
                                   + "1 6 8 . 2 . 5 . . 4 . 2 6 8 1 2 7 3 4 5 0 "
                                   + "            0 5 2 7 8 1 3 4 6             "
                                   + "            8 . . . 4 6 . 2 5             "
                                   + "            4 . 6 . 2 5 . 1 8             "
                                   + "4 3 7 2 8 5 1 6 0 2 7 8 4 5 3 8 1 7 2 6 0 "
                                   + "1 5 6 4 7 0 3 2 8 5 6 4 1 0 7 6 2 3 5 4 8 "
                                   + "2 8 0 3 1 6 7 4 5 1 . . 8 6 2 0 5 4 1 7 3 "
                                   + "0 1 5 7 4 3 6 8 2       3 2 0 1 4 8 6 5 7 "
                                   + "3 7 2 6 5 8 0 1 4       5 1 4 7 0 6 8 3 2 "
                                   + "6 4 8 0 2 1 5 7 3       6 7 8 5 3 2 0 1 4 "
                                   + "8 0 4 5 6 7 2 3 1       2 3 1 4 6 0 7 8 5 "
                                   + "5 6 1 8 3 2 4 0 7       0 8 6 3 7 5 4 2 1 "
                                   + "7 2 3 1 0 4 8 5 6       7 4 5 2 8 1 3 0 6 ";

        /*         4 7 1  ⁵⁶  ⁵⁶ 2     0 3   8
                   5 0 2  8   4  3     7 ¹⁶  ¹⁶
                   3 8 6  7   0  1     4 2   5

                   2 4 3  ⁰⁶  1  ⁰⁷    8 5   ⁶⁷
                   6 1 7  2   8  5     3 4   0
                   8 5 0  ³⁴⁶ ³⁶ ⁴⁷    1 ⁶⁷  2

                   0 3 5  1   7  6     2 8   4    6  1  7   5  3 0
                   7 2 4  ⁰³⁵ ³⁵ 8     6 ⁰¹  ¹³   8  5  ⁰³  2  7 4
                   1 6 8  ⁰³⁴ 2  ⁰⁴⁷   5 ⁰⁷  ³⁷   4  ⁰³ 2   6  8 1

                                       0 5   2    7  8  1   3  4 6
                                       8 ¹³⁷ ¹⁷   ⁰³ 4  6   ⁰⁷ 2 5
                                       4 ³⁷  6    ⁰³ 2  5   ⁰⁷ 1 8

                                       1 6   0    2  7  8   4  5 3
                                       3 2   8    5  6  4   1  0 7
                                       7 4   5    1  ⁰³ ⁰³  8  6 2
        */


        Sudoku s = SudokuMockUps.stringToSamuraiSudoku(SudokuMockUps.increase9By1(pattern));
        Solver solver = new Solver(s);
        //lockedCandidates column 8 and the extention below
        solver.getSolverSudoku().getCurrentCandidates(Position.get(8,10)).clear(3);

        assertTrue(solver.solveAll(true, false, true));

    }


    @Test
    public void testSamurai1_subBoard_a(){
        String pattern = ". 7 . . . 2 0 . 8 "
                       + "5 0 . 8 . 3 7 . . "
                       + "3 8 6 . 0 . . . 5 "
                       + "2 4 3 . 1 . . . . "
                       + ". . . . . 5 . . 0 "
                       + ". . . . . . . . 2 "
                       + ". 3 . . 7 6 . . 4 "
                       + "7 2 4 . . 8 6 . . "
                       + "1 . . . . . . . . ";


        Sudoku s = SudokuMockUps.stringTo9x9Sudoku(SudokuMockUps.increase9By1(pattern));
        Solver solver = new Solver(s);
        assertTrue(solver.solveAll(true, false, false));

    }



}