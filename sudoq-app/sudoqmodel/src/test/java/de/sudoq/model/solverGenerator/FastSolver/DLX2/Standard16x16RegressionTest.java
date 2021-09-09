package de.sudoq.model.solverGenerator.FastSolver.DLX2;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.RegressionBoards16;

public class Standard16x16RegressionTest {


    @BeforeClass
    public static void init() {
        TestWithInitCleanforSingletons.legacyInit();
    }

    @Test
    public void testR2(){
        FastSolver dlxsolver = new Standard16Solver(RegressionBoards16.r2);
        boolean dlx1 = dlxsolver.isAmbiguous();
    }


    @Test
    public void testArrayIndexOutOfBoundsException(){
    int[][] board = {{ 0,  0, 0,  0,  4,  0, 14,  0,  0,  8, 0, 0, 0,  0, 13, 0},
                     { 6,  0, 0, 12,  0,  0,  0,  0,  0,  0, 0, 7, 0,  0,  1, 0},
                     { 9,  0, 0,  0,  1,  3,  0,  5,  2,  4, 0, 0, 0,  0,  0, 0},
                     {10,  0, 0, 11, 15,  2,  7,  0,  0, 14, 0, 0, 0, 12,  0, 0},

                     { 8, 10, 0,  0,  0,  0,  0,  0, 15,  0, 0, 0, 0,  0,  0, 0},
                     { 0,  0, 0,  0,  0,  0,  0,  4,  0,  0, 0, 0, 6,  1, 11, 0},
                     { 0,  0, 7,  0,  0,  0,  0,  0,  0,  0, 1, 0, 0,  8,  0, 0},
                     {12,  0, 0,  0,  0, 13,  0,  0,  0,  0, 0, 0, 0,  0,  9, 0},

                     { 0,  0, 0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0,  0,  0, 0},
                     { 0,  0, 0,  0,  0,  5,  0,  0,  0,  0, 0, 0, 0,  0, 10, 0},
                     {14,  8, 0, 13,  0,  0,  2,  0,  0,  0, 0, 0, 4,  0,  0, 0},
                     { 2,  0, 0,  0,  0,  0,  0,  3,  0,  6, 0, 0, 0,  0,  0, 8},

                     { 0,  0, 0,  0,  0,  0,  0,  0,  4,  0, 7, 0, 0,  0, 15, 6},
                     { 1,  9, 0,  0,  2,  4,  0,  0,  0,  0, 0, 0, 0,  0,  0, 0},
                     { 0,  0, 0,  0,  0,  0,  0,  1,  0,  0, 0, 0, 0,  7,  0, 0},
                     { 0,  0, 0,  0,  0,  0,  0, 10, 12, 15, 0, 0, 0,  0, 14, 0}};
    new Hexadoku().solve(board);

    }
}
