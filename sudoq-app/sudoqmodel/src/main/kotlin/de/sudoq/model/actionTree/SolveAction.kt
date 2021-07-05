/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree

import de.sudoq.model.sudoku.Cell

/**
 * This class represents an action that adds or removes entries from a [cell].
 */
class SolveAction(diff: Int, cell: Cell) : Action(diff, cell) {

    init {
        XML_ATTRIBUTE_NAME = "SolveAction"
    }

    /**
     * {@inheritDoc}
     */
    override fun execute() {
        cell.currentValue += diff //todo execute is not idempotent... maybe pass target val instead of diff and save last value on execute?
    }

    /**
     * {@inheritDoc}
     */
    override fun undo() {
        cell.currentValue -= diff
    }

    override fun inverse(a: Action): Boolean {
        if (a !is SolveAction) return false
        return cell == a.cell
                && diff + a.diff == 0
    }

    /** in case we want to move an action up one level (field stays the same) we need to add the diffs
     * TODO refactor this, break with diffs entirely maybe?
     * TODO purpose unclear
     * @param action another action
     * @return a new SolveAction that sums up both actions diffs
     */
    fun add(action: SolveAction): SolveAction {
        return SolveAction(diff + action.diff, cell)
    }
}