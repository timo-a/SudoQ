/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.ModelChangeListener
import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypeProvider
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import de.sudoq.model.xml.Xmlable2
import java.io.File
import java.util.*
import kotlin.collections.Iterable
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

/**
 * This class represents a Sudoku with mit seinem Typ, seinen Feldern und seinem Schwierigkeitsgrad.
 */
open class Sudoku : ObservableModelImpl<Cell>, Iterable<Cell>, Xmlable2, ModelChangeListener<Cell> {

    /** An ID uniquely identifying the Sudoku */
    var id : Int = 0

    /** Counts how often the Sudokuw was already transformed */
    var transformCount = 0
        private set

    /** Eine Map, welche jeder Position des Sudokus ein Feld zuweist */
	@JvmField
	var cells: HashMap<Position, Cell>? = null

    private var cellIdCounter = 0
    private var cellPositions: MutableMap<Int, Position>? = null

    /** The Type of the Sudoku */
    var sudokuType: SudokuType? = null
        private set

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
    constructor(type: SudokuType,
                map: PositionMap<Int>? = PositionMap((type.size)!!),
                setValues: PositionMap<Boolean>? = PositionMap((type.size)!!)) {
        cellIdCounter = 1
        cellPositions = HashMap()
        sudokuType = type
        cells = HashMap()

        // iterate over the constraints of the type and create the fields
        for (constraint in type) {
            for (position in constraint) {
                if (!cells!!.containsKey(position)) {
                    var f: Cell
                    val solution = map?.get(position)
                    f = if (solution != null) {
                        val editable = setValues == null
                                || setValues[position] == null
                                || setValues[position] == false
                        Cell(editable, solution, cellIdCounter, type.numberOfSymbols)
                    } else {
                        Cell(cellIdCounter, type.numberOfSymbols)
                    }
                    cells!![position] = f
                    cellPositions!![cellIdCounter++] = position
                    f.registerListener(this)
                }
            }
        }
    }

    /** Creates a completely empty sudoku that has to be filled */
    @Deprecated("DO NOT USE THIS METHOD (if you are not from us)")
    internal constructor() {
        id = -1
    }

    /** increases transform count by one */
    fun increaseTransformCount() {
        transformCount++
    }

    /**
     * Returns the [Cell] at the specified [Position].
     * If the position is not mapped to a cell, null is returned
     *
     * @param position Position of the cell
     * @return Cell at the [Position] or null if it is not mapped to a [Cell].
     */
    fun getCell(position: Position): Cell? {
        //todo refactor so that a miss leads to illegal args exception
        return cells!![position]
    }

    /**
     * Returns the [Cell] at the id.
     *
     * @param id ID of the [Cell] to return
     * @return the [Cell] at the specified id or null of id not found
     */
    fun getCell(id: Int): Cell? {
        return getCell(cellPositions!![id]!!)
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

        var p : Position = cellPositions!![id] ?: return false

        return cells?.get(p) != null

    }

    /**
     * Returns the [Position] of the [Cell] if the given id.
     * Ist id noch nicht vergeben wird null zurückgegeben
     *
     * @param id ID of the Cell of the Position to return
     * @return the [Position] of the id or null if id not found
     */
    fun getPosition(id: Int): Position? {
        return cellPositions!![id]
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
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("sudoku")
        if (id > 0) {
            representation.addAttribute(XmlAttribute("id", "" + id))
        }
        representation.addAttribute(XmlAttribute("transformCount", "" + transformCount))
        representation.addAttribute(XmlAttribute("type", "" + sudokuType!!.enumType!!.ordinal))
        if (complexity != null) {
            representation.addAttribute(XmlAttribute("complexity", "" + complexity!!.ordinal))
        }
        for ((key, value) in cells!!) {
            val fieldmap = XmlTree("fieldmap")
            fieldmap.addAttribute(XmlAttribute("id", "" + value.id))
            fieldmap.addAttribute(XmlAttribute("editable", "" + value.isEditable))
            fieldmap.addAttribute(XmlAttribute("solution", "" + value.solution))
            val position = XmlTree("position")
            position.addAttribute(XmlAttribute("x", "" + key.x))
            position.addAttribute(XmlAttribute("y", "" + key.y))
            fieldmap.addChild(position)
            representation.addChild(fieldmap)
        }
        return representation
    }

    /**
     * {@inheritDoc}
     */
    override fun fillFromXml(xmlTreeRepresentation: XmlTree, sudokuDir: File) {
        // initialisation
        cellIdCounter = 1
        cellPositions = HashMap()
        cells = HashMap()
        id = try {
            xmlTreeRepresentation.getAttributeValue("id")!!.toInt()
        } catch (e: NullPointerException) {
            -1
        } catch (e: NumberFormatException) {
           -1
        }
        val enumType = SudokuTypes.values()[xmlTreeRepresentation.getAttributeValue("type")!!.toInt()]
        sudokuType = SudokuTypeProvider.getSudokuType(enumType, sudokuDir)
        transformCount = xmlTreeRepresentation.getAttributeValue("transformCount")!!.toInt()
        val compl = xmlTreeRepresentation.getAttributeValue("complexity")
        complexity = if (compl == null) null else Complexity.values()[compl.toInt()]

        // build the fields
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "fieldmap") {
                val fieldId = sub.getAttributeValue("id")!!.toInt()
                val editable = java.lang.Boolean.parseBoolean(sub.getAttributeValue("editable"))
                val solution = sub.getAttributeValue("solution")!!.toInt()
                var x = -1
                var y = -1
                // check if there is only one child element
                require(sub.numberOfChildren == 1)
                val position = sub.getChildren().next()
                if (position.name == "position") {
                    x = position.getAttributeValue("x")!!.toInt()
                    y = position.getAttributeValue("y")!!.toInt()
                }
                val pos = Position[x, y]
                val cell = Cell(editable, solution, fieldId, sudokuType!!.numberOfSymbols)
                cell.registerListener(this)
                cells!![pos] = cell
                cellPositions!![fieldId] = pos
                cellIdCounter++
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onModelChanged(changedCell: Cell) {
        notifyListeners(changedCell)
    }



    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (other != null && other is Sudoku) {
            val complexityMatch = complexity === other.complexity
            val typeMatch = sudokuType!!.enumType === other.sudokuType!!.enumType
            var fieldsMatch = true
            for (f in cells!!.values) {
                if (!other.hasCell(f.id) || f != other.getCell(f.id)){
                    fieldsMatch = false
                    break
                }
            }
            return complexityMatch && typeMatch && fieldsMatch
        }
        return false
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
        val OFFSET = if (sudokuType!!.numberOfSymbols < 10) "" else " "
        val EMPTY = if (sudokuType!!.numberOfSymbols < 10) "x" else "xx"
        val NONE = if (sudokuType!!.numberOfSymbols < 10) " " else "  "
        for (j in 0 until sudokuType!!.size!!.y) {
            for (i in 0 until sudokuType!!.size!!.x) {
                val f = getCell(Position[i, j])
                var op: String
                if (f != null) { //feld existiert
                    val value = f.currentValue
                    op = if (value == -1) EMPTY else if (value < 10) OFFSET + value else value.toString() + ""
                    sb.append(op)
                } else {
                    sb.append(NONE)
                }
                sb.append(" ") //separator
            }
            sb.replace(sb.length - 1, sb.length, "\n")
        }
        sb.delete(sb.length - 1, sb.length)
        return sb.toString()
    }


}