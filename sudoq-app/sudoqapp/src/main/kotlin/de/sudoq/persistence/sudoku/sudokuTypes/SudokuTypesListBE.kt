/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.persistence.sudoku.sudokuTypes

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.persistence.Xmlable
import java.util.*

/**
 * An xmlable ArrayList of [SudokuTypes]
 */
class SudokuTypesListBE : ArrayList<SudokuTypes>, Xmlable {

    constructor(): super()
    constructor(elements: Collection<SudokuTypes>) : super(elements)



    override fun toXmlTree(): XmlTree {
        val representation = XmlTree(ROOT_NAME)
        for (p in this) {
            val index = Integer.toString(representation.numberOfAttributes)
            representation.addAttribute(
                XmlAttribute(
                    TYPE_ID + "_" + index,
                    Integer.toString(p.ordinal)
                )
            )
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        clear()
        for (xa in xmlTreeRepresentation.attributes2) {
            if (xa.name.startsWith(TYPE_ID)) {
                val st = SudokuTypes.values()[xa.value.toInt()]
                add(st) //right order not guaranteed(if s.o. messes with xml)
            }
        }
    }

    companion object {
        const val ROOT_NAME = "SudokuTypesList"
        private const val ELEMENT_NAME = "Type"
        private const val TYPE_ID = "TypeID"

    }

}