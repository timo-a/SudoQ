/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.sudokuTypes.SudokuType

/**
 * This class represents a Sudoku with mit seinem Typ, seinen Feldern und seinem Schwierigkeitsgrad.
 *
 * @param type The Type of the Sudoku
 * @property cells Eine Map, welche jeder Position des Sudokus ein Feld zuweist
 */
open class SudokuUnderTransformation(
    type: SudokuType,
    val cells: MutableMap<Position, SimpleCell>, //needs to be mutable for setCell. extend PositionMap?
    private val cellPositions: MutableMap<Int, Position>
): Iterable<SimpleCell>, AbstractSudoku<SimpleCell>(type) {

    companion object {

        fun map(cell: Cell) : SimpleCell {
            return SimpleCell(cell.isEditable, cell.currentValue, cell.id, cell.numberOfValues)
        }

        fun map(cell: SimpleCell) : Cell {
            return Cell(cell.isEditable, cell.value, cell.id, cell.numberOfValues)
        }
    }

    /**
     * Returns the [Cell] at the specified [Position].
     *
     * @param position Position of the cell
     * @return Cell at the [Position]
     * @throws IllegalArgumentException if the position is not mapped to a [Cell].
     */
    fun getCell(position: Position): SimpleCell {
        return requireNotNull(cells[position])
    }

    fun getValue(position: Position): Int {
        return requireNotNull(cells[position]).value
    }

    /**
     * Maps the [Position] to the [Cell]
     * if cell is null nothing happens
     *
     *
     * @param cell the new [Cell]
     * @param position the [Position] for the new Cell
     */
    private fun setCell(cell: SimpleCell, position: Position) {
        cells[position] = cell
        cellPositions[cell.id] = position
    }

    fun swapCells(a: Position, b: Position) {
        val tmp = getCell(a)
        setCell(getCell(b), a)
        setCell(tmp, b)
    }


    fun replaceValue(position: Position, oldValue: Int, newValue: Int) {
        val cell = getCell(position)
        require(cell.value == oldValue)//just to check
        cell.value = newValue
    }


    /**
     * Checks if the id is mapped to a cell
     */
    fun hasCell(id: Int): Boolean {
        val p: Position = cellPositions[id] ?: return false
        return cells[p] != null
    }

    /**
     * Checks if the id is mapped to a cell
     */
    fun hasCell(pos: Position): Boolean = cells[pos] != null

    /**
     * Returns an [Iterator] over the [Cell]s.
     *
     * @return An [Iterator] over the [Cell]s
     */
    override fun iterator(): Iterator<SimpleCell> {
        return cells.values.iterator()
    }

    //debug
    override fun toString(): String {
        val sb = StringBuilder()
        val OFFSET = if (sudokuType.numberOfSymbols < 10) "" else " "
        val EMPTY = if (sudokuType.numberOfSymbols < 10) "x" else "xx"
        val NONE = if (sudokuType.numberOfSymbols < 10) " " else "  "
        for (j in 0 until sudokuType.size.y) {
            for (i in 0 until sudokuType.size.x) {
                val position = Position[i, j]
                var op: String
                if (hasCell(position)) { //cell exists
                    val value = getCell(position).value
                    op = when {
                        value == -1 -> EMPTY
                        value < 10 -> OFFSET + value
                        else -> value.toString() + ""
                    }
                    sb.append(op)
                } else {
                    sb.append(NONE)
                }
                sb.append(" ") //separator
            }
            //replace last character (not needed separator) with newline
            sb.replace(sb.length - 1, sb.length, "\n")
        }
        sb.deleteAt(sb.length - 1)//delete last newline
        return sb.toString()
    }
}
