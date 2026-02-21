/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType

/**
 * This class represents a Sudoku with mit seinem Typ, seinen Feldern und seinem Schwierigkeitsgrad.
 */
open class Sudoku : Iterable<Cell>, AbstractSudoku<Cell> {

    /** An ID uniquely identifying the Sudoku */
    var id: Int = 0

    /** Counts how often the Sudoku was already transformed */
    var transformCount = 0
        private set

    /** Eine Map, welche jeder Position des Sudokus ein Feld zuweist */
    @JvmField
    var cells: HashMap<Position, Cell>? = null //todo why isn't this a [PositionMap]?

    private var cellPositions: MutableMap<Int, Position>? = null

    /** The Complexity of this Sudoku */
    var complexity: Complexity? = null


    /**
     * All Cells are set as editable.
     *
     * @param type Type of the Sudoku
     * @param map A Map from Positions to solution values. Values in pre-filled Cells are negated. (actually bitwise negated)
     * @param setValues A Map from Position to whether the value is pre-filled.
     */
    @JvmOverloads
    constructor(
        type: SudokuType,
        map: PositionMap<Int>? = PositionMap.Builder<Int>(type.size).build(),
        setValues: PositionMap<Boolean>? = PositionMap.Builder<Boolean>(type.size).build()
    ) : super(type) {
        var cellIdCounter = 1
        cellPositions = HashMap()
        cells = HashMap()

        // iterate over the constraints of the type and create the fields
        type.flatMap(Constraint::getPositions).distinct().forEach { position ->
             var f: Cell
             val solution = if (map?.contains(position) == true) map[position] else null
            f = when {
                solution != null -> {
                    val editable = setValues == null
                            || position !in setValues
                            || !setValues[position]
                    Cell(editable, solution, cellIdCounter, type.numberOfSymbols)
                }
                else -> {
                    Cell(cellIdCounter, type.numberOfSymbols)
                }
            }
            cells!![position] = f
            cellPositions!![cellIdCounter++] = position
        }
    }

    /*init from basic properties. use this to init from BE */
    constructor(
        id: Int,
        transformCount: Int,
        sudokuType: SudokuType,
        complexity: Complexity,
        cells: HashMap<Position, Cell>
    ) : super(sudokuType) {
        this.id = id
        this.transformCount = transformCount
        this.complexity = complexity
        this.cells = cells

        cellPositions = HashMap()
        cells.forEach { (pos, c) -> cellPositions!![c.id] = pos }
    }

    /** increases transform count by one */
    fun increaseTransformCount() {
        transformCount++
    }

    /**
     * Returns the [Cell] at the specified [Position].
     *
     * @param position Position of the cell
     * @return Cell at the [Position]
     * @throws IllegalArgumentException if the position is not mapped to a [Cell].
     */
    fun getCell(position: Position): Cell {
        return requireNotNull(cells!![position])
    }

    /**
     * Returns the [Cell] at the id.
     *
     * @param id ID of the [Cell] to return
     * @return the [Cell] at the specified id
     */
    fun getCell(id: Int): Cell {
        val p = cellPositions!!.getValue(id)
        return getCell(p)
    }

    /**
     * Maps the [Position] to the [Cell]
     * if cell is null nothing happens
     *
     *
     * @param cell the new [Cell]
     * @param position the [Position] for the new Cell
     */
    fun setCell(cell: Cell?, position: Position) {
        //todo cell can be null because samurai transformation needs it -> refactor?
        if (cell == null) return

        cells!![position] = cell
        cellPositions!![cell.id] = position
    }


    /**
     * Checks if the id is mapped to a cell
     */
    fun hasCell(id: Int): Boolean {
        if (cellPositions == null)
            return false

        val p: Position = cellPositions!![id] ?: return false

        return cells?.get(p) != null

    }

    /**
     * Checks if the id is mapped to a cell
     */
    fun hasCell(pos: Position): Boolean {
        if (cellPositions == null)
            return false

        return cells!![pos] != null
    }

    /**
     * Returns the [Position] of the [Cell] if the given id.
     * If there is no such position, an IllegalArgumentException is thrown.
     *
     * @param id ID of the Cell of the Position to return
     * @return the [Position] of the id
     */
    fun getPosition(id: Int): Position {
        return requireNotNull(cellPositions!![id], { "id not found" })
    }

    /**
     * Returns an [Iterator] over the [Cell]s.
     *
     * @return An [Iterator] over the [Cell]s
     */
    override fun iterator(): Iterator<Cell> {
        return cells!!.values.iterator()
    }


    /**
     * Checks if the Sudoku is completely filled and solved correctly.
     *
     * @return true, iff Sudoku is fully filled and solved correctly
     */
    open val isFinished: Boolean
        get() {
            //todo doesn't check for completeness
            var allCorrect = true
            for (cell in cells!!.values)
                if (!cell.isSolvedCorrect) {
                    allCorrect = false
                    break
                }
            return allCorrect
        }

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Sudoku) return false

        val complexityMatch = complexity === other.complexity
        val typeMatch = sudokuType.enumType === other.sudokuType.enumType
        val cellsMatch = cells!!.values.all {
            c -> other.hasCell(c.id) && c == other.getCell(c.id)
        }
        return complexityMatch && typeMatch && cellsMatch
    }

    override fun hashCode(): Int {
        return complexity.hashCode() + sudokuType.enumType.hashCode() + cells!!.hashCode()
    }

    /**
     * Checks if this [Sudoku] has errors, i.e. if there is a [Cell] where the value is not the
     * correct solution.
     *
     * @return true, if there are incorrectly solved cells, false otherwise
     */
    open fun hasErrors(): Boolean {
        for (f in cells!!.values)
            if (!f.isNotWrong)
                return true
        return false

        //return this.fields.values().stream().anyMatch(f -> !f.isNotWrong()); //looks weird but be very careful with simplifications!
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
                    val value = getCell(position).currentValue
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