package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound

import de.sudoq.model.solverGenerator.AmbiguityChecker.isAmbiguous
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.solverGenerator.FastSolver.FastAmbiguityChecker
import de.sudoq.model.solverGenerator.FastSolver.FastSolver
import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory
import de.sudoq.model.solverGenerator.solver.Solver
import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver
import de.sudoq.model.solverGenerator.utils.PrettyStandard16x16Repo
import de.sudoq.model.sudoku.complexity.Complexity
import org.amshove.kluent.`should be true`
import org.junit.Test
import java.nio.file.Path

class RegressionTestSlow {

    @Test(timeout = 5 * 60 * 1000)
    //@Timeout(value = 6, unit = MINUTES) junit5 only has this annotation from 5.5.1
    fun test1() {

        val repo = PrettyStandard16x16Repo()
        val s: Sudoku = repo.read(
            Path.of("sudokus", "standard16x16", "slow.pretty"),
            Complexity.arbitrary)

        println(" ambiguitychecker " + isAmbiguous(s))
        println("fambiguitychecker " + FastAmbiguityChecker.isAmbiguous2(s))
        println("fambiguitychecker " + FastAmbiguityChecker.isAmbiguous(s))

        //System.exit(9);
        val fs = FastSolverFactory.getSolver(s)
        val fast = fs.isAmbiguous
        println("fast ambiguous $fast")
        val solver = Solver(s)
        val cr = solver.validate(null)
        println("solver validate $cr")
        val dlxsolver: FastSolver = DLXSolver(s)
        val dlx1 = dlxsolver.isAmbiguous

        /*for (int[][] board : ((DLXSolver) dlxsolver).getBothSolutionsForDebugPurposes())
                for (int[] row : board){
                    for (int e : row)
                        System.out.print(String.format("%1$3s", e));

                    System.out.println();
                }*/
        println("dlx ambiguous $dlx1")
        val solverResult = Solver(s).solveAll(true, true, false)
        println("solver.solveall  $solverResult")
    }

    /**
     * Braucht ewig
     */
    @Test(timeout = 5 * 60 * 1000)
    //@Timeout(value = 6, unit = MINUTES) junit5 only has this annotation from 5.5.1
    fun test2_backtrack() {

        val repo = PrettyStandard16x16Repo()
        val sudoku: Sudoku = repo.read(
            Path.of("sudokus", "standard16x16", "slow1.pretty"),
            Complexity.arbitrary)

        val solver = Solver(sudoku)
        solver.solveAll(false, false, false)
    }

    /**
     * Braucht ewig
     *
     */
    @Test
    fun test2_fastbacktrack() {

        val repo = PrettyStandard16x16Repo()
        val sudoku: Sudoku = repo.read(
            Path.of("sudokus", "standard16x16", "slow1.pretty"),
            Complexity.arbitrary)

        val solver = FastBranchAndBound(sudoku)
        solver.solveAll2()
    }

    /**
     * Braucht < 2s
     */
    @Test
    fun test2_dlx() {
        val repo = PrettyStandard16x16Repo()
        val sudoku: Sudoku = repo.read(
            Path.of("sudokus", "standard16x16", "slow1.pretty"),
            Complexity.arbitrary)
        val solver: FastSolver = DLXSolver(sudoku)
        solver.isAmbiguous.`should be true`()
    }

}