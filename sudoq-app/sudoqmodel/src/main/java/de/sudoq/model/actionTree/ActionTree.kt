/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.sudoku.Cell
import java.util.*

/**
 * Diese Klasse repräsentiert die Menge aller Züge auf einem Sudoku. Sie erlaubt
 * von einem Zustand aus verschiedene Wege aus weiterzuverfolgen. Folglich
 * ergeben die Züge einen Baum.
 */
class ActionTree : ObservableModelImpl<ActionTreeElement>(), Iterable<ActionTreeElement> {

    /**
     * The root node of the tree.
     */
    var root: ActionTreeElement

    /**
     * counter to assign each node a unique id.
     */
    private var idCounter = 1

    enum class InsertStrategy {
        redundant, undo, upwards, regular, none
    }

    val lastInsertStrategy = InsertStrategy.none
    private val actionSequence: List<Action> = ArrayList()

    /**
     * add an action to the tree.
     *
     * @param action the [Action] to add
     * @param mountingElement the node under which te action should be added.
     * There is no check whether mountingElement is part of the tree.
     * @return the new node.
     */
    fun add(action: Action, mountingElement: ActionTreeElement): ActionTreeElement {

        //mount new tree node on specified parent node
        val ate = ActionTreeElement(idCounter++, action, mountingElement)

        notifyListeners(ate)
        //actionSequence.clear();
        return ate
    }

    /**
     * searches the tree for a node with the given id.
     *
     * @param id of the desired node
     * @return the node if found else null
     */
    fun getElement(id: Int): ActionTreeElement? {

        if (1 <= id && id < idCounter) { //TODO is range check necessary?
            for (ate in this)
                if (ate.id == id)
                    return ate
        }
        return null
    }

    /**
     * @return the number of nodes in the tree
     */
    val size: Int
        get() = idCounter - 1

    /**
     * @return an iterator over action tree nodes
     */
    override fun iterator(): MutableIterator<ActionTreeElement> {//TODO should probably not be mutable
        return ActionTreeIterator(this)
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(obj: Any?): Boolean {
        if (obj !is ActionTree) return false

        if (size != obj.size) return false

        val at1: Iterator<ActionTreeElement> = iterator()
        val at2: Iterator<ActionTreeElement> = obj.iterator()
        while (at1.hasNext()) {
            // since the sites are equals at2.hasNext() is true
            if (at1.next() != at2.next()) return false
        }
        return true
    }

    companion object {
        /**
         * Returns the shortest path in the tree from start to end.
         * start and end are included in the path, unless they are identical, then an empty list is returned.
         *
         * @param start start node
         * @param end end note
         * @return the path
         * @throws NullPointerException
         * falls start oder end null sind
         */
		 fun findPath(start: ActionTreeElement, end: ActionTreeElement): List<ActionTreeElement>? {
            //Assumptions:
            //    every tree has a root with id == 0, so there is a common ancestor node by definition
            if (start.id == end.id) {
                return Collections.emptyList()
            }

            // Ways from Start or End Element to the tree root

            val startToRoot = LinkedList(Collections.singleton(start))
            val endToRoot = LinkedList(Collections.singleton(end))

            /* back track until parents with same ids are found */
            var current = startToRoot
            var other = endToRoot
            while (noCommonAncestorFoundMoreToGo(startToRoot, endToRoot)) {
                catchUp(current, other) //add to current until current.last.id <= other.last.id
                val tmp = current //swap
                current = other
                other = tmp
            }

            // both last elements now have the same id (worst case it is 1 i.e. root)
            // if the last elements are not also identical, they must be from different trees
            if (startToRoot.last !== endToRoot.last) {
                return null //todo return [] instead?
            }

            // remove elements which are in both paths
            var commonAncestor: ActionTreeElement?
            do {
                commonAncestor = startToRoot.removeLast()
                endToRoot.removeLast()
            } while (!startToRoot.isEmpty() && !endToRoot.isEmpty()
                    && startToRoot.last === endToRoot.last)

            // add the end-root way backwards
            startToRoot.addLast(commonAncestor)
            val it = endToRoot.descendingIterator()
            while (it.hasNext()) {
                startToRoot.addLast(it.next())
            }
            return startToRoot
        }

        private fun noCommonAncestorFoundMoreToGo(startToRoot: LinkedList<ActionTreeElement>, endToRoot: LinkedList<ActionTreeElement>): Boolean {
            val lastId1 = startToRoot.last.id
            val lastId2 = endToRoot.last.id
            val lastElementsDiffer = lastId1 != lastId2
            val notBothRoot = lastId1 > 1 || lastId2 > 1 //not necessary when we are absolutely sure to end up at the same root node
            //maybe compare ids in last elements differ
            return lastElementsDiffer && notBothRoot
        }

        /**
         * adds parents to current until current's last element has an id lesser or equal other's
         */
        private fun catchUp(current: LinkedList<ActionTreeElement>, other: LinkedList<ActionTreeElement>) {
            while (current.last.id > other.last.id) {
                val parent = current.last.parent
                current.addLast(parent)
            }
        }
    }

    /**
     * Erzeugt und instanziiert einen neuen ActionTree
     */
    init {
        val mockAction: Action = object : Action(0, Cell(-1, 1)) {
            override fun undo() {}
            override fun execute() {}
            override fun inverse(a: Action): Boolean {
                return false
            }
        }
        root = ActionTreeElement(idCounter++, mockAction, null)
    }
}