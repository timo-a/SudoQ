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
import java.lang.IllegalArgumentException
import java.util.ArrayList
import kotlin.Throws

/**
 * A Constraint comprises [Cell]s (or rather their [Position]s) in a Sudoku, so that they have to
 * satisfy certain requirements in the form of a [ConstraintBehavior].
 * In a standard sudoku for example the rows, columns and blocks are [Constraint] objects.
 *
 * @property behavior Describes the constraint that the cells have to satisfy
 * @param type Type of the Constraint
 */
class Constraint (private var behavior: ConstraintBehavior, type: ConstraintType) : Iterable<Position>, Xmlable {

    /** A List of [Position]s of all [Cells], that together satisfy a constraint */
    private var positions: MutableList<Position> = ArrayList()

    /** Name of the Constraint should start with one of "extra block", "Block", "Column", "Row".
     */
    private var name: String

    /** Type of the Constraint */
    var type: ConstraintType = type
        private set


    init {
        name = "Constraint with $behavior"
    }

    /**
     * Set name as well. TODO is it ever called with name == null?
     *
     * @param name Name of the Constraint should start with one of "extra block", "Block", "Column", "Row".
     */
    constructor(behavior: ConstraintBehavior, type: ConstraintType, name: String?) : this(behavior, type) {
        if(name != null)
            this.name = name
    }

    /**
     * Adds a [Position] to the contraint
     * @param position The [Position] to add to this constraint
     * TODO make those tests subclasses and set method to private
     */
    @Deprecated("""mostly used in tests""")
    fun addPosition(position: Position) {
        if (position !in positions) {
            positions.add(position)
        }
    }

    /**
     * Checks if the Sudoku satisfies this Constraint.
     *
     * @param sudoku The [Sudoku] to check for constraint saturation
     * @return true if sudoku satisfies this constraint,
     * false if it doesn't or even if it doesn't contain all the positions in this constraint
     */
    fun isSaturated(sudoku: Sudoku): Boolean {
        return behavior.check(this, sudoku)
    }

    /**
     * An iterator over the [Position]s in this constraint.
     *
     * @return An iterator over the [Position]s in this constraint.
     */
    override fun iterator(): Iterator<Position> {
        return positions.iterator()
    }

    /**
     * Check if the Position is part of the constraint.
     *
     * @param p the position to check.
     * @return true iff position is part of this constraint.
     */
    fun includes(p: Position): Boolean {
        return positions.contains(p)
    }

    /**
     * Number of [Position]s in this Constraint.
     */
    val size: Int
        get() = positions.size

    /**
     * Returns whether this Constraint has [UniqueConstraintBehavior] i.e. no symbol may appear more than once.
     *
     * @return true, if  this Constraint has [UniqueConstraintBehavior], false otherwise
     */
    fun hasUniqueBehavior(): Boolean {
        return behavior is UniqueConstraintBehavior
    }

    /**
     * A short description of this Constraint.
     *
     * @return A short description of this Constraint.
     */
    override fun toString(): String {
        return name
    }

    /**
     * Returns a list of positions in this constraint
     *
     * @return a list of positions in this constraint
     */
    fun getPositions(): ArrayList<Position> {//TODO change to immutable list
        return positions as ArrayList<Position>
    }

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("constraint")
        representation.addAttribute(XmlAttribute("behavior", behavior.javaClass.toString()))
        representation.addAttribute(XmlAttribute("name", name))
        representation.addAttribute(XmlAttribute("type", "" + type.ordinal))
        for (pos in positions) {
            representation.addChild(pos.toXmlTree())
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        val behavior = xmlTreeRepresentation.getAttributeValue("behavior")!!
        if (behavior.contains("Unique")) {
            this.behavior = UniqueConstraintBehavior()
        } else {
            throw IllegalArgumentException("Undefined constraint behavior")
        }
        name = xmlTreeRepresentation.getAttributeValue("name")!!
        type = ConstraintType.values()[xmlTreeRepresentation.getAttributeValue("type")!!.toInt()]
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "position") {
                addPosition(Position.fillFromXmlStatic(sub))
            }
        }
    }
}