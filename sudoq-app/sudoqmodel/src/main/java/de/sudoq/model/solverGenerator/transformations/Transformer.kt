/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator.transformations

import de.sudoq.model.sudoku.Sudoku
import java.util.*

/**
 * Transformer Klasse. Transformiert ein quadratisches! Sudoku, so dass es nach wie vor mit den gleichen Schritten
 * lösbar ist, aber völlig anders aussieht.
 */
object Transformer {
    val elementaryList: MutableList<Permutation>? = null
    val subtleList: MutableList<Permutation>? = null

    /**
     * Für Tests kann ein eigenes deterministisches 'random'-Object verwendet werden. Das Randomobjekt von Transformer
     * ist standardmäßig nichtdeterministisch und wird nach jedem Aufruf von transform() wieder auf nichtdeterministisch
     * gesetzt.
     *
     * @param r
     * ein neues randomObject
     */
    var random: Random? = null

    /**
     * Diese Methode transformiert das spezifizierte Sudoku falls möglich mehrmals auf folgende Weisen:<br></br>
     * 1. Vertauschen zweier Zeilen / Spalten von Blöcken<br></br>
     * 2. Vertauschen zweier Zeilen / Spalten von Feldern<br></br>
     * 3. Drehen 4. (Spiegeln) 5. Seien x und y zwei Symbole. Im gesamten Sudoku wird x durch y ersetzt und umgekehrt.<br></br>
     *
     * Nach einer beliebigen, endlichen Anzahl solcher Modifikationen, ist das Sudoku noch immer eindeutig lösbar,
     * unterscheidet sich aber im Allgemeinen gänzlich vom ursprünglichen Sudoku.
     *
     * @param sudoku
     * Das zu modifizierende Sudoku
     */
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
     * Führt elementare Permutationen auf dem übergebenen Sudoku aus, z.B. Spiegelungen und Drehungen
     *
     * @param sudoku
     * das Sudoku dessen Felder permutiert werden
     */
    private fun elementaryPermutation(sudoku: Sudoku) {
        val l: MutableList<Permutation> = Vector()
        for (p in elementaryList!!) {
            if (sudoku.sudokuType!!.permutationProperties.contains(p.condition)) {
                l.add(p)
            }
        }
        //if we were functional...
        //List<Permutation> l = elementaryList.stream().filter(p -> sudoku.getSudokuType().getPermutationProperties().contains(p.getCondition()))
        //		                                     .collect(Collectors.toList());
        if (l.size > 0) {
            l[random!!.nextInt(l.size)].permutate(sudoku)
        }
    }

    /**
     * Führt spezielle Permutationen auf dem übergebenen Sudoku aus, z.B. BlockPermutationen und ZeilenPermutationen
     * innerhalb eines Blocks
     *
     * @param sudoku
     * das Sudoku dessen Felder permutiert werden
     */
    private fun subtlePermutation(sudoku: Sudoku) {
        /* make sure only allowed permutations are executed by intersecting subtleList with properties of the sudoku */
        val l: MutableList<Permutation> = Vector()
        for (p in subtleList!!) if (sudoku.sudokuType!!.permutationProperties.contains(p.condition)) l.add(p)
        for (p in l) p.permutate(sudoku)


        //if we were functional
        //List<Permutation> l = subtleList.stream().filter(p->sudoku.getSudokuType().getPermutationProperties().contains(p.getCondition()))
        //		                                 .collect(Collectors.toList());

        //l.forEach( p -> p.permutate(sudoku) );
    }

    init {
        elementaryList = Vector()
        elementaryList.add(Rotate90())
        elementaryList.add(Rotate180())
        elementaryList.add(Rotate270())
        elementaryList.add(MirrorHorizontally())
        elementaryList.add(MirrorVertically())
        elementaryList.add(MirrorDiagonallyDown())
        elementaryList.add(MirrorDiagonallyUp())
    }

    init {
        subtleList = Vector()
        subtleList.add(HorizontalBlockPermutation())
        subtleList.add(VerticalBlockPermutation())
        subtleList.add(InBlockColumnPermutation())
        subtleList.add(InBlockRowPermutation())
    }

    init {
        random = Random()
    }
}