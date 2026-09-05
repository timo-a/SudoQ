/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypeProvider.getSudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

/** Provides functions to create a [SudokuType] or an empty [Sudoku] */
class SudokuBuilderNew(private val type: SudokuType?) {

    var id: Int = 0

    private val solutions: PositionMap.Builder<Int> = PositionMap.Builder(type!!.size)
    private val setValues: MutableSet<Position> = HashSet()

    /**
     * Creates a Builder for a [Sudoku] of the specified type.
     *
     * @param type Enum-Type of the [Sudoku] to create
     * @throws NullPointerException if type invalid.
     */
    constructor(type: SudokuTypes, sudokuTypeRepo: ReadRepo<SudokuType>) : this(getSudokuType(type, sudokuTypeRepo))

    fun id(id: Int): SudokuBuilderNew {
        this.id = id
        return this
    }

    /**
     * Creates a [Sudoku] with the SudokeType of this builder and the entered Solutions.
     *
     * @return a new Sudoku
     */
    fun build(): SudokuUnderConstruction = SudokuUnderConstruction(type!!, solutions.build(), setValues, id)

    /**
     * Ads a solution to the Sudoku
     *
     * @param pos [Position] of the Solution
     * @param value Value of the Solution
     * @throws IllegalArgumentException If the value is out of bounds for the type
     */
    fun addSolution(pos: Position, value: Int) {
        require(value >= 0) { "value must be at least 0, but $value was passed" }
        require(value < type!!.numberOfSymbols) { "value must be under ${type.numberOfSymbols} but $value was passed" }
        solutions.put(pos, value)
    }

    /**
     * Sets this Position as pre-filled in the Sudoku
     *
     * @param pos [Position] to mark as pre-filled
     */
    fun setFixed(pos: Position) {
        setValues.add(pos)
    }

}
