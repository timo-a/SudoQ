package de.sudoq.model.solverGenerator.FastSolver

import de.sudoq.model.solverGenerator.utils.PrettySamuraiRepo
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import org.amshove.kluent.`should be true`
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path

class SamuraiRegressionTest {

    @Test
    fun testSamurai1() {
        val repo = PrettySamuraiRepo()
        val s: Sudoku = repo.read(
            Path.of("sudokus", "samurai", "samurai1.pretty"),
            Complexity.arbitrary)
        val fs = FastSolverFactory.getSolver(s)
        fs.isAmbiguous.`should be true`()
    }

    @Test
    fun testSamurai2() {
        val repo = PrettySamuraiRepo()
        val s: Sudoku = repo.read(
            Path.of("sudokus", "samurai", "samurai2.pretty"),
            Complexity.arbitrary)
        val fs = FastSolverFactory.getSolver(s)
        fs.isAmbiguous.`should be true`()
    }

    @Test
    fun testSamurai2fromFile() {
        val repo = PrettySamuraiRepo()
        val s: Sudoku = repo.read(
            Path.of("sudokus", "samurai", "empty.pretty"),
            Complexity.arbitrary)
        val fs = FastSolverFactory.getSolver(s)
        fs.isAmbiguous.`should be true`()
    }

    /**
     * All sudokus are permutations of each other, so we would expect them to  take similar time.
     * But 7 takes 28s whereas all others are < 0.3s ...
     * */
    @ParameterizedTest
    @ValueSource(ints = [0,1,2,3,4,5,6,7,8])
    fun testPermutation(i: Int) {
        val repo = PrettySamuraiRepo()
        val s: Sudoku = repo.read(
            Path.of("sudokus", "samurai", "badPattern$i.pretty"),
            Complexity.arbitrary)
        val fs = FastSolverFactory.getSolver(s)
        fs.isAmbiguous.`should be true`()
    }

}