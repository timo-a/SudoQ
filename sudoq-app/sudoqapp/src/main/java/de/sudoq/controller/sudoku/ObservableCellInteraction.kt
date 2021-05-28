/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

/**
 * Interface, welches von Klassen implementiert werden kann, die auf denen
 * Feld-Interaktionen ausgeführt werden. einem Sudoku ausführen.
 *
 * @see CellInteractionListener
 */
interface ObservableCellInteraction {
    /**
     * Registriert einen Listener. Ist dieser null, so wird nichts getan.
     *
     * @param listener
     * Der Listener der hinzugefügt werden soll.
     */
    fun registerListener(listener: CellInteractionListener?)

    /**
     * Entfernt einen Listener. Ist dieser nicht registriert, so wird nichts
     * getan.
     *
     * @param listener
     * Der Listener der entfernt werden soll.
     */
    fun removeListener(listener: CellInteractionListener?)
}