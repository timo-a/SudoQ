/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

/* Never used, just here to show what's possible.
  Sudokus don't use this! or do they? not sure anymore...
  I say they wouln't be neccessary if the fully solved part was moved to uniquebehavior
  */

/** SumConstraintBehavior means the sum of the values in the cells must have a certain value
 * @property fixed sum the cells must have
 * */
class SumConstraintBehavior(private val sum: Int) : ConstraintBehavior {

    /**
     * Checkcs if the passed Constraint satisfies Unique behaviour, i.e.
     * if all values sum up to a given total.
     *
     * @return true, iff constraint satisfies sum behaviour.
     */
    override fun check(constraint: Constraint, sudoku: Sudoku): Boolean {
        var fieldSum = 0
        var fullySolved = true
        for (pos in constraint) {
            fieldSum += sudoku.getCell(pos)!!.currentValue
            if (sudoku.getCell(pos)!!.isNotSolved) fullySolved = false
        }
        return fieldSum == sum || !fullySolved && fieldSum <= sum
    }

    init {
        if (sum < 0) {
            throw IllegalArgumentException()
        }
    }
}