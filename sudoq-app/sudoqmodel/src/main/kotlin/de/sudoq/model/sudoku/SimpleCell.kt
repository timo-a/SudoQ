/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

/**
 * A Cell describes an atomic unit in a sudoku board.
 * It holds information about the current value, editability.
 */
class SimpleCell(editable: Boolean, var value: Int,
                 /** A unique number identifying the cell in the scope of the sudoku */
                 val id: Int,
                 /** the number of symbols this cell can take */
                 val numberOfValues: Int) {

    /** The editability of this cell; false for prefilled cell */
    val isEditable: Boolean = editable

    /** The highest value this cell can take */
    private val maxValue: Int = numberOfValues - 1

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return value.toString()
    }

    companion object {
        /** The value representing an empty cell */
        const val EMPTYVAL = -1
    }
}