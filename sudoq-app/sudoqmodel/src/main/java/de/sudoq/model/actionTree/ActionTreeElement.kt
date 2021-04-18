/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import java.util.*

/**
 * This class represents a node in the action tree.
 *
 * @property id an id that uniquely identifies this node.
 * @property action the action held by this node.
 * @property parent the parent node
 *
 */
class ActionTreeElement(val id: Int, val action: Action, val parent: ActionTreeElement?) : ObservableModelImpl<ActionTreeElement>(), Comparable<ActionTreeElement>, Iterable<ActionTreeElement> {

    /**
     * The list of all child elements.
     *
     * @return a list of all child nodes.
     */
    var childrenList: ArrayList<ActionTreeElement> = ArrayList()
        private set

    /**
     * Indicates whether this node is marked //TODO what does it mean to be marked?
     *
     * @return true iff this node is marked otherwise false
     */
    var isMarked = false
        private set

    /**
     * Indicates whether this move is known to have been a mistake.
     *
     * @return true if this action has demonstrably led to a wrong state. false if unknown
     */
    var isMistake = false
        private set

    /**
     * Indicates whether this action (and all parents up to root?) have been correct.
     * I think intermediate actions can be incorrect...
     * @return true if this action directly leads to a correct state. false if unknown
     */
    var isCorrect = false
        private set

    /**
     * Executes the [Action] of this node.
     */
    fun execute() {
        action.execute()
    }

    /**
     * Reverses the [Action] of this node.
     *
     * @return the parent [ActionTreeElement]
     */
    fun undo(): ActionTreeElement? {
        action.undo()
        return parent //TODO refactor: don't return parent make, let caller call parent extra
    }

    /**
     * Checks if this node has any child nodes.
     *
     * @return true if node has child nodes, false otherwise
     */
    public fun hasChildren(): Boolean {
        return childrenList.isNotEmpty()
    }

    /**
     * Checks if this node has several child nodes.
     *
     * @return true if node has several child nodes, false otherwise
     */
    fun isSplitUp(): Boolean {
        return childrenList.size > 1
    }

    /**
     * Adds child node.
     *
     * @param child a child node to add
     */
    fun addChild(child: ActionTreeElement) {
        childrenList.add(child)
    }

    /**
     * marks this node. Notifies listeners.
     */
    fun mark() {
        isMarked = true
        notifyListeners(this)
    }

    /**
     * Marks this action as a mistake
     */
    fun markWrong() {
        isMistake = true
    }

    /**
     * Marks this action as correct
     */
    fun markCorrect() {
        isCorrect = true
    }

    /**
     * @return an [XmlTree] representing this objects
     */
    fun toXml(): XmlTree {
        val xml = XmlTree("action")
        xml.addAttribute(XmlAttribute(ID, id.toString()))
        xml.addAttribute(XmlAttribute(PARENT, parent?.id?.toString() ?: ""))
        xml.addAttribute(XmlAttribute(DIFF, action.diff.toString()))
        xml.addAttribute(XmlAttribute(FIELD_ID, action.cellId.toString()))
        xml.addAttribute(XmlAttribute(ACTION_TYPE, action::class.simpleName))
        xml.addAttribute(XmlAttribute(MARKED, java.lang.Boolean.toString(isMarked)))
        if (isMistake) {
            xml.addAttribute(XmlAttribute(MISTAKE, java.lang.Boolean.toString(true)))
        }
        if (isCorrect) {
            xml.addAttribute(XmlAttribute(CORRECT, java.lang.Boolean.toString(true)))
        }
        return xml
    }

    /**
     * {@inheritDoc}
     */
    override fun compareTo(other: ActionTreeElement): Int {
        return id - other.id
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (other !is ActionTreeElement) return false;

        return id == other.id
                && isMarked == other.isMarked
                && action == other.action
    }

    /**
     * Returns whether the action held by this ActionTreeElement is equal to the action passed as parameter
     * @param o external action to compare
     * @return true if this.action == o, else false
     */
    fun actionEquals(o: Any?): Boolean {
        return action == o
    }

    /**
     * {@inheritDoc}
     */
    override fun iterator(): MutableIterator<ActionTreeElement> {
        return childrenList.iterator();
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

    init {
        isMarked = false
        isMistake = false
        isCorrect = false
        parent?.addChild(this)
    }
}