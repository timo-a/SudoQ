/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypeProvider.getSudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.io.File

/** Provides functions to create a [SudokuType] or an empty [Sudoku] */
class SudokuBuilder(private val type: SudokuType?) {

    private val solutions: PositionMap<Int> = PositionMap(type!!.size!!)
    private val setValues: PositionMap<Boolean> = PositionMap(type?.size!!)

    /**
     * Cretaes a Builder for a [Sudoku] of the specified type.
     *
     * @param type Enum-Type of the [Sudoku] to create
     * @throws NullPointerException if type invalid.
     */
    constructor(type: SudokuTypes, sudokuDir: File) : this(getSudokuType(type, sudokuDir))

    /**
     * Creates a [Sudoku] with the SudokeType of this builder and the entered Solutions.
     *
     * @return a new Sudoku
     */
    fun createSudoku(): Sudoku {
        return Sudoku(type!!, solutions, setValues)
    }

    /**
     * Ads a solution to the Sudoku
     *
     * @param pos [Position] of the Solution
     * @param value Value of the Solution
     * @throws IllegalArgumentException If the value is out of bounds for the type
     */
    fun addSolution(pos: Position, value: Int) {
        require(!(value < 0 || value >= type!!.numberOfSymbols)) { "Invalid value for given Sudoku Type" }
        solutions.put(pos, value)
    }

    /**
     * Sets this Position as pre-filled in the Sudoku
     *
     * @param pos [Position] to mark as pre-filled
     */
    fun setFixed(pos: Position?) {
        setValues.put(pos!!, true)
    }

}