/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes

/**
 * This enum defines the sudoku types.
 */
enum class SudokuTypes {

    /*
     * A Standard Sudoku.
     */
    standard9x9,  //for legacy reasons 9x9 has ord = 0

    /**
     * A 4x4 Sudoku with normal Rules.
     */
    standard4x4,

    /**
     * A 6x6 Sudoku with normal Rules.
     */
    standard6x6,

    /**
     * A 16x16 Sudoku with normal Rules.
     */
    standard16x16,

    /**
     * Five 9x9 Sudokus where 4 share a corner block with the 5th which is in the middle.
     */
    samurai,

    /**
     * A 9x9 Sudoku where the diagonals are additional constraints.
     */
    Xsudoku,

    /**
     * A Standard Sudoku with the following additional constraints:
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
     * A 9x9 Sudoku where the blocks are shaped like this:
     *
     * AA BBBBB CC
     * AAA BBB CCC
     * D AAA B CCC E
     * DD A FFF C EE
     * DDD  FFF  EEE
     * DD G FFF H EE
     * D GGG I HHH E
     * GGG III HHH
     * GG IIIII HH
     *
     */
    squigglya,

    /**
     * A 9x9 Sudoku where the blocks are shaped like this:
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
     * A 9x9 Sudoku where the blocks are shaped like this:
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