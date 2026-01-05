/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

/**
 * This Interface defines a check function, that checks if a constraint has a certain behaviour.
 */
interface ConstraintBehavior {//todo refactor. make abstract class instead?

    /**
     * This function checks of a certain constraint behaviour is fulfilled by the Sudoku
     *
     * @param constraint The Constraint which to check for
     * @param sudoku The Sudoku on which to check
     * @return true, iff sudoku satisfies the constraint
     */
    fun check(constraint: Constraint, sudoku: Sudoku): Boolean
}