package de.sudoq.model.persistence.xml.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypeProvider
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable2
import de.sudoq.model.xml.Xmlable3
import java.io.File
import java.util.*

class SudokuBE() : Xmlable3<SudokuTypeBE> {

    var id: Int = 0

    var transformCount = 0

    var sudokuType: SudokuType? = null

    var complexity: Complexity? = null

    var cells: HashMap<Position, Cell>? = null

    //todo switch noargs and 5args constructor
    constructor(
        id: Int,
        transformCount: Int,
        sudokuType: SudokuType,
        complexity: Complexity,
        cells: HashMap<Position, Cell>
    ) : this() {
        this.id = id
        this.transformCount = transformCount
        this.sudokuType = sudokuType
        this.complexity = complexity
        this.cells = cells
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
    override fun fillFromXml(xmlTreeRepresentation: XmlTree, sudokuTypeRepo: IRepo<SudokuTypeBE>) {
        // initialisation
        var cellIdCounter = 1
        cells = HashMap()
        id = try {
            xmlTreeRepresentation.getAttributeValue("id")!!.toInt()
        } catch (e: NullPointerException) {
            -1
        } catch (e: NumberFormatException) {
            -1
        }
        val enumType =
            SudokuTypes.values()[xmlTreeRepresentation.getAttributeValue("type")!!.toInt()]
        sudokuType = SudokuTypeProvider.getSudokuType(enumType, sudokuTypeRepo)
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
                cells!![pos] = cell
                cellIdCounter++
            }
        }
    }

}