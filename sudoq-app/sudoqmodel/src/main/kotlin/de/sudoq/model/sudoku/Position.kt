/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

/**
 * A two dimensional cartesian Coordinate.
 * Implemented as partial Flyweight, common instances are memoized
 *
 * @property x the x coordinate
 * @property y the y coordinate
 *
 */
class Position private constructor(val x: Int, val y: Int) {

    /**
     * Tests for equality with other [Position].
     *
     * @param other the other [Position]
     * @return true if [other] is of same type and coordinates match
     */
    override fun equals(other: Any?): Boolean {
        return other != null //other mustn't be null
                && other is Position //must have be of type
                && x == other.x //and values need to match
                && y == other.y
    }

    /**
     * Generates a unique hashcode for coordinates `< 65519`
     */
    override fun hashCode(): Int {
        val p = 65519
        val q = 65521
        return x * p + y * q
    }

    /**
     * Returns a distance vector by subtracting the parameter -> Manhattan distance
     *
     * @param p a position to subtract from this position
     * @return distance between this and other as position(this-p)
     */
    fun distance(p: Position): Position {//todo overload minus operator
        return Position(x - p.x, y - p.y)
    }

    /**
     * Returns a String representation of this [Position].
     */
    override fun toString(): String = "$x, $y"

    companion object {

        private const val CACHE_SIZE = 25

        /**
         * The Position array memoizes expected (x,y ∈ [0,24]) values
         */
        private val CACHE: Array<Position> = Array(CACHE_SIZE * CACHE_SIZE) { i ->
            Position(x = i % CACHE_SIZE, y = i / CACHE_SIZE)
        }

        /**
         * Returns a [Position] with the specified coordinates
         *
         * @param x the desired x-coordinate
         * @param y the desired y-coordinate
         * @return Position instance with the coordinates
         * @throws IllegalArgumentException if either coordinate is negative
         */
        @JvmStatic//still needed in java tests
        operator fun get(x: Int, y: Int): Position {
            require(x >= 0 && y >= 0) { "a parameter is less than zero" }

            return if (x < 25 && y < 25) {
                CACHE[x + y * CACHE_SIZE]
            } else {
                Position(x, y)
            }
        }
    }
}