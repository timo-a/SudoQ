/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.persistence.game

import de.sudoq.model.actionTree.Action
import de.sudoq.persistence.XmlAttribute
import de.sudoq.persistence.XmlTree
import java.util.*

/**
 * This class represents a node in the action tree.
 *
 * @property id an id that uniquely identifies this node.
 * @property action the action held by this node.
 * @property parent the parent node
 *
 */
class ActionTreeElementBE(val id: Int, val action: Action, private val parentId: Int?,
                          /** Indicates whether this node is marked TODO what does it mean to be marked? */
                          private var isMarked: Boolean = false,
                          /** Indicates whether this move is known to have been a mistake.
                            * @return true if this action has demonstrably led to a wrong state. false if unknown
                            */
                          private var isMistake: Boolean = false,
    /**
     * Indicates whether this action (and all parents up to root?) have been correct.
     * I think intermediate actions can be incorrect...
     * @return true if this action directly leads to a correct state. false if unknown
     */
                          private var isCorrect: Boolean = false
) {


    /**
     * @return an [XmlTree] representing this objects
     */
    fun toXml(): XmlTree? {
        if (action.cellId <= 0) //indicates root node
            return null

        val xml = XmlTree("action")
        xml.addAttribute(XmlAttribute(ID, id.toString()))
        xml.addAttribute(XmlAttribute(PARENT, parentId?.toString() ?: ""))
        xml.addAttribute(XmlAttribute(DIFF, action.diff.toString()))
        xml.addAttribute(XmlAttribute(FIELD_ID, action.cellId.toString()))
        xml.addAttribute(XmlAttribute(ACTION_TYPE, action.XML_ATTRIBUTE_NAME))
        xml.addAttribute(XmlAttribute(MARKED, java.lang.Boolean.toString(isMarked)))
        if (isMistake) {
            xml.addAttribute(XmlAttribute(MISTAKE, java.lang.Boolean.toString(true)))
        }
        if (isCorrect) {
            xml.addAttribute(XmlAttribute(CORRECT, java.lang.Boolean.toString(true)))
        }
        return xml
    }

    companion object {
        /**
         * Constant for XmlAttribute
         */
        const val ID = "id"

        /**
         * Constant for XmlAttribute
         */
        const val PARENT = "parent"

        /**
         * Constant for XmlAttribute
         */
        const val DIFF = "value"

        /**
         * Constant for XmlAttribute
         * corresponds to cell_id, left unchanged for backwards compatibility.
         */
        const val FIELD_ID = "field_id"

        /**
         * Constant for XmlAttribute
         */
        const val ACTION_TYPE = "action_type"

        /**
         * Constant for XmlAttribute
         */
        const val MARKED = "marked"

        /**
         * Constant for XmlAttribute
         */
        const val MISTAKE = "mistake"

        /**
         * Constant for XmlAttribute
         */
        const val CORRECT = "correct"
    }

}