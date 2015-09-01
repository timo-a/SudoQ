/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

/**
 * Dieses Enum definiert die auf einem Sudoku verfügbaren Permutationen.
 */
public enum PermutationProperties {
	/** Attributes */

	/**
	 * Rotation um 90°.
	 */
	rotate90,

	/**
	 * Rotation um 180°.
	 */
	rotate180,

	/**
	 * Permutation von BlockSpalten
	 */
	horizontal_Blockshift,

	/**
	 * Permutation von BlockZeilen
	 */
	vertical_Blockshift,

	/**
	 * Permutation von Spalten innerhalb von Blockgrenzen
	 */
	inBlock_Collumnshift,

	/**
	 * Permutation von Zeilen innerhalb von Blockgrenzen
	 */
	inBlock_Rowshift,
	
	/**
	 * Spiegelung an der Diagonalen von links unten nach recht oben
	 */
	diagonal_up,
	
	/**
	 * Spiegelung an der Diagonalen von links oben nach rechts unten
	 */
	diagonal_down,
	
	/**
	 * Spiegelung an einer mittigen Achse oben nach unten
	 */
	mirror_horizontal,
	
	/**
	 * Spiegelung an einer mittigen Achse links nach rechst
	 */
	mirror_vertical

}
