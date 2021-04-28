/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

/**
 * A class that can be (de-)serialised to/from XML.
 */
interface Xmlable {

    /**
     * Creates an XmlTree Objekt, which contains all persist-worthy attributes.
     *
     * @return [XmlTree] representation of the implementing class
     */
    fun toXmlTree(): XmlTree?

    /**
     * Loads data from an xml representation into the implementing class
     *
     * @param xmlTreeRepresentation Representation of the implementing class.
     * @throws IllegalArgumentException if the XML Representation has an unsupported structure.
     */
    @Throws(IllegalArgumentException::class)
    fun fillFromXml(xmlTreeRepresentation: XmlTree)
}