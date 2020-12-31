/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import de.sudoq.model.sudoku.Cell;

/**
 * Dieses Listenerinterface stellt Methoden bereit, sodass die implementierende
 * Klasse auf Hilfestellungsanfragen reagieren kann und diese gegebenenfalls
 * ausführt.
 */
public interface AssistanceRequestListener {
	/**
	 * 
	 * Löst im aktuellen Sudoku ein beliebiges Feld.
	 * 
	 * @return true, sollte das lösen eines Feldes erfolgreich sein, ansonsten
	 *         false.
	 */
	public boolean onSolveOne();

	/**
	 * Löst im aktuellen Sudoku das aktuell ausgewählte Feld.
	 * 
	 * @param cell
	 *            das zu lösende Feld
	 * @return true, sollte das lösen des aktuellen Feldes erfolgreich sein,
	 *         ansonsten false.
	 */
	public boolean onSolveCurrent(Cell cell);

	/**
	 * Löst das komplette Sudoku. Das entspricht der Aufgabe des Spielenden.
	 * 
	 * @return true, sollte das lösen des Sudokus erfolgreich sein, ansonsten
	 *         false.
	 */
	public boolean onSolveAll();
}
