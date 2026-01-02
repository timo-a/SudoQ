package de.sudoq.model.solverGenerator.FastSolver

import de.sudoq.model.solverGenerator.GenerationAlgo
import de.sudoq.model.solverGenerator.utils.PrettySamuraiRepo
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuBuilder
import de.sudoq.model.sudoku.complexity.Complexity
import org.amshove.kluent.`should be true`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import java.util.stream.Stream

class SamuraiLoadTest {

    //test was migrated from existing one
    //todo write test that solves single samurai sudoku to verify that that works
    //todo loop through all types to see if all sudokus in the assets are solvable, but how often do you need to run these (once before any release?), what is the purpose?
    @ParameterizedTest
    @MethodSource("providePaths")
    fun testSamurai(path: String) {
        val repo = PrettySamuraiRepo()
        val s: Sudoku = repo.read(Paths.get(path), Complexity.arbitrary)
        val fs = FastSolverFactory.getSolver(s)
        fs.hasSolution().`should be true`()

        val solution: PositionMap<Int>  = fs.getSolutions();
        val sub = SudokuBuilder(s.sudokuType);
        for( p: Position in GenerationAlgo.getPositions(s)) {
            sub.addSolution(p, solution.get(p)!!);//fill in all solutions
        }
        val sudoku = sub.createSudoku();
        for (p: Position in GenerationAlgo.getPositions(sudoku)) {
            val f = sudoku.getCell(p);
            f!!.currentValue = f.solution;
        }
        println(sudoku);
    }

    companion object{
        @JvmStatic
        fun providePaths(): Stream<Arguments> {
            return Stream
                .of("easy", "medium", "difficult", "infernal")
                .flatMap {
                    c -> IntRange(1,10)
                        .map { i -> Arguments.of("sudokus/samurai/assets/samurai_${c}_$i.pretty")
                        }.stream()
                };
        }
    }
}