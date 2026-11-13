package de.sudoq.controller.sudoku

import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Sudoku

/**
 * Symbols that occur exactly once in every unique constraint.
 * They don't need to be filled in anymore.
 */
internal fun completedSymbols(sudoku: Sudoku): Set<Int> {
    val type = sudoku.sudokuType
    val uConstraints = type.constraints.filter(Constraint::hasUniqueBehavior)
    uConstraints.forEach { check(it.size == type.numberOfSymbols) }

    if (uConstraints.isEmpty()) return emptySet()

    fun getUniqueValues(constraint: Constraint) : Set<Int> {
        return constraint
            .map(sudoku::getCurrentValue)
            .filter { it != -1 } // can be -1 if cell is empty, ignore those
            .groupingBy { it }.eachCount() // group by occurrence count
            .filterValues { it == 1 }.keys // return those that appear once
    }

    val completed = uConstraints
        .map(::getUniqueValues)
        .reduce { acc, ints -> acc.intersect(ints) }
    return completed
}
