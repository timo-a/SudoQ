/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.complexity

/**
 * This class represents the requirements a sudoku must meet to qualify as a certain complexity.
 *
 * @param complexity The complexity that this constraint pertains to
 * @param averageFields The average number of cells that should be pre-filled for a sudoku of this complexity
 * @param minComplexityIdentifier The minimum complexity score needed for the complexity (the
 * complexity score is the sum of the scores of the solution techniques that the solver needs to
 * solve a sudoku)
 *
 * @param maxComplexityIdentifier The maximum complexity score needed for the complexity
 * @param numberOfAllowedHelpers The number of solution techniques that he solution algorithm is allowed to use
 * @throws IllegalArgumentException if numbers are zero and lower or if min > max
 *
 */
class ComplexityConstraint(
    val complexity: Complexity,
    val averageCells: Int,
    val minComplexityIdentifier: Int,
    val maxComplexityIdentifier: Int,
    val numberOfAllowedHelpers: Int
) : Cloneable {

    init {
        require(minComplexityIdentifier > 0) { "minComplexityIdentifier < 0: $minComplexityIdentifier" }
        require(averageCells > 0) { "averageCells < 0: $averageCells" }
        require(numberOfAllowedHelpers > 0) { "minComplexityIdentifier < 0: $numberOfAllowedHelpers" }
        require(minComplexityIdentifier <= maxComplexityIdentifier) {
            ("minComplexityIdentifier > maxComplexityIdentifier: $minComplexityIdentifier > $maxComplexityIdentifier")
        }
    }

    public override fun clone(): Any {
        return super.clone()
    }
}