/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree

import de.sudoq.model.sudoku.Cell

/**
 *  This class represents an action that can be applied and reversed on a [Cell].
 *  @constructor protected to prevent instantiation outside this package.
 *  @property diff the difference between old and new value. If a cells value is changed from 4 to 6
 *            then the diff is 2.
 *  @property cell the [Cell] that this action is associated  with
 */
abstract class Action internal constructor(var diff: Int, val cell: Cell) {
//constuctor was originally package wide through protected
//but there is no kotlin equivalent so it is `internal` now

    /**
     * Executes the action.
     */
    abstract fun execute()

    /**
     * Reverses the action.
     */
    abstract fun undo()

    /**
     * Returns the id of the cell.
     *
     * @return the [Cell.id] of the cell associated with this class.
     */
    val cellId: Int
        get() = cell.id

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass)
            return false

        other as Action //TODO make explicit equals for all subclasses instead of comparing javaClass

        return diff == other.diff
                && cell == other.cell
    }

    /**
     * Determines whether two actions are inverse to each other.
     * @param a another action
     * @return whether the passed action is inverse to this one.
     */
    abstract fun inverse(a: Action): Boolean

}