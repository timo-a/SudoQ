/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.solverGenerator.solver.helper.Helpers
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.model.sudoku.complexity.ComplexityFactory
import java.util.*
import kotlin.collections.ArrayList

/**
 * A SudokuType represents the Attributes of a specific sudoku type.
 * This includes especially the Constraints that describe a sudoku type.
 */
open class SudokuType : Iterable<Constraint>, ComplexityFactory {

    /** Enum holding this Type */
    open var enumType: SudokuTypes? = null
    //TODO make fillFromXML statis - make this nonnullable

    /** The ratio of fields that are to be allocated i.e. already filled when starting  a sudoku game  */
    @JvmField
    var standardAllocationFactor: Float = 0f

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
    protected lateinit var positions: MutableList<Position>

    /**
     * list of admissible permutations for this sudoku type
     */
    var permutationProperties: List<PermutationProperties>

    var helperList: MutableList<Helpers>

    @JvmField
    var ccb: ComplexityConstraintBuilder


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
        permutationProperties = ArrayList()
        helperList = ArrayList()
        ccb = ComplexityConstraintBuilder()
    }

    /**
     * used to initialize from SudokuTypeBE
     */
    constructor(
        enumType: SudokuTypes,
        numberOfSymbols: Int,
        standardAllocationFactor: Float,
        size: Position,
        blockSize: Position,
        constraints: MutableList<Constraint>,
        permutationProperties: List<PermutationProperties>,
        helperList: MutableList<Helpers>,
        ccb: ComplexityConstraintBuilder
    ) {
        this.enumType = enumType
        this.numberOfSymbols = numberOfSymbols
        this.standardAllocationFactor = standardAllocationFactor
        this.size = size
        this.blockSize = blockSize
        this.constraints = constraints
        this.permutationProperties = permutationProperties
        this.helperList = helperList
        this.ccb = ccb
        initPositionsList()
    }

    /**
     * Checks if the passed [Sudoku] satisfies all [Constraint]s of this [SudokuType].
     *
     * @param sudoku Sudoku to check for constraint satisfaction

     * @return true, iff the sudoku satisfies all constraints
     */
    fun checkSudoku(sudoku: Sudoku): Boolean {
        return constraints.all { it.isSaturated(sudoku) }
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
    @Deprecated(
        "Gibt eine Liste der Constraints, welche zu diesem Sudokutyp gehören zurück. " +
                "Hinweis: Wenn möglich stattdessen den Iterator benutzen.",
        ReplaceWith(
            "iterator()",
            "kotlin.collections.Iterator",
            "de.sudoq.model.sudoku.Constraint"
        )
    )
    fun getConstraints(): ArrayList<Constraint> {
        return constraints as ArrayList<Constraint>
    }

    //make a method that returns an iterator over all positions !=null. I think we need this a lot
    fun addConstraint(c: Constraint) {
        constraints.add(c)
    }

    private fun initPositionsList() {
        positions = ArrayList()
        for (c in constraints) for (p in c) if (!positions.contains(p)) positions.add(p)
    }


}