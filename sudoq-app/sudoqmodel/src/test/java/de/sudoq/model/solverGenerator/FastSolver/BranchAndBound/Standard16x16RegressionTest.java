package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound;

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

    //should work just fine
    @Test(timeout = 60)
    public void testR2(){

        FastBranchAndBound solver = new FastBranchAndBound(RegressionBoards16.r2);
        solver.solveAll2();
    }
}
