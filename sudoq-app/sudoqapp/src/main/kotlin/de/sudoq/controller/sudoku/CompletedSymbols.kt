package de.sudoq.controller.sudoku

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Sudoku

/**
 * Symbols that occur exactly once in every region that requires all symbols.
 * Using the regions also covers overlapping grids and additional variant rules,
 * without consulting the solution or counting pencil notes.
 */
internal fun completedSymbols(sudoku: Sudoku): Set<Int> {
    val type = sudoku.sudokuType
    val uConstraints = type.constraints.filter(Constraint::hasUniqueBehavior)
    uConstraints.forEach { check(it.size == type.numberOfSymbols) }

    if (uConstraints.isEmpty()) return emptySet()

    val completed = type.symbolIterator.toMutableSet()
    for (uConstraint in uConstraints) {
        val counts = IntArray(type.numberOfSymbols)
        for (position in uConstraint) {
            val value = sudoku.getCurrentValue(position)
            if (value in counts.indices) counts[value]++
        }
        completed.removeAll { counts[it] != 1 }
    }
    return completed
}
