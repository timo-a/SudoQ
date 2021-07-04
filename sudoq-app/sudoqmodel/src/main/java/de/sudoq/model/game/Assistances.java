/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

/**
 * Dieses Enum stellt Flags zur Verfügung, mit denen die Verfügbarkeit einer entsprechenden Hilfestellung repräsentiert
 * werden kann. Dabei gibt es passive Hilfestellungen (solche die automatisch ausgeführt werden) und aktive
 * Hilfestellungen, die vom Benutzer angefordert werden müssen.
 */
public enum Assistances {
    /* Attributes */

	/**
	 * Die Zeile und Spalte, in der sich das momentan ausgewählte Feld befindet, werden markiert (passiv).
	 */
	markRowColumn,

	/**
	 * Wird ein Symbol eingegeben und entspricht nicht der korrekten Lösung für dieses Feld, so wird dies angezeigt
	 * (passiv).
	 */
	markWrongSymbol,

	/**
	 * Es können nur Symbole in einem Feld eingetragen werden, welche bei deren Eingabe die Constraints des Sudokus
	 * nicht verletzen (passiv).
	 */
	restrictCandidates,

	/**
	 * Die Notizen werden bei Änderungen am Sudokus automatisch geändert, sodass nur Symbole als Notizen eingetragen
	 * sind, die bei ihrer Eingabe in das Feld die Constraints des Sudokus nicht verletzen würden.
	 */
	autoAdjustNotes


}
