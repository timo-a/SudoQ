/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import java.util.*

/** UniqueConstraintBehavior means no symbol may appear twice within a constraint. */
class UniqueConstraintBehavior : ConstraintBehavior {

    /** list of symbols found in the check method. */
    private var foundNumbers: MutableList<Int> = ArrayList()

    /**
     * Checkcs if the passed Constraint satisfies Unique behaviour, i.e.
     * if no symbol appears twice among the cells in the constraint.
     *
     * @return true, iff constraint satisfies unique behaviour.
     */
    override fun check(constraint: Constraint, sudoku: Sudoku): Boolean {
        var currentValue: Int
        foundNumbers.clear()
        val positions = constraint.getPositions()
        for (pos in positions) {
            currentValue = sudoku.getCell(pos)!!.currentValue
            if (currentValue != -1)
                if (foundNumbers.contains(currentValue))
                    return false
                else
                    foundNumbers.add(currentValue)
        }
        return true
    }

}