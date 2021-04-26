/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable

/**
 * A two dimensional cartesian Coordinate.
 * Implemented as Flyweight (not quite, but Position.get(x,y) gives memoized objects)
 *
 * @property x the x coordinate
 * @property y the y coordinate
 *
 */
class Position(var x: Int, var y: Int) : Xmlable {

    /**
     * Instanziiert ein neues Position-Objekt mit den spezifizierten x- und y-Koordinaten. Ist eine der Koordinaten
     * kleiner als 0, so wird eine IllegalArgumentException geworfen.
     *
     * @throws IllegalArgumentException
     * Wird geworfen, falls eine der Koordinaten kleiner als 0 ist
     */

    /** Identifies, if this Position is a Flyweight and thus may not be changed. */
    private var fixed = true

    /**
     * Tests for equality with other [Position].
     *
     * @return true if obj is of same type and coordinates match
     */
    override fun equals(obj: Any?): Boolean {
        return if (obj == null || obj !is Position) {
            false
        } else {
            x == obj.x
                    && y == obj.y
        }
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
     * returns a distance vector by subtracting the parameter. (both objects remain unchanged)
     * @param p a position to substract from this position
     * @return distance between this and other as position(this-p)
     */
    fun distance(p: Position): Position {
        return Position(x - p.x, y - p.y)
    }

    /**
     * Returns a String Representation of this Position.
     */
    override fun toString(): String {
        return "$x, $y"
    }

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("position")
        representation.addAttribute(XmlAttribute("x", "" + x))
        representation.addAttribute(XmlAttribute("y", "" + y))
        return representation
    }

    fun toXmlTree(name: String?): XmlTree {
        val representation = XmlTree(name)
        representation.addAttribute(XmlAttribute("x", "" + x))
        representation.addAttribute(XmlAttribute("y", "" + y))
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        require(!fixed) { "Tried to manipulate a fixed position" }
        x = xmlTreeRepresentation.getAttributeValue("x").toInt()
        y = xmlTreeRepresentation.getAttributeValue("y").toInt()
    }

    companion object {
        /**
         * Das statische Position-Array
         */
        private var positions: Array<Array<Position>>? = null

        /**
         * Returns a [Position] with the specified coordinates
         *
         * @param x the desired x-coordinate
         * @param y the desired y-coordinate
         * @return Position Object with the coordinates
         * @throws IllegalArgumentException if either coordinate is > 0
         */
        @JvmStatic
		operator fun get(x: Int, y: Int): Position {
            require(x >= 0 && y >= 0) { "a parameter is less than zero" }

            if (positions == null) {
                initializePositions();
            }

            return if (x < 25 && y < 25) {
                positions!![x][y]
            } else {
                val pos = Position(x, y)
                pos.fixed = false
                pos
            }
        }

        /**
         * Initialises the Position Array for efficient Position storage.
         */
        private fun initializePositions() {
            positions = Array(25) { Array(25) { Position(0,0) } }
            for (x in 0..24) {
                for (y in 0..24) {
                    positions!![x][y] = Position(x, y)
                }
            }
        }

        fun fillFromXmlStatic(xmlTreeRepresentation: XmlTree): Position {
            return Companion[
                    xmlTreeRepresentation.getAttributeValue("x").toInt(),
                    xmlTreeRepresentation.getAttributeValue("y").toInt()]
        }
    }
}