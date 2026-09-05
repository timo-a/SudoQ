/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

/**
 * A map from Positions to generic objects.
 * Since the mapping is defined directly over the x,y coordinates it is more efficient than a HashMap or a TreeMap.
 *
 * @param T arbitrary type on which to map positions
 * @param dimension the bounding box dimensions of these positions to map, components must be at least 1
 * @param values value array of this map
 * @throws IllegalArgumentException if either dimension component is <= 0
 */
class PositionMap<T> private constructor(
    private var dimension: Position,
    private val values: Array<Array<T?>> = Array(dimension.x) { arrayOfNulls<Any>(dimension.y) } as Array<Array<T?>>
) : Cloneable {

    init {
        require(dimension.x >= 1) { "dimension.x must be at least 1, but is ${dimension.x}" }
        require(dimension.y >= 1) { "dimension.y must be at least 1, but is ${dimension.y}" }
    }

    /**
     * Returns the object at the specified position.
     *
     * @param pos [Position] to query object for
     * @return the object at the specified position or null if there is no mapping
     */
    operator fun get(pos: Position): T {
        require(pos.x in 0 until dimension.x) { "x coordinate of pos out of range [0, ${dimension.x-1}]: ${pos.x}" }
        require(pos.y in 0 until dimension.y) { "y coordinate of pos out of range [0, ${dimension.y-1}]: ${pos.y}" }
        return values[pos.x][pos.y]!!
    }

    /**
     * Indicates if there is a value for the specified position.
     *
     * @param pos [Position] to query for
     * @return true iff the PositionMap has a non-null value saved for the parameter [pos]
     */
    operator fun contains(pos: Position): Boolean {
        require(pos.x in 0 until dimension.x) { "x coordinate of pos out of range [0, ${dimension.x-1}]: ${pos.x}" }
        require(pos.y in 0 until dimension.y) { "y coordinate of pos out of range [0, ${dimension.y-1}]: ${pos.y}" }
        return values[pos.x][pos.y] != null
    }

    class Builder<T>(private var dimension: Position) {

        /**
         * The 2D-Array of this PositionMap
         */
        var values: Array<Array<T?>>

        init {
            require(dimension.x >= 1 && dimension.y >= 1) { "Specified dimension or one of its components was null." }
            @Suppress("UNCHECKED_CAST")
            values = Array(dimension.x) { arrayOfNulls<Any>(dimension.y) } as Array<Array<T?>>
        }

        /**
         * Adds the value at the position, an existing mapping will be overwritten
         *
         * @param pos [Position] at which to insert the object
         * @param value the value to insert
         */
        fun put(pos: Position, value: T) {
            require(!(pos.x > dimension.x || pos.y > dimension.y))
            values[pos.x][pos.y] = value
        }

        fun build(): PositionMap<T> {
            return PositionMap(dimension, values)
        }

        /**
         * Creates and populates a PositionMap by applying a mapping function to an iterable of positions.
         *
         * @param positions An iterable of [Position]s to populate the map with.
         * @param mapper A function that takes a [Position] and returns the corresponding value of type T.
         */
        fun from(positions: Iterable<Position>, mapper: (Position) -> T): PositionMap<T> {

            require(positions.count() <= dimension.x * dimension.y)
            //for samurai there are fewer positions
            for (pos in positions) {
                 this.put(pos, mapper(pos))
            }
            return build()
         }
    }
}
