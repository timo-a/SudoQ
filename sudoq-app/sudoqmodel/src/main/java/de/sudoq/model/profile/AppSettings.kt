/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable

/**
 * This class represents general settings:
 * - whether debug mode is on
 * - the language
 */
class AppSettings : Xmlable {

    var isDebugSet = false
        private set

    /* language */
    var language: String = "system"

    /* additional settings */
    fun setDebug(value: Boolean) {
        isDebugSet = value
    }

    /* to and from string */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("appSettings")
        representation.addAttribute(XmlAttribute("debug", isDebugSet))
        representation.addAttribute(XmlAttribute("language", language))
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        isDebugSet =
            java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("debug"))
        language = xmlTreeRepresentation.getAttributeValue("language") ?: "system"
        //if language hasn't been used before it will be null -> assume system
    }
}