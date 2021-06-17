/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.ActionTree
import de.sudoq.model.actionTree.ActionTree.Companion.findPath
import de.sudoq.model.actionTree.ActionTreeElement
import de.sudoq.model.actionTree.SolveAction
import java.util.*

/**
 * Diese Klasse verwaltet den Zustand eines Spiels durch einen ActionTree und stellt Funktionalität für die Verwaltung
 * des Zustandes zur Verfügung.
 */
class GameStateHandler : ObservableModelImpl<ActionTreeElement>() {
    /**
     * The data structure that stores all actions in order
     */
    val actionTree: ActionTree = ActionTree()

    /**
     * The node of the [ActionTree] that represents the current state.
     */
    var currentState: ActionTreeElement? //TODO can be made nonnullable?
        private set

    /**
     * In case of undo this stack saves the way back forward for a redo
     */
    private val undoStack: Stack<ActionTreeElement> = Stack()

    /**
     * A semaphore? to prevent concurrent changes by listeners during a change
     */
    private var locked: Boolean

    /**
     * Adds an [Action] to the [ActionTree] and executes it
     *
     * @param action The [Action] to add and execute
     */
    fun addAndExecute(action: Action) {
        // if another change is in progress dont execute!
        //TODO this looks wrong we're not waiting we're just skipping
        if (!locked) {
            locked = true
            addStrategic(action)
            notifyListeners(currentState!!)
            locked = false
        }
    }

    //private
    private fun addStrategic(action: Action) {
        when {
            isActionRedundant(currentState, action) -> {
                currentState = findExistingChildren(currentState, action)[0]
                action.execute()
            }
            isActionAStepBack(currentState, action) -> {
                currentState = currentState!!.parent
                action.execute()
            }
            isSolveOnSameCell(action) -> {
                val intended = action as SolveAction
                val above = currentState!!.action as SolveAction
                val liftedAction = intended.add(above)
                currentState!!.undo()
                currentState = currentState!!.parent
                addStrategic(liftedAction)
            }
            else -> {
                currentState = actionTree.add(action, currentState!!)
                currentState!!.execute()
            }
        }
    }

    /* check if action already in Tree,
	      i.e. we went back in actionTree but are doing same steps again */
    private fun isActionRedundant(mountingElement: ActionTreeElement?, action: Action): Boolean {
        return findExistingChildren(mountingElement, action).isNotEmpty()
    }

    private fun findExistingChildren(
        mountingElement: ActionTreeElement?,
        action: Action
    ): List<ActionTreeElement> {
        val l: MutableList<ActionTreeElement> = Stack()
        if (mountingElement != null) {
            for (ateI in mountingElement.childrenList) if (ateI.actionEquals(action)) l.add(ateI)
        }
        return l
    }

    private fun isActionAStepBack(mountingElement: ActionTreeElement?, action: Action): Boolean {
        return mountingElement!!.action.inverse(action)
    }

    private fun isSolveOnSameCell(action: Action): Boolean {
        return currentState !== actionTree.root
                && bothSolveActions(currentState, action)
                && isActionOnSameCell(currentState, action)
    }

    private fun bothSolveActions(mountingElement: ActionTreeElement?, action: Action): Boolean {
        val actionAbove = mountingElement!!.action
        return action is SolveAction
                && actionAbove is SolveAction
    }

    private fun isActionOnSameCell(mountingElement: ActionTreeElement?, action: Action): Boolean {
        val sameCell = mountingElement!!.action.cell == action.cell
        return action is SolveAction && sameCell
    }

    /**
     * Executes all necessary [Action]s to bring the Sudoku back to the passed state.
     *
     * @param target The ActionTreeElement in which state the [Sudoku] is to be converted
     */
    fun goToState(target: ActionTreeElement) {
        locked = true
        var onlyUndo = true
        val listWay = findPath(currentState!!, target)!!
        val way = listWay.toTypedArray()

        for (i in 1 until way.size) {
            if (way[i - 1].parent === way[i]) { //are we going backwards?
                way[i - 1].undo()
                if (way[i].isSplitUp()) {
                    undoStack.push(way[i - 1])
                }
            } else {
                onlyUndo = false
                if (i - 2 >= 0 && way[i - 2].parent !== way[i - 1]) {
                    way[i - 1].execute()
                }
                if (way[i] === target) {
                    target.execute()
                }
            }
        }
        if (!onlyUndo) {
            undoStack.clear()
        }
        currentState = target
        notifyListeners(currentState!!)
        locked = false
    }

    /**
     * Gibt zurück, ob die letzte Aktion rückgängig gemacht werden kann
     *
     * @return true, falls die letzte Aktion rückgängig gemacht werden kann, false falls es keine Aktion gibt, die
     * rückgängig gemacht werden kann
     */
    fun canUndo(): Boolean {
        return currentState!!.parent != null
    }

    /**
     * Undoes the last [Action]. Goes one step back in the version history.
     */
    fun undo() {
        locked = true
        if (currentState!!.parent != null) {
            val oldElement = currentState
            currentState = currentState!!.undo()
            if (currentState!!.isSplitUp()) {
                undoStack.push(oldElement)
            }
            notifyListeners(currentState!!)
        }
        locked = false
    }

    /**
     * Checks if You can go a step forward in the action history.
     *
     * @return true, if a redo is possible, false otherwise
     */
    fun canRedo(): Boolean {
        //if there are several child nodes then undo stack cannot be empty
        val a = currentState!!.isSplitUp() && undoStack.isNotEmpty()
        //if there are less than 2 child nodes, there has to be at least one
        val b = !currentState!!.isSplitUp() && currentState!!.hasChildren()
        return a || b
    }

    /**
     * Goes one step forward in the action history.
     * If the last step was an undo that undo is reversed.
     */
    fun redo() {
        locked = true
        if (currentState!!.isSplitUp()) {
            if (!undoStack.empty()) {
                currentState = undoStack.pop()
                currentState!!.execute()
                notifyListeners(currentState!!)
            }
        } else {
            if (currentState!!.hasChildren()) {
                //if there is a child node, go there, execute
                currentState = currentState!!.iterator().next()
                currentState!!.execute()
                notifyListeners(currentState!!)
            }
        }
        locked = false
    }

    /**
     * Marks the current state to better find it later
     */
    fun markCurrentState() {
        currentState!!.mark()
    }

    /**
     * Checks if the passed [ActionTreeElement] is marked.
     *
     * @param ate the [ActionTreeElement] to check
     * @return true if it is marked, false otherwise
     */
    fun isMarked(ate: ActionTreeElement?): Boolean {
        return ate != null && ate.isMarked //TODO make non nullable
    }

    init {
        currentState = actionTree.root
        locked = false
    }
}