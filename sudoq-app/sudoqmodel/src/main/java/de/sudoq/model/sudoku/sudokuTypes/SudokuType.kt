/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.files.FileManager
import de.sudoq.model.solverGenerator.solver.helper.Helpers
import de.sudoq.model.sudoku.*
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.model.sudoku.complexity.ComplexityFactory
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*

/**
 * A SudokuType represents the Attributes of a specific sudoku type.
 * This includes especially the Constraints that describe a sudoku type.
 */
open class SudokuType : Iterable<Constraint>, ComplexityFactory, Xmlable {

    /** Enum holding this Type */
    open var enumType: SudokuTypes? = null //TODO make fillFromXML statis - make this nonnullable

    /** The ratio of fields that are to be allocated i.e. already filled when starting  a sudoku game  */
    @JvmField
    var standardAllocationFactor : Float = 0f

    /**
     * Gibt den Standard Belegungsfaktor zurück
     */
    override fun getStandardAllocationFactor(): Float {
        return standardAllocationFactor //this is needed explicitly because of ComplexityFactory TODO combine with field one everything is kotlin
    }

    /**
     * The size of the sudoku as a [Position] object where
     * the x-coordinate is the maximum number of (columns) horizontal cells and
     * the y-coordinate is the maximum number of (rows) vertical cells.
     */
    var size: Position? = null
        protected set

    /**
     * The Dimensions of one quadratic block, i.e. for a normal 9x9 Sudoku: 3,3.
     * But for Squiggly or Stairstep: 0,0
     * and for 4x4: 2,2
     */
    var blockSize: Position = Position.get(0, 0)
        protected set

    /**
     * Number of symbols that can be entered in a cell.
     */
    var numberOfSymbols = 0
        private set

    /**
     * The list of constraints for this sudoku type
     */
    @JvmField
    var constraints: MutableList<Constraint>

    /**
     * All [Position]s that are contained in at least one constraint.
     * (For a Samurai sudoku not all positions are part of a constraint)
     */
    protected var positions: MutableList<Position>

    /**
     * list of admissible permutations for this sudoku type
     */
    var permutationProperties: List<PermutationProperties>

    protected var helperList: MutableList<Helpers>
    @JvmField
    var ccb: ComplexityConstraintBuilder

    constructor() {
        constraints = ArrayList()
        positions = ArrayList()
        permutationProperties = SetOfPermutationProperties()
        helperList = ArrayList()
        ccb = ComplexityConstraintBuilder()
    }

    /**
     * Creates a SudokuType
     *
     * @param width width of the sudoku in cells
     * @param height height of the sudoku in cells
     * @param numberOfSymbols the number of symbols that can be used in this sudoku
     */
    constructor(width: Int, height: Int, numberOfSymbols: Int) {
        require(numberOfSymbols >= 0) { "Number of symbols < 0 : $numberOfSymbols" }
        require(width >= 0) { "Sudoku width < 0 : $width" }
        require(height >= 0) { "Sudoku height < 0 : $height" }
        enumType = null
        standardAllocationFactor = -1.0f
        this.numberOfSymbols = numberOfSymbols
        size = Position.get(width, height)

        constraints = ArrayList()
        positions = ArrayList()
        permutationProperties = SetOfPermutationProperties()
        helperList = ArrayList()
        ccb = ComplexityConstraintBuilder()
    }

    /**
     * Checks if the passed [Sudoku] satisfies all [Constraint]s of this [SudokuType].
     *
     * @param sudoku Sudoku to check for constraint satisfaction

     * @return true, iff the sudoku satisfies all constraints
     */
    fun checkSudoku(sudoku: Sudoku): Boolean {
        for (c in constraints) {
            if (!c.isSaturated(sudoku))
                return false
        }
        return true
    }

    /**
     * Returns an Iterator over the [Constraint]s of this sudoku type.
     *
     * @return Iterator over the [Constraint]s of this sudoku type
     */
    override fun iterator(): Iterator<Constraint> {
        return constraints.iterator()
    }

    private inner class Positions : Iterable<Position> {
        override fun iterator(): Iterator<Position> {
            return positions.iterator()
        }
    }

    /**
     * Returns an iterator over all valid positions in this type.
     * valid meaning a position that appears in a constraint
     * @return all positions
     */
    val validPositions: Iterable<Position>
        get() = Positions()


    /**
     * returns a (monotone) Iterable over all symbols in this type starting at 0, for use in for each loops
     * @return a (monotone) Iterable over all symbols in this type starting at 0
     */
    val symbolIterator: Iterable<Int>
        get() = object : AbstractList<Int>() {
            override fun get(index: Int): Int {
                return index
            }

            override val size: Int
                get() = numberOfSymbols
        }

    /**
     * Returns a complexity constraint for a complexity.
     *
     * @param complexity Complexity for which to return a ComplexityConstraint
     */
    override fun buildComplexityConstraint(complexity: Complexity?): ComplexityConstraint? {
        return ccb.getComplexityConstraint(complexity)
    }


    /**
     * Returns the sudoku type as string.
     *
     * @return sudoku type as string
     */
    override fun toString(): String {
        return "" + enumType
    }

    /**
     * Sets the type
     * @param type Type
     */
    fun setTypeName(type: SudokuTypes) {
        enumType = type
    }

    fun setDimensions(p: Position) {
        size = p
    }

    fun setNumberOfSymbols(numberOfSymbols: Int) {
        if (numberOfSymbols > 0) this.numberOfSymbols = numberOfSymbols
    }

    /**
     * @return A list of the [Constraint]s of this SudokuType.
     */
    @Deprecated(""" Gibt eine Liste der Constraints, welche zu diesem Sudokutyp gehören zurück. Hinweis: Wenn möglich stattdessen den
	  Iterator benutzen.
	  
	  """)
    fun getConstraints(): ArrayList<Constraint> {
        return constraints as ArrayList<Constraint>
    }

    //make a method that returns an iterator over all positions !=null. I think we need this a lot
    fun addConstraint(c: Constraint) {
        constraints.add(c)
    }

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("sudokutype")
        representation.addAttribute(XmlAttribute("typename", "" + enumType!!.ordinal))
        representation.addAttribute(XmlAttribute("numberOfSymbols", "" + numberOfSymbols))
        representation.addAttribute(XmlAttribute("standardAllocationFactor", standardAllocationFactor.toString()))
        representation.addChild(size!!.toXmlTree("size"))
        representation.addChild(blockSize.toXmlTree("blockSize"))
        for (c in constraints) {
            representation.addChild(c.toXmlTree())
        }
        representation.addChild((permutationProperties as SetOfPermutationProperties).toXmlTree())
        val hList = XmlTree("helperList")
        for (i in helperList.indices) {
            hList.addAttribute(XmlAttribute("i", "" + helperList[i].ordinal))
        }
        representation.addChild(hList)
        representation.addChild(ccb.toXmlTree())

        // TODO complexity builderdata
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        enumType = SudokuTypes.values()[xmlTreeRepresentation.getAttributeValue("typename")!!.toInt()]
        numberOfSymbols = xmlTreeRepresentation.getAttributeValue("numberOfSymbols")!!.toInt()
        standardAllocationFactor = xmlTreeRepresentation.getAttributeValue("standardAllocationFactor")!!.toFloat()
        for (sub in xmlTreeRepresentation) {
            when (sub.name) {
                "size" -> size = Position.fillFromXmlStatic(sub)
                "blockSize" -> blockSize = Position.fillFromXmlStatic(sub)
                "constraint" -> {
                    val c = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE)
                    c.fillFromXml(sub)
                    constraints.add(c)
                }
                SetOfPermutationProperties.SET_OF_PERMUTATION_PROPERTIES -> {
                    permutationProperties = SetOfPermutationProperties()
                    (permutationProperties as SetOfPermutationProperties).fillFromXml(sub) //cast neccessary because setOPP is defined as 
                }
                "helperList" -> {
                    helperList = ArrayList(sub.numberOfAttributes)
                    val jterator = sub.getAttributes()
                    while (jterator.hasNext()) {
                        val xa = jterator.next()
                        val index = xa.name.toInt()
                        val h = Helpers.values()[xa.value.toInt()]
                        helperList[index] = h
                    }
                }
                ComplexityConstraintBuilder.TITLE -> {
                    ccb = ComplexityConstraintBuilder()
                    ccb.fillFromXml(sub)
                }
                else -> {
                }
            }
        }
        initPositionsList()
    }

    private fun initPositionsList() {
        positions = ArrayList()
        for (c in constraints) for (p in c) if (!positions.contains(p)) positions.add(p)
    }

    companion object {
        @JvmStatic
        fun getSudokuType(type: SudokuTypes): SudokuType? {
            val f = FileManager.getSudokuTypeFile(type)
            if (!f.exists()) {
                return null
            }
            val helper = XmlHelper()
            try {
                val t = SudokuType()
                val xt = helper.loadXml(f)!!
                t.fillFromXml(xt)
                return t
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        val sudokuTypeIds: List<Int>
            get() {
                val ids: MutableList<Int> = ArrayList()
                val f = FileManager.getSudokuDir()
                for (id in f.listFiles()) {
                    if (id.isDirectory) {
                        ids.add(id.name.toInt())
                    }
                }
                return ids
            }
    }
}