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
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*
import kotlin.collections.Iterable
import kotlin.collections.MutableIterator
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

/**
 * Diese Klasse repräsentiert ein Sudoku mit seinem Typ, seinen Feldern und seinem Schwierigkeitsgrad.
 */
open class Sudoku : ObservableModelImpl<Cell?>, Iterable<Cell?>, Xmlable, ModelChangeListener<Cell?>, Cloneable {
    /* Attributes */
    /**
     * Eine Identifikationsnummer, die ein Sudoku eindeutig identifiziert
     */
    private var id = 0

    /**
     * Gibt an wie oft dieses Sudoku bereits transformiert wurde
     *
     * @return die anzahl der Transformationen
     */
    var transformCount = 0
        private set

    /**
     * Eine Map, welche jeder Position des Sudokus ein Feld zuweist
     */
    var cells: HashMap<Position, Cell>? = null
    private var cellIdCounter = 0
    private var cellPositions: MutableMap<Int, Position>? = null
    /**
     * Gibt den Typ dieses Sudokus zurück.
     *
     * @return Der Typ dieses Sudokus
     */
    /**
     * Der Typ dieses Sudokus
     */
    var sudokuType: SudokuType? = null
        private set

    /**
     * Der Schwierigkeitsgrad dieses Sudokus
     */
    private var complexity: Complexity? = null
    /**
     * Instanziiert ein Sudoku-Objekt mit dem spezifizierten SudokuType. Ist dieser null, so wird eine
     * IllegalArgumentException geworfen.
     *
     * @param type
     * Der Typ des zu erstellenden Sudokus
     * @param map
     * Eine Map von Positions auf Lösungswerte. Werte in vorgegebenen Feldern sind verneint. (nicht negiert,
     * sondern bitweise verneint)
     * @param setValues
     * Eine Map wo jede Position mit dem Wert true einen vorgegebenen Wert markiert
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene Typ null ist
     */
    /* Constructors */
    /**
     * Instanziiert ein Sudoku-Objekt mit dem spezifizierten SudokuType. Ist dieser null, so wird eine
     * IllegalArgumentException geworfen. Alle Felder werden als editierbar ohne vorgegebene Lösung gesetzt.
     *
     * @param type
     * Der Typ des zu erstellenden Sudokus
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene Typ null ist
     */
    @JvmOverloads
    constructor(type: SudokuType?, map: PositionMap<Int?>? = PositionMap((if (type == null) Position[1, 1] else type.size)!!), setValues: PositionMap<Boolean?>? =  //TODO warum so kompliziert? wenn type == null fliegt eh eine exception
            PositionMap((if (type == null) Position[1, 1] else type.size)!!)) {
        requireNotNull(type)
        cellIdCounter = 1
        cellPositions = HashMap()
        sudokuType = type
        cells = HashMap()
        complexity = null

        // iterate over the constraints of the type and create the fields
        for (constraint in type) {
            for (position in constraint) {
                if (!cells!!.containsKey(position)) {
                    var f: Cell
                    val solution = map?.get(position)
                    f = if (solution != null) {
                        val editable = setValues == null || setValues[position] == null || setValues[position] == false
                        Cell(editable, solution, cellIdCounter, type.numberOfSymbols)
                    } else {
                        Cell(cellIdCounter, type.numberOfSymbols)
                    }
                    cells!![position] = f
                    cellPositions[cellIdCounter++] = position
                    f.registerListener(this)
                }
            }
        }
    }

    /**
     * Erzeugt ein vollständig leeres Sudoku, welches noch gefüllt werden muss. DO NOT USE THIS METHOD (if you are not
     * from us)
     */
    internal constructor() {
        id = -1
    }
    /* Methods */
    /**
     * Gibt die id dieses Sudokus zurueck
     *
     * @return die id
     */
    fun getId(): Int {
        return id
    }

    /**
     * Zaehlt den transform Counter um 1 hoch
     */
    fun increaseTransformCount() {
        transformCount++
    }

    /**
     * Gibt das Feld, welches sich an der spezifizierten Position befindet zurück. Ist position null oder in diesem
     * Sudoku unbelegt, so wird null zurückgegeben.
     *
     * @param position
     * Die Position, dessen Feld abgefragt werden soll
     * @return Das Feld an der spezifizierten Position oder null, falls dies nicht existiert oder null übergeben wurde
     */
    fun getCell(position: Position?): Cell? {
        return if (position == null) null else cells!![position]
    }

    /**
     * Belegt die spezifizierte Position mit einem neuen Field.
     * Falls field oder position null sind, bricht die Methode ab
     *
     * @param cell
     * das neue Field
     * @param position
     * die Position des neuen Fields
     */
    fun setCell(cell: Cell?, position: Position?) {
        if (cell == null || position == null) return
        cells!![position] = cell
        cellPositions!![cell.id] = position
    }

    /**
     * Gibt das Feld, das die gegebene id hat zurück. Ist id noch nicht vergeben wird null zurückgegeben
     *
     * @param id
     * Die id des Feldes das ausgegeben werden soll
     * @return Das Feld an der spezifizierten Position oder null, falls dies nicht existiert oder die id ungültig war
     */
    fun getCell(id: Int): Cell? {
        return getCell(cellPositions!![id])
    }

    /**
     * Gibt die Position des Feldes, das die gegebene id hat zurück. Ist id noch nicht vergeben wird null zurückgegeben
     *
     * @param id
     * Die id des Feldes der Position die ausgegeben werden soll
     * @return Die spezifizierte Position oder null, falls diese nicht existiert oder die id ungültig war
     */
    fun getPosition(id: Int): Position? {
        return cellPositions!![id]
    }

    /**
     * Gibt einen Iterator zurück, mithilfe dessen über alle Felder dieses Sudokus iteriert werden kann.
     *
     * @return Ein Iterator mit dem über alle Felder dieses Sudokus iteriert werden kann
     */
    override fun iterator(): MutableIterator<Cell> {
        return cells!!.values.iterator()
    }

    /**
     * Gibt den Schwierigkeitsgrad dieses Sudokus zurück.
     *
     * @return Der Schwierigkeitsgrad dieses Sudokus
     */
    fun getComplexity(): Complexity? {
        return complexity
    }

    /**
     * Setzt den Schwierigkeitsgrad dieses Sudokus auf den Spezifizierten. Ist dieser ungültig so wird nichts getan.
     *
     * @param complexity
     * Der Schwierigkeitsgrad auf den dieses Sudoku gesetzt werden soll
     */
    fun setComplexity(complexity: Complexity?) {
        this.complexity = complexity
    }

    /**
     * Gibt an, ob das Sudoku vollstaendig ausgefuellt und korrekt geloest ist.
     *
     * @return true, falls das Sudoku ausgefüllt und gelöst ist, sonst false
     */
    open val isFinished: Boolean
        get() {
            var allCorrect = true
            for (cell in cells!!.values) allCorrect = allCorrect and cell.isSolvedCorrect
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
            representation.addAttribute(XmlAttribute("complexity", "" + getComplexity()!!.ordinal))
        }
        for ((key, value) in cells!!) {
            if (value != null) {
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
        }
        return representation
    }

    /**
     * {@inheritDoc}
     */
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        // initialisation
        cellIdCounter = 1
        cellPositions = HashMap()
        cells = HashMap()
        id = try {
            xmlTreeRepresentation.getAttributeValue("id").toInt()
        } catch (e: NumberFormatException) {
            -1
        }
        val enumType = SudokuTypes.values()[xmlTreeRepresentation.getAttributeValue("type").toInt()]
        sudokuType = SudokuBuilder.createType(enumType)
        transformCount = xmlTreeRepresentation.getAttributeValue("transformCount").toInt()
        val compl = xmlTreeRepresentation.getAttributeValue("complexity")
        complexity = if (compl == null) null else Complexity.values()[compl.toInt()]

        // build the fields
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "fieldmap") {
                val fieldId = sub.getAttributeValue("id").toInt()
                val editable = java.lang.Boolean.parseBoolean(sub.getAttributeValue("editable"))
                val solution = sub.getAttributeValue("solution").toInt()
                var x = -1
                var y = -1
                // check if there is only one child element
                require(sub.numberOfChildren == 1)
                val position = sub.children.next()
                if (position.name == "position") {
                    x = position.getAttributeValue("x").toInt()
                    y = position.getAttributeValue("y").toInt()
                }
                val pos = Position[x, y]
                val cell = Cell(editable, solution, fieldId, sudokuType.numberOfSymbols)
                cell.registerListener(this)
                cells!![pos] = cell
                cellPositions[fieldId] = pos
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
     * Setzt die Identifikationsnummer des Sudokus.
     *
     * @param id
     * Die eindeutige Identifikationsnummer
     */
    fun setId(id: Int) {
        this.id = id
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(obj: Any?): Boolean {
        if (obj != null && obj is Sudoku) {
            val other = obj
            val complexityMatch = complexity === other.getComplexity()
            val typeMatch = sudokuType!!.enumType === other.sudokuType!!.enumType
            var fieldsMatch = true
            for (f in cells!!.values) fieldsMatch = fieldsMatch and f.equals(other.getCell(f.id))
            return complexityMatch && typeMatch && fieldsMatch
        }
        return false
    }

    /**
     * Gibt zurück, ob dieses Sudoku in den aktuell gesetzten Werten Fehler enthält, d.h. ob es ein Feld gibt, dessen
     * aktueller Wert nicht der korrekten Lösung entspricht.
     *
     * @return true, falls es in dem Sudoku falsch gelöste Felder gibt, false andernfalls
     */
    open fun hasErrors(): Boolean {
        for (f in cells!!.values) if (!f.isNotWrong) return true
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

    /**
     * creates a perfect clone,
     */
    public override fun clone(): Any {
        val clone = Sudoku(sudokuType)
        clone.id = id
        clone.transformCount = transformCount
        clone.cells = HashMap()
        for ((key, value) in cells!!) clone.cells!![key] = value.clone() as Cell
        clone.cellIdCounter = cellIdCounter
        clone.cellPositions = HashMap(cellPositions)
        clone.complexity = complexity
        return clone
    }
}