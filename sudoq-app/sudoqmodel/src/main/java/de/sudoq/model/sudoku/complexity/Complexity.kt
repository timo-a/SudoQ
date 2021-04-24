/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.complexity

import java.util.*

/**
 * The complexity of a sudoku.
 */
enum class Complexity {

    easy,

    medium,

    difficult,

    /** highest complexity guessing(-> backtracking) is required*/
    infernal,

    /** Dummy value for arbitrary complexity. There are no requirements. */
    arbitrary;

    companion object {
        @JvmStatic
		fun playableValues(): Iterable<Complexity> {
            val l: MutableList<Complexity> = ArrayList(listOf(*values()))
            l.remove(arbitrary)//skip arbitrary as it is not playable
            return l
        }
    }
}