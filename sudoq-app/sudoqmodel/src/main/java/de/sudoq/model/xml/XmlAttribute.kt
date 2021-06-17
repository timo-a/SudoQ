/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

/**
 * An Attribute in an [XmlTree]
 *
 * @property value Value of the Attribute
 *
 */
class XmlAttribute(name: String, var value: String) {

    /** Name of the Attribute */
    val name: String

    init {
        require(name != "")
        this.name = name
    }

    constructor(name: String, value: Boolean) : this(name, "" + value)


    override fun toString(): String {
        return "$name: $value"
    }

    /**
     * Checks if another attribute has the same name
     *
     * @param attribute attribute to compare
     * @return True, iff both have the same name
     */
    fun isSameAttribute(attribute: XmlAttribute): Boolean {
        return attribute.name == name
    }
}