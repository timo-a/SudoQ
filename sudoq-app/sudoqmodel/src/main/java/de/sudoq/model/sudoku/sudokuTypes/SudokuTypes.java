/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

/**
 * Dieses Enum definiert die verfügbaren Sudoku-Sorten.
 */
public enum SudokuTypes {
	/* Attributes */

	/**
	 * Ein Standard-Sudoku.
	 */
	standard9x9,

	/**
	 * Ein 4x4 Sudoku mit normalen Regeln.
	 */
	standard4x4,
	/**
	 * Ein 6x6 Sudoku mit normalen Regeln.
	 */
	standard6x6,
	/**
	 * Ein 16x16 Sudoku mit normalen Regeln.
	 */
	standard16x16,

	/**
	 * Fünf 9x9 Sudokus wobei sich je 4 einen Eckblock mit dem 5. zentralen
	 * Sudoku teilen
	 */
	samurai,

	/**
	 * Ein 9x9 Sudoku bei dem zusätzlich die Diagonalen mit 9 verschiedenen
	 * Symbolen gefüllt werden müssen.
	 */
	Xsudoku,

	/**
	 * Ein StandardSudoku bei dem zusätzlich folgende Constraints erfüllt sein
	 * müssen:
	 * 
	 * ......... 
	 * .AAA.BBB. 
	 * .AAA.BBB. 
	 * .AAA.BBB. 
	 * ......... 
	 * .CCC.DDD. 
	 * .CCC.DDD.
	 * .CCC.DDD. 
	 * .........
	 * 
	 */
	HyperSudoku,

	/**
	 * Ein 9x9 Sudoku mit folgender Constraintbelegung:
	 * 
	 *  AA BBBBB CC
	 *  AAA BBB CCC
	 * D AAA B CCC E
	 * DD A FFF C EE
	 * DDD  FFF  EEE
	 * DD G FFF H EE
	 * D GGG I HHH E
	 *  GGG III HHH
	 *  GG IIIII HH
	 * 
	 */
	squigglya,
	/**
	 * Ein 9x9 Sudoku mit folgender Constraintbelegung:
	 * 
	 * aaaaa      bbbb 
	 * aa  cccc  s  bb 
	 * a ccz # c ss  b 
	 * a zzz # cc  s b 
	 * g z #####   s b 
	 * g z oo #  sss i 
	 * g zz o # s oo i 
	 * gg z   oooo  ii 
	 * gggg      iiiii
	 * 
	 */
	squigglyb,
	/**
	 * Ein 9x9 Sudoku mit folgender Constraintbelegung:
	 * 
	 * aaaa bbb cc 
	 * aaa bbb ccc 
	 * aa bbb cccc 
	 * dddd eee ff 
	 * ddd eee fff 
	 * dd eee ffff
	 * gggg hhh ii 
	 * ggg hhh iii 
	 * gg hhh iiii
	 * 
	 */
	stairstep

}
