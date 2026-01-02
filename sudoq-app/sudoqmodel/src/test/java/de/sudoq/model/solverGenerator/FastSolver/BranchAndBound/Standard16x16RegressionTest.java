package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.RegressionBoards16;

public class Standard16x16RegressionTest {


    //should work just fine
    @Test(timeout = 6000)
    public void testR2(){

        FastBranchAndBound solver = new FastBranchAndBound(RegressionBoards16.r2);
        solver.solveAll();//todo test solveAll2 here too
    }
}
