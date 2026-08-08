package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.NoNotesDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import java.util.BitSet
import java.util.Vector

class NoNotesHelper(sudoku: SolverSudoku, complexity: Int) : SolveHelper(sudoku, complexity) {

    override fun update(buildDerivation: Boolean): Boolean {
        var foundOne = false
        var candidate: Position
        val emptyPos = sudoku.sudokuType.validPositions
            .filter { sudoku.getCell(it).isCompletelyEmpty }
            .toCollection(Vector<Position>())
        foundOne = emptyPos.isNotEmpty()
        if (buildDerivation) {
            //Todo replace by filling in all notes and apply other helpers repeatedly? but how can we limit this to allempty positions?
            //create map from pos to constraint
            val emptyPosSet: Set<Position> = HashSet(emptyPos)
            val cmap: MutableMap<Position, MutableList<Constraint>> = HashMap()
            for (c in sudoku.sudokuType)
                if (c.hasUniqueBehavior())
                    for (p in c.getPositions())
                        if (emptyPosSet.contains(p))
                            if (cmap.containsKey(p))
                                cmap[p]!!.add(c)
                            else cmap[p] = ArrayList(listOf(c))
            derivation = NoNotesDerivation()
            val allSymbols: Set<Int> = sudoku.sudokuType.symbolIterator.toSet()
            for (p in emptyPos) {
                val allCandidates: MutableSet<Int> = HashSet(allSymbols)
                for (c in cmap[p]!!) {
                    for (pi in c.getPositions()) {
                        val f = sudoku.getCell(pi)
                        if (f.isSolved) allCandidates.remove(f.currentValue)
                    }
                }
                val relevant = BitSet()
                val irrelevant = BitSet()
                for (i in allSymbols)
                    (if (i in allCandidates) relevant
                    else irrelevant).set(i)

                derivation!!.addDerivationCell(DerivationCell(p, relevant, irrelevant))
            }
        }
        return foundOne
    }

    init {
        hintType = HintTypes.NoNotes
    }
}