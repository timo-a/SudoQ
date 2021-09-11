/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import de.sudoq.model.actionTree.ActionTreeElement

/**
 * Das Interface ActionTreeNavListener stellt die Möglichkeit bereit, auf
 * Interaktionen des Benutzers mit dem Aktionsbaum zu reagieren.
 */
interface ActionTreeNavListener {
    /** Methods  */
    /**
     * Wird aufgerufen, wenn der Benutzer über ein Element des Aktionsbaumes
     * fährt. Wird verwendet um das Sudoku im Hintergrund auf den Stand des vom
     * Nutzer gerade angewählten Knotens zu bringen.
     *
     * @param ate
     * ActionTreeElement, welches der Nutzer angewählt hat
     */
    fun onHoverTreeElement(ate: ActionTreeElement)

    /**
     * Wird aufgerufen, wenn der Stand eines ausgewählten ActionTreeElementes
     * geladen werden soll.
     *
     * @param ate
     * Wom Benutzer ausgewähltes ActionTreeElement
     */
    fun onLoadState(ate: ActionTreeElement)
}