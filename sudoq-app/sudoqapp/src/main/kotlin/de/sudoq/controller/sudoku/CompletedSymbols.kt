package de.sudoq.controller.sudoku

import de.sudoq.model.sudoku.Sudoku

/**
 * Symbols that occur exactly once in every region that requires all symbols.
 * Using the regions also covers overlapping grids and additional variant rules,
 * without consulting the solution or counting pencil notes.
 */
internal fun completedSymbols(sudoku: Sudoku): Set<Int> {
    val type = sudoku.sudokuType
    val regions = type.constraints.filter {
        it.hasUniqueBehavior() && it.size == type.numberOfSymbols
    }
    if (regions.isEmpty()) return emptySet()

    val completed = type.symbolIterator.toMutableSet()
    for (region in regions) {
        val counts = IntArray(type.numberOfSymbols)
        for (position in region) {
            val value = sudoku.getCurrentValue(position)
            if (value in counts.indices) counts[value]++
        }
        completed.removeAll { counts[it] != 1 }
    }
    return completed
}
