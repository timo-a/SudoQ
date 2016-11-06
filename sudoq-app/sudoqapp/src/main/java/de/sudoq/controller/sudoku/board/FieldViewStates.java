/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku.board;

/**
 * Dieses Enum definiert Konstanten für Animationen bzw. Markierungen für
 * Felder.
 */
public enum FieldViewStates {
	/** Animation */

	/**
	 * Animation für ein zur Eingabe selektiertes Feld
	 */
	SELECTED_INPUT,

	/**
	 * Animation für ein für Notizen selektiertes Feld
	 */
	SELECTED_NOTE,

	/**
	 * Zur Eingabe selektiertes Feld mit Rahmen
	 */
	SELECTED_INPUT_BORDER,

	/**
	 * Für Notizen selektiertes Feld mit Rahmen
	 */
	SELECTED_NOTE_BORDER,

	/**
	 * Zur Eingabe selektiertes Feld mit falschem Inhalt
	 */
	SELECTED_INPUT_WRONG,

	/**
	 * Für Notizen selektiertes Feld mit falschem Inhalt
	 */
	SELECTED_NOTE_WRONG,

	/**
	 * Animation für ein ausgewähltes, nicht editierbares Feld
	 */
	SELECTED_FIXED,

	/**
	 * Animation für ein mit dem selektierten verbundenes Feld
	 */
	CONNECTED,

	/**
	 * Verbundenes Feld mit falschem Eintrag
	 */
	CONNECTED_WRONG,

	/**
	 * Ein Feld, welches vorgegeben ist
	 */
	FIXED,

	/**
	 * Zeichnet das Standart-Feld-Aussehen
	 */
	DEFAULT,

	/**
	 * Zeichnet das Standard Feld Ausstehen mit Rahmen
	 */
	DEFAULT_BORDER,

	/**
	 * Zeichnet das Standard Feld Aussehen mit falschem Eintrag
	 */
	DEFAULT_WRONG,

	/**
	 * Zeichnet den Hintergrund für die Controls
	 */
	CONTROLS,

	/**
	 * Zeichnet den Hintergrund für die Tastatur
	 */
	KEYBOARD,

	/**
	 * Zeichnet den Hintergrund für das Sudoku
	 */
	SUDOKU,
}
