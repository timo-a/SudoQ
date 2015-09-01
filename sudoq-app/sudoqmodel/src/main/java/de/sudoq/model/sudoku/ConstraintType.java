/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

/**
 * Definiert die Typen von Constraints.
 */
public enum ConstraintType {
	/**
	 * Eine Zeile oder Spalte
	 */
	LINE,

	/**
	 * Ein Block, wobei alle Blöcke eines Sudokus sich nicht überschneiden
	 */
	BLOCK,

	/**
	 * Ein gesondertes Constraints, das sich mit Blöcken überschneiden kann.
	 */
	EXTRA

}
