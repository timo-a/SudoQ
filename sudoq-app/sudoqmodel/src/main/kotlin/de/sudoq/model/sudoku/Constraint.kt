/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import java.util.*

/**
 * A Constraint comprises [Cell]s (or rather their [Position]s) in a Sudoku, so that they have to
 * satisfy certain requirements in the form of a [ConstraintBehavior].
 * In a standard sudoku for example the rows, columns and blocks are [Constraint] objects.
 *
 * @property behavior Describes the constraint that the cells have to satisfy
 * @property type Type of the Constraint
 * @property name Name of the Constraint should start with one of "extra block", "Block", "Column", "Row".
 * @property positions A set of [Position]s, that together satisfy a constraint
 */
class Constraint(private val behavior: ConstraintBehavior,
                 val type: ConstraintType,
                 val name: String,
                 vararg positions: Position
) : Iterable<Position> {

    //we don't want duplicates -> Set, and linkedSetOf preserves the order, just in case
    private val positions: Set<Position> = linkedSetOf(*positions)

    /**
     * Checks if the Sudoku satisfies this Constraint.
     *
     * @param sudoku The [Sudoku] to check for constraint saturation
     * @return true if sudoku satisfies this constraint,
     * false if it doesn't or even if it doesn't contain all the positions in this constraint
     */
    fun isSaturated(sudoku: Sudoku): Boolean {
        return behavior.check(this, sudoku)
    }

    /**
     * An iterator over the [Position]s in this constraint.
     *
     * @return An iterator over the [Position]s in this constraint.
     */
    override fun iterator(): Iterator<Position> {
        return positions.iterator()
    }

    /**
     * Check if the Position is part of the constraint.
     *
     * @param p the position to check.
     * @return true iff position is part of this constraint.
     */
    fun includes(p: Position): Boolean {
        return positions.contains(p)
    }

    /**
     * Number of [Position]s in this Constraint.
     */
    val size: Int
        get() = positions.size

    /**
     * Returns whether this Constraint has [UniqueConstraintBehavior] i.e. no symbol may appear more than once.
     *
     * @return true, if  this Constraint has [UniqueConstraintBehavior], false otherwise
     */
    fun hasUniqueBehavior(): Boolean {
        return behavior is UniqueConstraintBehavior
    }

    /**
     * A short description of this Constraint.
     *
     * @return A short description of this Constraint.
     */
    override fun toString(): String {
        return name
    }

    /**
     * Returns a list of positions in this constraint
     *
     * @return a list of positions in this constraint
     */
    fun getPositions(): List<Position> {//TODO test for ordering
        return positions.toList()
    }

    constructor(behavior: ConstraintBehavior, type: ConstraintType, vararg positions: Position)
            : this(behavior, type, "Constraint with $behavior", *positions)

    init {
        //todo require(positions.isNotEmpty())
    }
}
