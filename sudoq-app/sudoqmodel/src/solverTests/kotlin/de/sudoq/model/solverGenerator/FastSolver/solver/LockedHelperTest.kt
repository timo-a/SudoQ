package de.sudoq.model.solverGenerator.FastSolver.solver

import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solverGenerator.solver.SudokuMockUps
import de.sudoq.model.solverGenerator.solver.helper.LockedCandandidatesHelper
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper
import de.sudoq.model.solverGenerator.utils.PrettyStandard9x9Repo
import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.complexity.Complexity
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path
import java.util.ArrayList

class LockedHelperTest {

    val standard9x9Repo = PrettyStandard9x9Repo()

    @Test
    fun lockedUpdateOne() { //TODO can the sudoku be mocked on the spot?
        /*¹²³⁴⁵⁶⁷⁸⁹
          9    8     4 | ¹²   ¹²⁷  ³⁶ | ¹³⁵ ⁶⁷  ⁵⁷
          ³⁷   ⁶⁷    2 | 5    ¹⁷⁸  ³⁶ | ¹³⁹ 4   ⁷⁸⁹
          ³⁵⁷  ⁵⁶⁷   1 | 9    ⁷⁸   4  | ³⁵  ⁶⁷⁸ 2
          -------------+--------------|--------------
          ⁵⁸   ¹⁴⁵   6 | ¹⁴⁸  9    7  | 2   3   ⁴⁵⁸
          ⁵⁷⁸  ¹⁴⁵⁷  3 | 6    ¹⁴⁸  2  | ⁵⁹  ⁷⁸  ⁴⁵⁷⁸⁹
          2    ⁴⁷    9 | ⁴⁸   3    5  | 6   1   ⁴⁷⁸
          -------------|--------------+--------------
          1    9     5 | 7    6    8  | 4   2   3
          4    2     7 | 3    5    1  | 8   9   6
          6    3     8 | ²⁴   ²⁴   9  | 7   5   1
          expected to find r3c7 (=Position(6,2)), 5 can be removed
        */

        val s = standard9x9Repo.read(
            Path.of("sudokus","standard9x9", "lockedCandidates1.pretty"),
            Complexity.arbitrary)
        val sudoku = SolverSudoku(s)
        val helper: SolveHelper = LockedCandandidatesHelper(sudoku, sudoku.complexityValue)
        val sdlist: MutableList<SolveDerivation?> = ArrayList<SolveDerivation?>()

        sudoku.getCurrentCandidates(Position[6, 2]).isSet(4).`should be true`()

        while (helper.update(true)) {
            sdlist.add(helper.derivation)
            println("print derivation:")
            println(sdlist[sdlist.size - 1])
        }
        /* make sure the solution where "5" is removed from field "7,3" is among the found solutions */
        sudoku.getCurrentCandidates(Position[6, 2]).isSet(4).`should be false`()

        sdlist.size `should be equal to` 2
    }


}