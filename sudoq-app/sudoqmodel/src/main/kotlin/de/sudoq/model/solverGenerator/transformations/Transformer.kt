/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator.transformations

import de.sudoq.model.sudoku.Sudoku
import java.util.*

/**
 * Transforms a quadratic! Sudoku, so that it is still solvable with the same steps
 * but looks completely different.
 */
object Transformer {

    private val elementaryList: List<Permutation> = Vector(
        listOf(
            Rotate90(),
            Rotate180(),
            Rotate270(),
            MirrorHorizontally(),
            MirrorVertically(),
            MirrorDiagonallyDown(),
            MirrorDiagonallyUp()
        )
    )

    private val subtleList: List<Permutation> = Vector(
        listOf(
            HorizontalBlockPermutation(),
            VerticalBlockPermutation(),
            InBlockColumnPermutation(),
            InBlockRowPermutation()
        )
    )


    /**
     * Random object. Can be set to have it deterministic during testing.
     * Is reset to nondeterministic after every execution of transform().
     *
     */
    @JvmStatic
    var random: Random = Random()


    /**
     * This method transforms the specified Sudoku if possible several times as follows:
     * 1. swap two rows / columns of blocks
     * 2. swap two rows / columns of cells
     * 3. Rotate
     * 4. (mirror)
     * 5. swapping symbols (e.g. replace all 4s with 7s, ...)
     *
     * After an arbitrary finite number of such modifications the Sudoku is still
     * uniquely(eindeutig) solvable, but looks very different.
     *
     * @param sudoku [Sudoku] to transform
     */
    @JvmStatic
    fun transform(sudoku: Sudoku) {
        // not rotateClockwise and mirror! results in Clockrotation(grouptheory)
        elementaryPermutation(sudoku)
        subtlePermutation(sudoku)
        elementaryPermutation(sudoku)
        subtlePermutation(sudoku)
        elementaryPermutation(sudoku)
        changeSymbols(sudoku)
        sudoku.increaseTransformCount()
        random = Random()
    }

    /**
     * Executes elementary permutations on the [Sudoku], e.g. mirroring and rotations
     *
     * @param sudoku [Sudoku] to transform
     */
    private fun elementaryPermutation(sudoku: Sudoku) {
        val l: MutableList<Permutation> = Vector()
        for (p in elementaryList) {
            if (sudoku.sudokuType.permutationProperties.contains(p.condition)) {
                l.add(p)
            }
        }
        //if we were functional...
        //List<Permutation> l = elementaryList.stream().filter(p -> sudoku.getSudokuType().getPermutationProperties().contains(p.getCondition()))
        //		                                     .collect(Collectors.toList());
        if (l.size > 0) {
            l[random.nextInt(l.size)].permutate(sudoku)
        }
    }

    /**
     * Executes special [Permutation]s such as
     * block permutations and row permutations within a block
     *
     * @param sudoku [Sudoku] to transform
     */
    private fun subtlePermutation(sudoku: Sudoku) {
        /* make sure only allowed permutations are executed by intersecting subtleList with properties of the sudoku */
        val l: MutableList<Permutation> = Vector()
        for (p in subtleList) if (sudoku.sudokuType.permutationProperties.contains(p.condition)) l.add(
            p
        )
        for (p in l) p.permutate(sudoku)


        //if we were functional
        //List<Permutation> l = subtleList.stream().filter(p->sudoku.getSudokuType().getPermutationProperties().contains(p.getCondition()))
        //		                                 .collect(Collectors.toList());

        //l.forEach( p -> p.permutate(sudoku) );
    }


}