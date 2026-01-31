package de.sudoq.model.solverGenerator.FastSolver.DLX1

import de.sudoq.model.solverGenerator.FastSolver.FastSolver
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class DLXSolver(s: Sudoku) : FastSolver {
    private var calculationDone = false

    private var solutions: MutableList<Array<IntArray>>? = null

    @get:Deprecated("")
    var bothSolutionsForDebugPurposes: MutableList<Array<IntArray>>? = solutions
        private set

    private val solver: AbstractSudokuSolver
    private val array: Array<IntArray?>

    init {
        when (s.sudokuType.enumType) {
            SudokuTypes.standard16x16 -> solver = Sudoku16DLX()
            SudokuTypes.samurai -> solver = DLXSudokuSamurai()
            SudokuTypes.Xsudoku -> solver = DLXSudokuX()
            SudokuTypes.standard9x9 -> solver = SudokuDLX()
            else -> throw IllegalArgumentException("only 16x16 are accepted at the moment!!!")
        }

        array = sudoku2Array(s)

        // SudokuDLX solver = new SudokuDLX();
        // solver.solve(hardest);
    }

    override fun hasSolution(): Boolean {
        if (!calculationDone) {
            solver.solve(array)
            this.bothSolutionsForDebugPurposes = solver.getSolutions()
            calculationDone = true
        }
        return bothSolutionsForDebugPurposes!!.isNotEmpty()
    }

    override fun isAmbiguous(): Boolean {
        if (!calculationDone) {
            /*
             * If the sudoku only has one solution, we have to go through the whole search space
             * to rule out further solutions.
             * as this takes a long time we rather want to stop after x minutes and treat it as if there are no further solution
             *
             * */
            val executor = Executors.newSingleThreadExecutor()
            val c: Collection<AmbiguousTask> = listOf(AmbiguousTask())
            try {
                executor.invokeAll(c, 5, TimeUnit.MINUTES) // Timeout of 5 minutes.
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            executor.shutdown()
        }


        return this.bothSolutionsForDebugPurposes != null && bothSolutionsForDebugPurposes!!.size > 1
    }

    override fun getAmbiguousPos(): Position {
        val first = bothSolutionsForDebugPurposes!![0]
        val second = bothSolutionsForDebugPurposes!![1]

        for (r in first.indices) {
            for (c in first[0].indices) {
                if (first[r][c] != second[r][c]) return Position[c, r]
            }
        }
        throw IllegalStateException()
    }

    override fun getSolutions(): PositionMap<Int> {
        val solution = bothSolutionsForDebugPurposes!![0]
        val pm: PositionMap<Int> = PositionMap(Position[solution[0].size, solution.size])

        for (r in solution.indices) for (c in solution[0].indices) {
            if (solution[r][c] != -1) pm.put(Position[c, r], solution[r][c] - 1)
        }
        return pm
    }

    private inner class AmbiguousTask : Callable<Any?> {
        @Throws(Exception::class)
        override fun call(): Any? {
            solver.solve(array)
            solutions = solver.getSolutions()
            calculationDone = true
            return null
        }
    }

    companion object {
        private fun sudoku2Array(s: Sudoku): Array<IntArray?> {
            val dim = s.sudokuType.size
            val sarray = Array<IntArray?>(dim.y) { IntArray(dim.x) }
            for (r in sarray.indices) for (c in sarray[0]!!.indices) {
                val f = s.getCellNullable(Position[c, r])
                sarray[r]!![c] = if (f == null)
                    -1 //if pos doesn't exist e.g. (9,0) in SamuraiSudoku
                else
                    if (f.isSolved)
                        f.currentValue + 1
                    else
                        0
            }
            return sarray
        }
    }
}

