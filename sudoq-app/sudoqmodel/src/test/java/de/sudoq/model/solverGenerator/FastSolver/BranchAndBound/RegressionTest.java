package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.AmbiguityChecker;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.Sudoku16DLX;
import de.sudoq.model.solverGenerator.FastSolver.FastAmbiguityChecker;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory;
import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.solver.SudokuMockUps;
import de.sudoq.model.sudoku.Sudoku;

import static de.sudoq.model.solverGenerator.RegressionBoards16.increase16By1;

public class RegressionTest {

    @BeforeClass
    public static void init() {
        FileManagerTests.init();
    }





    /* fast branch and bound takes forever for this one */

    public static String pattern2 =
               ".  .  6  .  .  .  .  .  .  . 12  .  9  .  0  . "
            + " .  .  0  . 10  8  .  9  .  .  .  . 12  . 11  . "
            + " .  .  .  .  .  .  .  .  .  0 13  3  .  .  6  5 "
            + " .  4  .  .  0  1  . 15  .  .  .  .  3 14  . 13 "
            + " .  .  .  .  3  .  .  .  .  4  .  . 15  . 10  . "
            + " . 11  .  .  . 12  . 13 10  .  .  .  0  2  .  . "
            + " 1  5 13  .  .  . 15  .  .  .  .  .  .  .  .  . "
            + " .  .  3 12  4  .  .  .  5  .  7  8  .  .  .  . "
            + " .  . 10 11  6 15  .  .  4  .  .  .  .  .  .  0 "
            + " .  .  .  8  1  .  .  .  .  .  .  7  .  .  .  . "
            + " .  . 14  0  .  . 11  .  .  . 10  6  .  .  .  . "
            + " . 15  .  .  .  .  .  .  0  .  .  .  .  .  1  . "
            + "13  .  .  . 15  .  9  4  .  6  2  .  .  . 14  . "
            + " .  .  .  .  .  .  .  . 15  .  .  5  .  . 12  . "
            + " .  .  .  .  7  .  .  6  . 10  .  .  .  .  .  . "
            + " .  .  .  .  .  .  .  2  .  8  .  .  .  .  .  . ";



    /**
     * Braucht < 2s
     */
    @Test
    public void test2_dlx() {

        Sudoku sudoku = SudokuMockUps.stringTo16x16Sudoku(increase16By1(pattern2));
        FastSolver solver = new DLXSolver(sudoku);
        solver.isAmbiguous();
    }
}
