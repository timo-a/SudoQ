/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
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

/**
 * A SudokuType represents the Attributes of a specific sudoku type.
 * This includes especially the Constraints that describe a sudoku type.
 *
 * @param enumType Enum holding this Type
 * @param numberOfSymbols Number of symbols that can be entered in a cell
 * @param standardAllocationFactor The ratio of fields that are to be allocated i.e. already filled
 * when starting a sudoku game
 * @param size dimensions of the board
 *  - x is the maximum number of (columns) horizontal cells
 *  - y is the maximum number of (rows) vertical cells
 * @param blockSize The dimensions of one quadratic block, e.g.
 *  - for a normal 9x9 Sudoku: 3,3.
 *  - for 4x4: 2,2
 *  - but for Squiggly or Stairstep: 0,0
 * @param constraints The list of constraints for this sudoku type
 * @param permutationProperties The list of permutations for this sudoku type
 * @param helperList The list of helpers for this sudoku type
 *
 */
open class SudokuType(val enumType: SudokuTypes,
                      val numberOfSymbols: Int,  //should be val as should the others
                      private val standardAllocationFactor: Float,
                      val size: Position,
                      val blockSize: Position,
                      val constraints: List<Constraint>,
                      val permutationProperties: List<PermutationProperties>,
                      val helperList: List<Helpers>,
                      val ccb: ComplexityConstraintBuilder
) : Iterable<Constraint>, ComplexityFactory {

    /**
     * Gibt den Standard Belegungsfaktor zurück
     */
    override fun getStandardAllocationFactor(): Float {
        return standardAllocationFactor
    }

    /**
     * All [Position]s that are contained in at least one constraint.
     * (For a Samurai sudoku not all positions are part of a constraint)
     */
    protected val positions: List<Position>

    init {
        require(numberOfSymbols >= 0) { "Number of symbols < 0 : $numberOfSymbols" }
        positions = constraints.flatMap { it.getPositions() }.distinct().toList()
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
        get() = 0 until numberOfSymbols

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
        return enumType.toString() //todo should be distinct from the enum, maybe "$enumType object"?
    }
}
