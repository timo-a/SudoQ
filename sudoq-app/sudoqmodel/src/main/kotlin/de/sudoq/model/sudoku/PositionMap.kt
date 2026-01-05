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
 * @property dimension the bounding box dimensions of this positions to map
 */
class PositionMap<T>(private var dimension: Position) : Cloneable {

    /**
     * Das Werte-Array dieser PositionMap
     */
    var values: Array<Array<T?>>

    /**
     * Adds the value at the position, an existing mapping will be overwritten
     *
     * @param pos [Position] at which to insert the object
     * @param value the value to insert
     * @return the previous value at that position or null if there was none
     */
    fun put(pos: Position, value: T): T? {
        require(!(pos.x > dimension.x || pos.y > dimension.y))
        val ret = values[pos.x][pos.y]
        values[pos.x][pos.y] = value
        return ret
    }

    /**
     * Returns the object at the specified position.
     *
     * @param pos [Position] to query object for
     * @return the object at the specified position or null if there is no mapping
     */
    operator fun get(pos: Position): T? {//todo can this be made nonnullable?
        require(pos.x <= dimension.x) { "x coordinate of pos was > " + dimension.x + ": " + pos.x }
        require(pos.y <= dimension.y) { "y coordinate of pos was > " + dimension.y + ": " + pos.y }
        assert(pos.x in 0 until dimension.x)
        assert(pos.y in 0 until dimension.y)
        return values[pos.x][pos.y]
    }

    /**
     * Initialises a new PositionMap for as many Entries as dimension defines.
     * Size must be at least 1 on both components.
     *
     * @param dimension size of the domain
     * @throws IllegalArgumentException if either dimension component is <= 0
     */
    init {
        require(!(dimension.x < 1 || dimension.y < 1)) { "Specified dimension or one of its components was null." }
        values = Array(dimension.x) { arrayOfNulls<Any>(dimension.y) } as Array<Array<T?>>
    }

    /**
     * Creates and populates a PositionMap by applying a mapping function to an iterable of positions.
     *
     * @param dimension The dimensions of the map.
     * @param positions An iterable of [Position]s to populate the map with.
     * @param mapper A function that takes a [Position] and returns the corresponding value of type T.
     */
    constructor(dimension: Position, positions: Iterable<Position>, mapper: (Position) -> T) : this(dimension) {
        require(positions.count() <= dimension.x * dimension.y) //for samurai there are less positions
        for (pos in positions) {
            this.put(pos, mapper(pos))
        }
    }

}
