/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes

/**
 * This enum defines the permutations that are admissible on a sudoku.
 */
enum class PermutationProperties {

    /** Rotation by 90°. */
    rotate90,

    /** Rotation by 180°. */
    rotate180,

    /** Permutation of Block columns */
    horizontal_Blockshift,

    /** Permutation of Block rows */
    vertical_Blockshift,

    /** Permutation of columns within a block */
    inBlock_Collumnshift,

    /** Permutation of  rows within a block */
    inBlock_Rowshift,

    /** Mirroring at the diagonal from bottom left to top right */
    diagonal_up,

    /** Mirroring at the diagonal from top left to bottom right */
    diagonal_down,

    /** Mirroring at the top to bottom axis */
    mirror_horizontal,

    /** Mirroring at the links to right axis */
    mirror_vertical
}