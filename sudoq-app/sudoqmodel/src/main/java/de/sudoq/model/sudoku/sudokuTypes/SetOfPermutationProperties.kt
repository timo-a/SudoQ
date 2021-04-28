/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*

/**
 * An xmlable ArrayList of PermutationProperties
 */
class SetOfPermutationProperties : ArrayList<PermutationProperties>(), Xmlable {

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree(SET_OF_PERMUTATION_PROPERTIES)
        for (p in this) {
            val xt = XmlTree(PERMUTATION_PROPERTY)
            xt.addAttribute(XmlAttribute(TAG_PROPERTY_NR, "" + p.ordinal))
            representation.addChild(xt)
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        for (sub in xmlTreeRepresentation) {
            if (sub.name == PERMUTATION_PROPERTY) {
                add(PermutationProperties.values()[sub.getAttributeValue(TAG_PROPERTY_NR)!!.toInt()])
            }
        }
    }

    companion object {
        const val SET_OF_PERMUTATION_PROPERTIES = "SetOfPermutationProperties"
        private const val PERMUTATION_PROPERTY = "PermutationProperty"
        private const val TAG_PROPERTY_NR = "permutationNr"
    }
}