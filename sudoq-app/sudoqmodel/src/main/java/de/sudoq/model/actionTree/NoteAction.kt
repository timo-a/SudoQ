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
 * This class represents an action that adds or removes notes from a [cell].
 */
class NoteAction(diff: Int, cell: Cell) : Action(diff, cell) {

    /**
     * {@inheritDoc}
     */
    override fun execute() {
        cell.toggleNote(diff)
    }

    /**
     * {@inheritDoc}
     */
    override fun undo() {
        cell.toggleNote(diff)
    }

    override fun inverse(a: Action): Boolean {
        //ensure type, inherited equals can be reused as NoteActions are self inverse.
        if (a !is NoteAction) return false
        return equals(a)
    }
}