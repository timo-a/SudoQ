/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

/**
 * Dieses Interface definiert eine check Methode, welche überprüft, ob ein
 * spezifiziertes Constraint ein bestimmtes Verhalten erfüllt.
 */
public interface ConstraintBehavior {
    /* Methods */

	/**
	 * Diese Methode überprüft, ob ein bestimmtes (durch die Methode
	 * implementiertes) Verhalten auf den durch das Constraint spezifizierten
	 * Feldern des uebergebenen Sudokus erfüllt ist. Ist das Constraint oder das
	 * Sudoku null, so wird false zurückgegeben.
	 * 
	 * @param constraint
	 *            Das Constraint, welches auf ein bestimmtes Verhalten überprüft
	 *            werden soll
	 * @param sudoku
	 *            Sudoku, auf welchem das Constraint ueberprueft werden soll
	 * @return true, falls das Constraint die Vorgaben erfüllt, false falls es
	 *         dies nicht tut oder null ist
	 */
    boolean check(Constraint constraint, Sudoku sudoku);
}
