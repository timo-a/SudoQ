/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.complexity

/**
 * This class represents the requirements a sudoku must meet to qualify as a certain complexity.
 */
class ComplexityConstraint : Cloneable {

    /** The complexity that this constraint pertains to */
    var complexity: Complexity = Complexity.arbitrary //default value without meaning
        private set

    /**
     * The average number of cells that should be pre-filled for a sudoku of this complexity
     */
    var averageCells = 0
        private set

    /**
     * The minimum complexity score needed for the complexity.
     * (the complexity score is the sum of the scores of the solution techniques that the solver needs to solve a sudoku)
     */
    var minComplexityIdentifier = 0
        private set

    /**
     * The maximum complexity score needed for the complexity.
     * (the complexity score is the sum of the scores of the solution techniques that the solver needs to solve a sudoku)
     */
    var maxComplexityIdentifier = 0
        private set

    /**
     * The number of solution techniques that he solution algorithm is allowed to use
     */
    var numberOfAllowedHelpers = 0
        private set

    constructor()  //this is needed for fillFromXML todo refactor fillFromXML into a static method that returns an object

    /**
     * Creates a new ComplexityConstraint.
     *
     * @param complexity The complexity that this constraint pertains to
     * @param averageFields The average number of cells that should be pre-filled for a sudoku of this complexity
     * @param minComplexityIdentifier The minimum complexity score needed for the complexity
     * @param maxComplexityIdentifier The maximum complexity score needed for the complexity
     * @param numberOfAllowedHelpers The number of solution techniques that he solution algorithm is allowed to use
     * @throws IllegalArgumentException if numbers are zero and lower or if min > max
     *
     */
    constructor(
        complexity: Complexity, averageFields: Int, minComplexityIdentifier: Int,
        maxComplexityIdentifier: Int, numberOfAllowedHelpers: Int
    ) {
        require(minComplexityIdentifier > 0) { "minComplexityIdentifier < 0: $minComplexityIdentifier" }
        require(averageFields > 0) { "averageFields < 0: $averageFields" }
        require(numberOfAllowedHelpers > 0) { "minComplexityIdentifier < 0: $numberOfAllowedHelpers" }
        require(minComplexityIdentifier <= maxComplexityIdentifier) {
            ("minComplexityIdentifier > maxComplexityIdentifier: $minComplexityIdentifier > $maxComplexityIdentifier")
        }
        this.complexity = complexity
        averageCells = averageFields
        this.minComplexityIdentifier = minComplexityIdentifier
        this.maxComplexityIdentifier = maxComplexityIdentifier
        this.numberOfAllowedHelpers = numberOfAllowedHelpers
    }

    public override fun clone(): Any {
        return super.clone()
    }
}