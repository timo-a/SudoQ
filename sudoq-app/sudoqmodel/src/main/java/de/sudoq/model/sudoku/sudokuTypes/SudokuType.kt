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
 * Ein SudokuType repräsentiert die Eigenschaften eines spezifischen Sudoku-Typs. Dazu gehören insbesondere die
 * Constraints, die einen Sudoku-Typ beschreiben.
 */
open class SudokuType : Iterable<Constraint?>, ComplexityFactory, Xmlable {
    /**
     * Gibt das enum dieses Typs zurück.
     * @return Enum dieses Typs
     */
    /** Attributes  */
    open var enumType: SudokuTypes? = null

    /** The ratio of fields that are to be allocated i.e. already filled when starting  a sudoku game  */
    var standardAllocationFactor = 0f
    /**
     * Gibt die Größe eines Sudokus dieses Typs zurück. Die Größe wird durch ein Position-Objekt repräsentiert, wobei
     * die x-Koordinate die maximale Anzahl horizontaler Felder eines Sudokus dieses Typs beschreibt, die y-Koordinaten
     * die maximale Anzahl vertikaler Felder.
     *
     * @return Ein Position-Objekt, welches die maximale Anzahl horizontaler bzw. vertikaler Felder eines Sudokus dieses
     * Typs beschreibt
     */
    /**
     * Ein Positionobjekt das in seinen Koordinaten die Anzahl an Spalten und Zeilen hält
     */
    var size: Position? = null
        protected set

    /**
     * The Dimensions of one quadratic block, i.e. for a normal 9x9 Sudoku: 3,3.
     * But for Squiggly or Stairstep: 0,0
     * and for 4x4: 2,2
     */
    var blockSize = Position.get(0, 0)
        protected set

    /**
     * Die Anzahl von Symbolen die in die Felder eines Sudokus dieses Typs eingetragen werden können.
     */
    private var numberOfSymbols = 0

    /**
     * Die Liste der Constraints, die diesen Sudoku-Typ beschreiben
     */
    var constraints: MutableList<Constraint>

    /**
     * Alle Positions die in Teil eines Constraints sind d.h. auf ein Feld verweisen
     * (Bei Samurai sind das ja nicht alle)
     */
    protected var positions: MutableList<Position>
    /**
     * Gibt eine Liste mit zulässigen Transformationen an diesem Sudoku aus.
     *
     * @return eine Liste mit zulässigen Transformationen an diesem Sudoku
     */
    /**
     * eine List die zulässige Transformationen am Sudokutyp hält
     */
    var permutationProperties: List<PermutationProperties>
    protected var helperList: MutableList<Helpers>
    var ccb: ComplexityConstraintBuilder

    constructor() {
        constraints = ArrayList()
        positions = ArrayList()
        permutationProperties = SetOfPermutationProperties()
        helperList = ArrayList()
        ccb = ComplexityConstraintBuilder()
    }

    /**
     * Konstruktor für einen SudokuTyp
     *
     *
     * @param width
     * width of the sudoku in fields
     * @param height
     * height of the sudoku in fields
     * @param numberOfSymbols
     * die Anzahl an Symbolen die dieses Sudoku verwendet
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
    /* Methods */
    /**
     * Überprüft, ob das spezifizierte Sudoku die Vorgaben aller Constraints dieses SudokuTyps erfüllt. Ist dies der
     * Fall, so wir true zurückgegeben. Erfüllt es die Vorgaben nicht, oder wird null übergeben, so wird false
     * zurückgegeben.
     *
     * @param sudoku
     * Das Sudoku, welches auf Erfüllung der Constraints überprüft werden soll
     * @return true, falls das Sudoku alle Constraints erfüllt, false falls es dies nicht tut oder null ist
     */
    fun checkSudoku(sudoku: Sudoku?): Boolean {
        if (sudoku == null) return false
        for (c in constraints) {
            if (!c.isSaturated(sudoku)) return false
        }
        return true
    }

    /**
     * Gibt einen Iterator für die Constraints dieses Sudoku-Typs zurück.
     *
     * @return Einen Iterator für die Constraints dieses Sudoku-Typs
     */
    override fun iterator(): MutableIterator<Constraint> {
        return constraints.iterator()
    }

    private inner class Positions : Iterable<Position?> {
        override fun iterator(): MutableIterator<Position> {
            return positions.iterator()
        }
    }//return positions.iterator();

    /**
     * Returns an iterator over all valid positions in this type.
     * valid meaning a position that appears in a constraint
     * @return all positions
     */
    val validPositions: Iterable<Position>
        get() =//return positions.iterator();
            Positions()

    /**
     * Gibt die Anzahl der Symbole eines Sudokus dieses Typs zurück.
     *
     * @return Die Anzahl der Symbole in einem Sudoku dieses Typs
     */
    fun getNumberOfSymbols(): Int {
        return numberOfSymbols
    }

    /**
     * returns a (monotone) Iterable over all symbols in this type starting at 0, for use in for each loops
     * @return a (monotone) Iterable over all symbols in this type starting at 0
     */
    val symbolIterator: Iterable<Int>
        get() = object : AbstractList<Int>() {
            override fun get(index: Int): Int {
                return index
            }

            override fun size(): Int {
                return numberOfSymbols
            }
        }

    /**
     * Gibt einen ComplexityContraint für eine Schwierigkeit complexity zurück.
     *
     * @param complexity
     * eine Schwierigkeit zu der ein ComplexityConstraint erzeugt werden soll
     */
    override fun buildComplexityConstraint(complexity: Complexity): ComplexityConstraint? {
        return ccb.getComplexityConstraint(complexity)
    }

    /**
     * Gibt den Standard Belegungsfaktor zurück
     */
    override fun getStandardAllocationFactor(): Float {
        return standardAllocationFactor //0.35f; TODO WHY DID I SET A CONST. VALUE?! REALLY?!
    }

    /**
     * Gibt den Sudoku-Typ als String zurück.
     *
     * @return Sudoku-Typ als String
     */
    override fun toString(): String {
        return "" + enumType
    }

    /**
     * Setzt den Typ auf den spezifizierten Wert.
     * @param type Typ
     */
    fun setTypeName(type: SudokuTypes?) {
        if (type != null) enumType = type
    }

    fun setDimensions(p: Position?) {
        size = p
    }

    fun setNumberOfSymbols(numberOfSymbols: Int) {
        if (numberOfSymbols > 0) this.numberOfSymbols = numberOfSymbols
    }

    /**
     * @return Eine Liste der Constraints dieses Sudokutyps.
     */
    @Deprecated(""" Gibt eine Liste der Constraints, welche zu diesem Sudokutyp gehören zurück. Hinweis: Wenn möglich stattdessen den
	  Iterator benutzen.
	  
	  """)
    fun getConstraints(): ArrayList<Constraint> {
        return constraints as ArrayList<Constraint>
    }

    //make a method that returns an iterator over all positions !=null. I think we need this a lot
    fun addConstraint(c: Constraint?) {
        if (c != null) {
            constraints.add(c)
        }
    }

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("sudokutype")
        representation.addAttribute(XmlAttribute("typename", "" + enumType!!.ordinal))
        representation.addAttribute(XmlAttribute("numberOfSymbols", "" + numberOfSymbols))
        representation.addAttribute(XmlAttribute("standardAllocationFactor", java.lang.Float.toString(standardAllocationFactor)))
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
        enumType = SudokuTypes.values()[xmlTreeRepresentation.getAttributeValue("typename").toInt()]
        numberOfSymbols = xmlTreeRepresentation.getAttributeValue("numberOfSymbols").toInt()
        standardAllocationFactor = xmlTreeRepresentation.getAttributeValue("standardAllocationFactor").toFloat()
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
                    val jterator = sub.attributes
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
        fun getSudokuType(type: SudokuTypes?): SudokuType? {
            if (type == null) {
                return null
            }
            val f = FileManager.getSudokuTypeFile(type)
            if (!f.exists()) {
                return null
            }
            val helper = XmlHelper()
            try {
                val t = SudokuType()
                val xt = helper.loadXml(f)
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