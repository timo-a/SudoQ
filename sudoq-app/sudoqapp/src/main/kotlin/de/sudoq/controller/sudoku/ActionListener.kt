/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import de.sudoq.model.sudoku.Cell

/**
 * Dieses Interface ermöglicht es Klassen zu reagieren, sobald Actions geaendert werde sollen.
 */
interface ActionListener {
    /**
     * Wird aufgerufen, falls der Benutzer die ReDo-Funktion ausgewählt hat
     *
     */
    fun onRedo()

    /**
     * Wird aufgerufen, falls der Benutzer die Rückgängig-Funktion ausgewählt
     * hat
     *
     */
    fun onUndo()

    /**
     * Wird aufgerufen, falls der Benutzer einem Feld eine Notiz hinzufügt.
     *
     * @param cell
     * Das Feld, welchem eine Notiz hinzugefügt werden soll
     * @param value
     * Der Wert der hinzugefügten Notiz
     */
    fun onNoteAdd(cell: Cell, value: Int)

    /**
     * Wird aufgerufen, falls der Benutzer eine Notiz entfernt.
     *
     * @param cell
     * Das Feld, dessen Notiz entfernd werden soll
     * @param value
     * Der Wert der Notiz, die zu entfernen ist.
     */
    fun onNoteDelete(cell: Cell, value: Int)

    /**
     * Wird aufgerufen, falls der Benutzer einem Feld einen Lösungswert
     * hinzufügt.
     *
     * @param cell
     * Das Feld, welchem der Lösungswert hinzugefügt werden soll
     * @param value
     * Der Wert der Eingabe
     */
    fun onAddEntry(cell: Cell, value: Int)

    /**
     * Wird aufgerufen, falls der Benutzer einen Eintrag aus einem Feld
     * entfernt.
     *
     * @param cell
     * Das Feld, aus welchem der Eintrag entfernt werden soll
     */
    fun onDeleteEntry(cell: Cell)
}