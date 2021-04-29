/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

import java.util.*

/** Do never ever try to do this generic. NEVER
 * An xmlable ArrayList of PermutationProperties
 */
abstract class XmlableEnumList     //TODO replace setofpermutationproperties by this
(val rootName: String, var enumType: String) : ArrayList<Int?>(), Xmlable {
    fun add(h: Enum<*>): Boolean {
        return this.add(h.ordinal)
    }

    abstract val list: ArrayList<*>?
    override fun toXmlTree(): XmlTree? {
        val representation = XmlTree(rootName)
        for (i in this) {
            val index = "" + representation.numberOfChildren
            val value = "" + this[i!!]
            val xa = XmlAttribute(index, value)
            representation.addAttribute(xa)
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        this.clear()
        ensureCapacity(xmlTreeRepresentation.numberOfAttributes)
        for (xa in xmlTreeRepresentation.attributes2) {
            val index = xa.name.toInt()
            val value = xa.value.toInt() //TODO wont work wg. type erasure?
            this[index] = value
        }
    }
}