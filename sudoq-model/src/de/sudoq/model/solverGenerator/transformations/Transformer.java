/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator.transformations;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import de.sudoq.model.sudoku.Sudoku;

/**
 * Transformer Klasse. Transformiert ein quadratisches! Sudoku, so dass es nach wie vor mit den gleichen Schritten
 * lösbar ist, aber völlig anders aussieht.
 */
public class Transformer {

	static List<Permutation> elementaryList;

	static {
		elementaryList = new Vector<Permutation>();
		elementaryList.add(new Rotate90());
		elementaryList.add(new Rotate180());
		elementaryList.add(new Rotate270());
		elementaryList.add(new MirrorHorizontally());
		elementaryList.add(new MirrorVertically());
		elementaryList.add(new MirrorDiagonallyDown());
		elementaryList.add(new MirrorDiagonallyUp());
	}

	static List<Permutation> subtleList;

	static {
		subtleList = new Vector<Permutation>();
		subtleList.add(new HorizontalBlockPermutation());
		subtleList.add(new VerticalBlockPermutation());
		subtleList.add(new InBlockColumnPermutation());
		subtleList.add(new InBlockRowPermutation());
	}

	static {
		setRandom(new Random());
	}

	private static Random random;

	/**
	 * Für Tests kann ein eigenes deterministisches 'random'-Object verwendet werden. Das Randomobjekt von Transformer
	 * ist standardmäßig nichtdeterministisch und wird nach jedem Aufruf von transform() wieder auf nichtdeterministisch
	 * gesetzt.
	 * 
	 * @param r
	 *            ein neues randomObject
	 */
	public static void setRandom(Random r) {
		random = r;
	}

	public static Random getRandom() {
		return random;
	}

	/**
	 * Diese Methode transformiert das spezifizierte Sudoku falls möglich mehrmals auf folgende Weisen:<br>
	 * 1. Vertauschen zweier Zeilen / Spalten von Blöcken<br>
	 * 2. Vertauschen zweier Zeilen / Spalten von Feldern<br>
	 * 3. Drehen 4. (Spiegeln) 5. Seien x und y zwei Symbole. Im gesamten Sudoku wird x durch y ersetzt und umgekehrt.<br>
	 * 
	 * Nach einer beliebigen, endlichen Anzahl solcher Modifikationen, ist das Sudoku noch immer eindeutig lösbar,
	 * unterscheidet sich aber im Allgemeinen gänzlich vom ursprünglichen Sudoku.
	 * 
	 * @param sudoku
	 *            Das zu modifizierende Sudoku
	 */
	public static void transform(Sudoku sudoku) {
		// not rotateClockwise and mirror! results in Clockrotation(grouptheory)

		elementaryPermutation(sudoku);

		subtlePermutation(sudoku);

		elementaryPermutation(sudoku);

		subtlePermutation(sudoku);

		elementaryPermutation(sudoku);

		TransformationUtilities.changeSymbols(sudoku);

		sudoku.increaseTransformCount();

		setRandom(new Random());
	}

	/**
	 * Führt elementare Permutationen auf dem übergebenen Sudoku aus, z.B. Spiegelungen und Drehungen
	 * 
	 * @param sudoku
	 *            das Sudoku dessen Felder permutiert werden
	 */
	private static void elementaryPermutation(Sudoku sudoku) {
		List<Permutation> l = new Vector<Permutation>();
		for (Permutation p : elementaryList) {
			if (sudoku.getSudokuType().getPermutationProperties().contains(p.getCondition())) {
				l.add(p);
			}
		}
		if (l.size() > 0) {
			l.get(getRandom().nextInt(l.size())).permutate(sudoku);
		}
	}

	/**
	 * Führt spezielle Permutationen auf dem übergebenen Sudoku aus, z.B. BlockPermutationen und ZeilenPermutationen
	 * innerhalb eines Blocks
	 * 
	 * @param sudoku
	 *            das Sudoku dessen Felder permutiert werden
	 */
	private static void subtlePermutation(Sudoku sudoku) {
		List<Permutation> l = new Vector<Permutation>();
		for (Permutation p : subtleList) {
			if (sudoku.getSudokuType().getPermutationProperties().contains(p.getCondition())) {
				l.add(p);
			}
		}
		for (Permutation p : l) {
			p.permutate(sudoku);
		}
	}

}