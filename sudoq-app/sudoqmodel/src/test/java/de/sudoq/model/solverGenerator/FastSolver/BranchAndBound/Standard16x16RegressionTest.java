package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.RegressionBoards16;

class Standard16x16RegressionTest {


    //should work just fine
    @Test
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    void r2(){

        FastBranchAndBound solver = new FastBranchAndBound(RegressionBoards16.r2);
        solver.solveAll();//todo test solveAll2 here too
    }
}
