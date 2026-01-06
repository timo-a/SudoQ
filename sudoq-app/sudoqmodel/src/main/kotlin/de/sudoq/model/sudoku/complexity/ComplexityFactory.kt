/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.complexity

/**
 * This interface specifies methods to create ComplexityConstraints subject to a complexity.
 * todo seems redundant, try to remove
 */
interface ComplexityFactory {

    /**
     * This method creates a complexity constraint that specifies the requirements of a sudoku of the given complexity.
     *
     * @param complexity The [Complexity for which to generate a [ComplexityConstraint]
     * @return A ComplexityConstraint for the given complexity of null, if complexity constraint is not valid
     * Todo throw illegalArgException instead
     */
    fun buildComplexityConstraint(complexity: Complexity): ComplexityConstraint? //todo review if this can be nonnullable. maybe 4x4 does not have infernal? should arbitrary be accepted?

    /**
     * Returns the factor that indicates the number of [Cell]s, that are randomly prefilled and
     * allow the generation algorithm to validate.
     *
     * @return the standard factor for preallocated Cells
     */
    fun getStandardAllocationFactor(): Float
}