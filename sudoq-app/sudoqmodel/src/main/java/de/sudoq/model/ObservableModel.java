/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model;

/**
 * Dieses Interface wird von allen Model-Klassen implementiert, die beobachtet
 * werden können. Bei Änderungen benachrichtigen sie alle beobachtenden Objekte.
 * 
 * @param <T>
 *            der Typ des Objekts welches bei Änderungen mitgegeben wird
 */
public interface ObservableModel<T> {
	/* Methods */

	/**
	 * Beim Aufruf dieser Methode werden alle beobachtenden Objekte
	 * benachrichtigt.
	 * 
	 * @param obj
	 *            Das Objekt, das sich geaendert hat.
	 */
    void notifyListeners(T obj);

	/**
	 * Mit dieser Methode kann sich ein Objekt registrieren, um bei allen
	 * zukünfitgen Änderungen benachrichtigt zu werden.
	 * 
	 * @param listener
	 *            Das einzutragenden Objekt
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene Listener null ist
	 */
    void registerListener(ModelChangeListener<T> listener);

	/**
	 * Mit diser Methode kann das gegebene Objekt aus der Beobachter-Liste
	 * entfernt werden. Bei zukünfitgen Änderugen wird das Objekt nicht mehr
	 * benachrichtigt. Ist das Objekt nicht in der Liste passiert nichts.
	 * 
	 * @param listener
	 *            Das zu entfernende Objekt.
	 */
    void removeListener(ModelChangeListener<T> listener);

}
