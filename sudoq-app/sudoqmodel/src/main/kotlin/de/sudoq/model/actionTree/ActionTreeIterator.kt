/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree

import java.util.*

/**
 * This class provides an iterator that iterates over all [ActionTreeElement]s in an ActionTree
 * @property actionTree ActionTree over which to iterate
 */
class ActionTreeIterator(private val actionTree: ActionTree) : Iterator<ActionTreeElement> {

    /**
     * pointer
     */
    private var currentElement: ActionTreeElement?

    private val otherPaths: Stack<ActionTreeElement>

    /**
     * {@inheritDoc}
     */
    override fun hasNext(): Boolean {
        return currentElement != null
    }

    /**
     * {@inheritDoc}
     */
    override fun next(): ActionTreeElement {
        if (currentElement == null) {
            throw NoSuchElementException()
        }

        val ret: ActionTreeElement = currentElement as ActionTreeElement
        for (i in currentElement!!.childrenList.indices.reversed()) {
            otherPaths.push(currentElement!!.childrenList[i])
        }
        currentElement = if (!otherPaths.empty()) {
            otherPaths.pop()
        } else {
            null
        }
        return ret
    }

    init {
        currentElement = actionTree.root
        otherPaths = Stack()
    }
}