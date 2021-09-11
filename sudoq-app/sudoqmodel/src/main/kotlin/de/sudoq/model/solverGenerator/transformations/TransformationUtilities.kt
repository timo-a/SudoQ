/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator.transformations

import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import java.util.*
import kotlin.collections.set

/**
 * Fundamental building blocks for transformations
 * the actual transformation classes will use a combination of the methods provided here
 */

/* elementary [Permutation]s: all [Cell]s move */

internal fun rotate90(sudoku: Sudoku) {
    mirrorDiagonallyDown(sudoku)
    mirrorHorizontally(sudoku)
}


internal fun rotate180(sudoku: Sudoku) {
    rotate90(sudoku)
    rotate90(sudoku)
}

internal fun rotate270(sudoku: Sudoku) {
    rotate90(sudoku)
    rotate90(sudoku)
    rotate90(sudoku)
}

internal fun mirrorHorizontally(sudoku: Sudoku) {
    val width = sudoku.sudokuType!!.size!!.x
    for (i in 0 until width / 2) {
        swap_columns(sudoku, i, width - 1 - i)
    }
}

internal fun mirrorVertically(sudoku: Sudoku) {
    mirrorHorizontally(sudoku)
    rotate180(sudoku)
}

internal fun mirrorDiagonallyDown(sudoku: Sudoku) {
    val width = sudoku.sudokuType!!.size!!.x
    for (i in 0 until width - 1) { // zeilen
        for (j in i + 1 until width) { // zeilenElemente
            swapCells(
                sudoku,
                Position[i, j],
                Position[j, i]
            )
        }
    }
}

internal fun swapCells(sudoku: Sudoku, a: Position, b: Position) {
    val tmp = sudoku.getCell(a)
    sudoku.setCell(sudoku.getCell(b), a)
    sudoku.setCell(tmp, b)
}

internal fun mirrorDiagonallyUp(sudoku: Sudoku) {
    mirrorHorizontally(sudoku)
    rotate90(sudoku)
}

/* Special [Permutation]s: only some [Cell]s swap */

/**
 * Permutes the rows in a block row.
 *
 * @param sudoku [Sudoku] on which to perform the transformation
 */
internal fun inBlockRowPermutation(sudoku: Sudoku) {
    rotate90(sudoku)
    inBlockColumnPermutation(sudoku, sudoku.sudokuType!!.blockSize.y)
    rotate270(sudoku)
}

/**
 * Permutes the columns in a block column.
 *
 * @param sudoku [Sudoku] on which to perform the transformation
 */
internal fun inBlockColumnPermutation(sudoku: Sudoku) {
    inBlockColumnPermutation(sudoku, sudoku.sudokuType!!.blockSize.x)
}

/**
 * moves blocks horizontally
 *
 * @param sudoku [Sudoku] on which to perform the transformation
 */
internal fun horizontalBlockPermutation(sudoku: Sudoku) {
    val collumnsPerBlock = sudoku.sudokuType!!.blockSize.x
    val numberOfHorizontalBlocks = sudoku.sudokuType!!.size!!.x / collumnsPerBlock
    rotate_horizontally_By1(sudoku, numberOfHorizontalBlocks, collumnsPerBlock)
    horizontalBlockSwaps(sudoku, numberOfHorizontalBlocks, collumnsPerBlock)
}

/**
 * moves blocks vertically
 *
 * @param sudoku [Sudoku] on which to perform the transformation
 */
internal fun verticalBlockPermutation(sudoku: Sudoku) {
    val rowsPerBlock = sudoku.sudokuType!!.blockSize.y
    val numberOfVertikalBlocks = sudoku.sudokuType!!.size!!.y / rowsPerBlock
    Rotate90().permutate(sudoku)
    rotate_horizontally_By1(sudoku, numberOfVertikalBlocks, rowsPerBlock)
    horizontalBlockSwaps(sudoku, numberOfVertikalBlocks, rowsPerBlock)
    Rotate270().permutate(sudoku)
}

// columns from 0 to numberOfColumnsInBlock - 1
private fun swapColumnOfBlocks(
    sudoku: Sudoku,
    column1: Int,
    column2: Int,
    numberOfColumnsInBlock: Int
) {
    if (column1 != column2) {
        for (i in 0 until numberOfColumnsInBlock) {
            swap_columns(
                sudoku, column1 * numberOfColumnsInBlock + i, column2
                        * numberOfColumnsInBlock + i
            )
        }
    }
}

/* swaps two columns */
private fun swap_columns(sudoku: Sudoku, column1: Int, column2: Int) {
    val height = sudoku.sudokuType!!.size!!.y
    for (j in 0 until height) {
        val a = Position[column1, j]
        val b = Position[column2, j]
        swapCells(sudoku, a, b)
    }
}

/* moves each block to the right */
private fun rotate_horizontally_By1(
    sudoku: Sudoku,
    numberOfHorizontalBlocks: Int,
    blocklength: Int
) {
    for (i in 0 until numberOfHorizontalBlocks - 1) swapColumnOfBlocks(
        sudoku,
        i,
        i + 1,
        blocklength
    )
}

/* swaps columns of blocks */
private fun horizontalBlockSwaps(
    sudoku: Sudoku,
    numberOfHorizontalBlocks: Int,
    collumnsPerBlock: Int
) {
    val limit = numberOfHorizontalBlocks / 2 - (1 - numberOfHorizontalBlocks % 2)
    for (i in 0 until limit) {
        val first = Transformer.random.nextInt(numberOfHorizontalBlocks)
        val other = randomOtherNumber(first, numberOfHorizontalBlocks)
        swapColumnOfBlocks(sudoku, first, other, collumnsPerBlock)
    }
}

/**
 * swaps columns in a block column
 *
 * @param sudoku [Sudoku] on which to execute the transformation
 * @param blockWidth Number of columns per block (standard has 3, 16x16 has 4)
 */
private fun inBlockColumnPermutation(sudoku: Sudoku, blockWidth: Int) {
    val numberOfHorizontalBlocks = (sudoku.sudokuType!!.size!!.x
            / sudoku.sudokuType!!.blockSize.x)
    for (i in 0 until numberOfHorizontalBlocks) {
        for (j in 0 until blockWidth) {
            val first = Transformer.random.nextInt(blockWidth)
            swap_columns(
                sudoku,
                i * blockWidth + first,
                i * blockWidth + randomOtherNumber(first, blockWidth)
            )
        }
    }
}


/* permute symbols */

/**
 * Randomly swaps solutions of the sudoku.
 * Same symbols are replaced with same symbol.
 *
 * @param sudoku [Sudoku] on which to execute the transformation
 */
fun changeSymbols(sudoku: Sudoku) {
    val permutationRule = createPermutation(sudoku)
    for (p in sudoku.sudokuType!!.validPositions) {
        val f = sudoku.getCell(p)
        val oldSymbol = f!!.solution
        val newSymbol = permutationRule[oldSymbol]!!
        if (newSymbol != oldSymbol) // nur wenn sich was ändert, sonst bleibts ja gleich
            sudoku.setCell(Cell(f.isEditable, newSymbol, f.id, f.numberOfValues), p)
    }
}

/** Returns a permutation as a map. Identity is never returned */
private fun createPermutation(sudoku: Sudoku): Map<Int, Int> {
    val permutationRule: MutableMap<Int, Int> = HashMap()
    val numberOfSymbols = sudoku.sudokuType!!.numberOfSymbols
    val tries = Math.sqrt(numberOfSymbols.toDouble()).toInt() // wurzel(numberOfElements)
    // versuche, damit wir
    // deterministisch
    // bleiben
    var success: Boolean
    for (i in sudoku.sudokuType!!.symbolIterator) {
        success = false
        run {
            var j = 0
            while (j < tries && !success) {
                val otherNum = randomOtherNumber(i, numberOfSymbols)
                if (!permutationRule.containsValue(otherNum)) {
                    permutationRule[i] = otherNum
                    success = true
                }
                j++
            }
        }
        var j = 0
        while (!success && j < numberOfSymbols) {
            if (!permutationRule.containsValue(j)) {
                permutationRule[i] = j
                break
            }
            j++
        }
    }
    return permutationRule
}


/* returns a number < range but not num */
private fun randomOtherNumber(num: Int, range: Int): Int {
    val distance = Transformer.random.nextInt(range - 1) + 1
    return (num + distance) % range
}
