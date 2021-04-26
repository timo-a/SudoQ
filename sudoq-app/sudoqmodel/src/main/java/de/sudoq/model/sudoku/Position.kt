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
 * Eine Position repräsentiert eine zweidimensionale, kartesische Koordinate.
 * implementiert als Flyweight (not quite, but Position.get(x,y) gives memoized objects)
 */
class Position
/**
 * Instanziiert ein neues Position-Objekt mit den spezifizierten x- und y-Koordinaten. Ist eine der Koordinaten
 * kleiner als 0, so wird eine IllegalArgumentException geworfen.
 *
 * @param x
 * Die x-Koordinate der zu erzeugenden Position
 * @param y
 * Die y-Koordinate der zu erzeugenden Position
 * @throws IllegalArgumentException
 * Wird geworfen, falls eine der Koordinaten kleiner als 0 ist
 */(
        /**
         * Die x-Koordinate der Position
         */
        var x: Int,
        /**
         * Die y-Koordinate der Position
         */
        var y: Int) : Xmlable {
    /* Attributes */
    /**
     * Identifiziert, ob diese Position ein Flyweight ist und somit nicht geändert werden darf.
     */
    private var fixed = true
    /**
     * Gibt die x-Koordinate dieser Position zurück.
     *
     * @return Die x-Koordinate dieser Position
     */
    /**
     * Gibt die y-Koordinate dieser Position zurück.
     *
     * @return Die y-Koordinate dieser Position
     */
    /* Methods */
    /**
     * Vergleicht mit einem anderen Positionobjekt auf Gleichheit bzgl. der Koordinaten
     *
     * @return true wenn obj vom Typ Position und mit diesem Objekt in den Koordinaten übereinstimmt, sonst false
     */
    override fun equals(obj: Any?): Boolean {
        return if (obj == null || obj !is Position) {
            false
        } else {
            val other = obj
            x == other.x && y == other.y
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
     * Gibt eine String-Repräsentation dieser Position zurück.
     */
    override fun toString(): String {
        return x.toString() + ", " + y
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
        private var positions: Array<Array<Position?>>?

        /**
         * Gibt ein Positions-Objekt mit den spezifizierten x- und y-Koordinaten. Ist eine der Koordinaten kleiner als 0, so
         * wird eine IllegalArgumentException geworfen.
         *
         * @param x
         * Die x-Koordinate der zu erzeugenden Position
         * @param y
         * Die y-Koordinate der zu erzeugenden Position
         * @return das neue Position Objekt
         * @throws IllegalArgumentException
         * Wird geworfen, falls eine der Koordinaten kleiner als 0 ist
         */
        operator fun get(x: Int, y: Int): Position? {
            require(!(x < 0 || y < 0)) { "a parameter is less than zero" }
            if (positions == null) {
                initializePositions()
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
         * Initialisiert das Positions-Array für effiziere Positionsverwaltung.
         */
        private fun initializePositions() {
            positions = Array(25) { arrayOfNulls(25) }
            for (x in 0..24) {
                for (y in 0..24) {
                    positions!![x][y] = Position(x, y)
                }
            }
        }

        fun fillFromXmlStatic(xmlTreeRepresentation: XmlTree): Position? {
            return Companion[xmlTreeRepresentation.getAttributeValue("x").toInt(), xmlTreeRepresentation.getAttributeValue("y").toInt()]
        }
    }
    /* Constructors */
}