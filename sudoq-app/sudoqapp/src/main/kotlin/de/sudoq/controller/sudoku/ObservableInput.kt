/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

/**
 * Interface, welches von Klassen implementiert werden kann, die auf Eingaben in
 * einem Sudoku reagieren.
 *
 * @see InputListener
 */
interface ObservableInput {
    /** Methods  */
    /**
     * Beim Aufruf dieser Methode werden alle beobachtenden Objekte
     * benachrichtigt.
     */
    fun notifyListeners()

    /**
     * Mit dieser Methode kann sich ein Objekt registrieren, um bei allen
     * zukünfitgen Änderungen benachrichtigt zu werden.
     *
     * @param listener
     * Das einzutragenden Objekt
     */
    fun registerListener(listener: InputListener)

    /**
     * Mit diser Methode kann das gegebene Objekt aus der Beobachter-Liste
     * entfernt werden. Bei zukünfitgen Änderugen wird das Objekt nicht mehr
     * benachrichtigt. Ist das Objekt nicht in der Liste passiert nichts.
     *
     * @param listener
     * Das zu entfernende Objekt.
     */
    fun removeListener(listener: InputListener)
}