/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import de.sudoq.model.sudoku.Field;

/**
 * Dieses Interface ermöglicht es Klassen zu reagieren, sobald Actions geaendert werde sollen.
 */
public interface ActionListener {
	/**
	 * Wird aufgerufen, falls der Benutzer die ReDo-Funktion ausgewählt hat
	 * 
	 */
	public void onRedo();
	
	/**
	 * Wird aufgerufen, falls der Benutzer die Rückgängig-Funktion ausgewählt
	 * hat
	 * 
	 */
	public void onUndo();

	/**
	 * Wird aufgerufen, falls der Benutzer einem Feld eine Notiz hinzufügt.
	 * 
	 * @param field
	 *            Das Feld, welchem eine Notiz hinzugefügt werden soll
	 * @param value
	 *            Der Wert der hinzugefügten Notiz
	 * @throw IllegalArgumentException Wird geworfen, falls das übergebene Field
	 *        null ist
	 */
	public void onNoteAdd(Field field, int value);
	
	/**
	 * Wird aufgerufen, falls der Benutzer eine Notiz entfernt.
	 * 
	 * @param field
	 *            Das Feld, dessen Notiz entfernd werden soll
	 * @param value
	 *            Der Wert der Notiz, die zu entfernen ist.
	 * @throw IllegalArgumentException Wird geworfen, falls das übergebene Field
	 *        null ist
	 */
	public void onNoteDelete(Field field, int value);

	/**
	 * Wird aufgerufen, falls der Benutzer einem Feld einen Lösungswert
	 * hinzufügt.
	 * 
	 * @param field
	 *            Das Feld, welchem der Lösungswert hinzugefügt werden soll
	 * @param value
	 *            Der Wert der Eingabe
	 * @throw IllegalArgumentException Wird geworfen, falls das übergebene Field
	 *        null ist
	 */
	public void onAddEntry(Field field, int value);

	/**
	 * Wird aufgerufen, falls der Benutzer einen Eintrag aus einem Feld
	 * entfernt.
	 * 
	 * @param field
	 *            Das Feld, aus welchem der Eintrag entfernt werden soll
	 * @throw IllegalArgumentException Wird geworfen, falls das übergebene Field
	 *        null ist
	 */
	public void onDeleteEntry(Field field);
}
