/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
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
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.set

/**
 * This class provides the fundamental building blocks for transformations
 * the actual transformation classes will use a combination of the methods provided here
 * @author timo
 */
object TransformationUtilities {
    /* elementare Permutationen: alle Felder werden verschoben */
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
            swap_collums(sudoku, i, width - 1 - i)
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
                swapCells(sudoku,
                        Position[i, j],
                        Position[j, i])
            }
        }
    }

    internal fun swapCells(sudoku: Sudoku, a: Position?, b: Position?) {
        val tmp = sudoku.getCell(a!!)
        sudoku.setCell(sudoku.getCell(b!!), a)
        sudoku.setCell(tmp, b)
    }

    internal fun mirrorDiagonallyUp(sudoku: Sudoku) {
        mirrorHorizontally(sudoku)
        rotate90(sudoku)
    }
    /* Spezielle Permutationen: nur manche Felder werden vertauscht */
    /**
     * Führt in jeder Blockzeile für jede Zeile eine Zeilenvertauschung durch.
     *
     * @param sudoku
     * Das Sudoku auf dem die Permutation ausgeführt werden soll
     */
    internal fun inBlockRowPermutation(sudoku: Sudoku) {
        rotate90(sudoku)
        inBlockCollumnPermutation(sudoku, sudoku.sudokuType!!.blockSize.y)
        rotate270(sudoku)
    }

    /**
     * Führt in jeder Blockspalte für jede Spalte eine Spaltenvertauschung durch.
     *
     * @param sudoku
     * Das Sudoku auf dem die Permutation ausgeführt werden soll
     */
    internal fun inBlockCollumnPermutation(sudoku: Sudoku) {
        inBlockCollumnPermutation(sudoku, sudoku.sudokuType!!.blockSize.x)
    }

    /**
     * verschiebt Blöcke in horizontaler Richtung
     *
     * @param sudoku
     * Das Sudoku auf dem die Permutation ausgeführt werden soll
     */
    internal fun horizontalBlockPermutation(sudoku: Sudoku) {
        val collumnsPerBlock = sudoku.sudokuType!!.blockSize.x
        val numberOfHorizontalBlocks = sudoku.sudokuType!!.size!!.x / collumnsPerBlock
        rotate_horizontally_By1(sudoku, numberOfHorizontalBlocks, collumnsPerBlock)
        horizontalBlockSwaps(sudoku, numberOfHorizontalBlocks, collumnsPerBlock)
    }

    /**
     * verschiebt Blöcke in vertikaler Richtung
     *
     * @param sudoku
     * Das Sudoku auf dem die Permutation ausgeführt werden soll
     */
    internal fun verticalBlockPermutation(sudoku: Sudoku) {
        val rowsPerBlock = sudoku.sudokuType!!.blockSize.y
        val numberOfVertikalBlocks = sudoku.sudokuType!!.size!!.y / rowsPerBlock
        Rotate90().permutate(sudoku)
        rotate_horizontally_By1(sudoku, numberOfVertikalBlocks, rowsPerBlock)
        horizontalBlockSwaps(sudoku, numberOfVertikalBlocks, rowsPerBlock)
        Rotate270().permutate(sudoku)
    }

    // collumn von 0 bis numberOfCollumnsInBlock - 1
    private fun swapCollumOfBlocks(sudoku: Sudoku, collumn1: Int, collumn2: Int, numberOfCollumnsInBlock: Int) {
        if (collumn1 != collumn2) {
            for (i in 0 until numberOfCollumnsInBlock) {
                swap_collums(sudoku, collumn1 * numberOfCollumnsInBlock + i, collumn2
                        * numberOfCollumnsInBlock + i)
            }
        }
    }

    /* vertauscht zwei Spalten */
    private fun swap_collums(sudoku: Sudoku, collum1: Int, collum2: Int) {
        val height = sudoku.sudokuType!!.size!!.y
        for (j in 0 until height) {
            val a = Position[collum1, j]
            val b = Position[collum2, j]
            swapCells(sudoku, a, b)
        }
    }

    /* verschiebt jeden Block um eins nach rechts */
    private fun rotate_horizontally_By1(sudoku: Sudoku, numberOfHorizontalBlocks: Int, blocklength: Int) {
        for (i in 0 until numberOfHorizontalBlocks - 1) swapCollumOfBlocks(sudoku, i, i + 1, blocklength)
    }

    /* Vertauscht Blockspalten */
    private fun horizontalBlockSwaps(sudoku: Sudoku, numberOfHorizontalBlocks: Int, collumnsPerBlock: Int) {
        val limit = numberOfHorizontalBlocks / 2 - (1 - numberOfHorizontalBlocks % 2)
        for (i in 0 until limit) {
            val first = Transformer.getRandom().nextInt(numberOfHorizontalBlocks)
            val other = randomOtherNumber(first, numberOfHorizontalBlocks)
            swapCollumOfBlocks(sudoku, first, other, collumnsPerBlock)
        }
    }

    /**
     * Führt in jeder Blockspalte blockWidth Spaltenvertauschungen aus
     *
     * @param sudoku
     * Das Sudoku auf dem die Permutation ausgeführt werden soll
     * @param blockWidth
     * Anzahl an Spalten pro Block
     */
    private fun inBlockCollumnPermutation(sudoku: Sudoku, blockWidth: Int) {
        val numberOfHorizontalBlocks = (sudoku.sudokuType!!.size!!.x
                / sudoku.sudokuType!!.blockSize.x)
        for (i in 0 until numberOfHorizontalBlocks) {
            for (j in 0 until blockWidth) {
                val first = Transformer.getRandom().nextInt(blockWidth)
                swap_collums(sudoku, i * blockWidth + first, i * blockWidth + randomOtherNumber(first, blockWidth))
            }
        }
    }
    /* Symbole vertauschen */
    /**
     * Vertauscht zufällig die Lösungen des Sudokus, wobei gleiche Lösungen gleiche neue Lösungen erhalten.
     *
     * @param sudoku
     * das Sudoku dessen Felder manipuliert werden sollen
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

    /*
	 * gibt eine Map mit einer Permutationsvorschrift zurück. Die Identität ist als Rückgabewert ausgeschlossen
	 */
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

    /* gibt eine zahl < range zurück aber nicht num */
    private fun randomOtherNumber(num: Int, range: Int): Int {
        val distance = Transformer.getRandom().nextInt(range - 1) + 1
        return (num + distance) % range
    }
}