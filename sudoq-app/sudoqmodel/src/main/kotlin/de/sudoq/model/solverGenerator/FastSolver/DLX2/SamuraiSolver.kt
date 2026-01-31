package de.sudoq.model.solverGenerator.FastSolver.DLX2

import de.sudoq.model.solverGenerator.FastSolver.FastSolver
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku

class SamuraiSolver(s: Sudoku) : FastSolver {
    var array: Array<IntArray>?
    var calculationDone: Boolean = false
    private var samurai: Samurai = Samurai()
    var s: Sudoku

    init {
        /* transform sudoku into array or 1-9 for symbols and 0 both for unknown and no cell */
        array = toArray(s)
        this.s = s
    }

    private fun toArray(s: Sudoku): Array<IntArray> {
        val a = Array(21) { IntArray(21) }
        for (pos in s.sudokuType.validPositions) {
            val f = s.getCellNullable(pos)
            if (f != null && f.isSolved) a[pos.y][pos.x] =
                f.currentValue + 1 //s has values [0,8] so we need to add one.
        }

        return a
    }


    override fun hasSolution(): Boolean {
        ensureSolved()
        return samurai.solutions.size > 0
    }

    private fun ensureSolved() {
        if (!calculationDone) {
            samurai.solve(array)
            calculationDone = true
        }
    }

    override fun getSolutions(): PositionMap<Int> {
        val solution = samurai.solutions[0]
        return PositionMap(Position[21, 21], s.sudokuType.validPositions)
        { position: Position -> solution[position.y]!![position.x] - 1 }
    }

    override fun isAmbiguous(): Boolean {
        ensureSolved()
        return samurai.solutions.size >= 2
    }

    override fun getAmbiguousPos(): Position {
        val first = samurai.solutions[0]
        val second = samurai.solutions[1]

        for (r in first.indices) {
            for (c in first[0]!!.indices) {
                if (first[r]!![c] != second[r]!![c]) return Position[c, r]
            }
        }
        throw IllegalStateException()
    }
}
