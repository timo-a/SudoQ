/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator.transformations;

import java.util.HashMap;
import java.util.Map;

import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

/**
 * This class provides the fundamental building blocks for transformations
 * the actual transformation classes will use a combination of the methods provided here
 * @author timo
 *
 */
public final class TransformationUtilities {

	private TransformationUtilities() {
	}

	/* elementare Permutationen: alle Felder werden verschoben */

	protected static void rotate90(Sudoku sudoku) {
		mirrorDiagonallyDown(sudoku);
		mirrorHorizontally(sudoku);
	}

	protected static void rotate180(Sudoku sudoku) {
		rotate90(sudoku);
		rotate90(sudoku);
	}

	protected static void rotate270(Sudoku sudoku) {
		rotate90(sudoku);
		rotate90(sudoku);
		rotate90(sudoku);
	}

	protected static void mirrorHorizontally(Sudoku sudoku) {
		int width = sudoku.getSudokuType().getSize().getX();

		for (int i = 0; i < width / 2; i++) {
			TransformationUtilities.swap_collums(sudoku, i, width - 1 - i);
		}
	}

	protected static void mirrorVertically(Sudoku sudoku) {
		mirrorHorizontally(sudoku);
		rotate180(sudoku);
	}

	protected static void mirrorDiagonallyDown(Sudoku sudoku) {
		int width = sudoku.getSudokuType().getSize().getX();

		for (int i = 0; i < width - 1; i++) {// zeilen
			for (int j = i + 1; j < width; j++) {// zeilenElemente
				Field tmp = sudoku.getField(Position.get(i, j));
				sudoku.setField(sudoku.getField(Position.get(j, i)), Position.get(i, j));
				sudoku.setField(tmp, Position.get(j, i));
			}
		}
	}

	protected static void mirrorDiagonallyUp(Sudoku sudoku) {
		mirrorHorizontally(sudoku);
		rotate90(sudoku);
	}

	/* Spezielle Permutationen: nur manche Felder werden vertauscht */

	/**
	 * Führt in jeder Blockzeile für jede Zeile eine Zeilenvertauschung durch.
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem die Permutation ausgeführt werden soll
	 */
	protected static void inBlockRowPermutation(Sudoku sudoku) {
		rotate90(sudoku);
		TransformationUtilities.inBlockCollumnPermutation(sudoku, sudoku.getSudokuType().getBlockSize().getY());
		rotate270(sudoku);
	}

	/**
	 * Führt in jeder Blockspalte für jede Spalte eine Spaltenvertauschung durch.
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem die Permutation ausgeführt werden soll
	 */
	protected static void inBlockCollumnPermutation(Sudoku sudoku) {
		inBlockCollumnPermutation(sudoku, sudoku.getSudokuType().getBlockSize().getX());
	}

	/**
	 * verschiebt Blöcke in horizontaler Richtung
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem die Permutation ausgeführt werden soll
	 */
	protected static void horizontalBlockPermutation(Sudoku sudoku) {
		int collumnsPerBlock = sudoku.getSudokuType().getBlockSize().getX();
		int numberOfHorizontalBlocks = sudoku.getSudokuType().getSize().getX() / collumnsPerBlock;

		rotate_horizontally_By1(sudoku, numberOfHorizontalBlocks, collumnsPerBlock);
		horizontalBlockSwaps(sudoku, numberOfHorizontalBlocks, collumnsPerBlock);

	}

	/**
	 * verschiebt Blöcke in vertikaler Richtung
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem die Permutation ausgeführt werden soll
	 */
	protected static void verticalBlockPermutation(Sudoku sudoku) {
		int rowsPerBlock = sudoku.getSudokuType().getBlockSize().getY();
		int numberOfVertikalBlocks = sudoku.getSudokuType().getSize().getY() / rowsPerBlock;

		new Rotate90().permutate(sudoku);

		rotate_horizontally_By1(sudoku, numberOfVertikalBlocks, rowsPerBlock);

		horizontalBlockSwaps(sudoku, numberOfVertikalBlocks, rowsPerBlock);

		new Rotate270().permutate(sudoku);
	}

	// collumn von 0 bis numberOfCollumnsInBlock - 1
	private static void swapCollumOfBlocks(Sudoku sudoku, int collumn1, int collumn2, int numberOfCollumnsInBlock) {
		if (collumn1 != collumn2) {
			for (int i = 0; i < numberOfCollumnsInBlock; i++) {
				TransformationUtilities.swap_collums(sudoku, collumn1 * numberOfCollumnsInBlock + i, collumn2
						* numberOfCollumnsInBlock + i);
			}
		}
	}

	/* vertauscht zwei Spalten */
	private static void swap_collums(Sudoku sudoku, int collum1, int collum2) {
		int height = sudoku.getSudokuType().getSize().getY();

		for (int j = 0; j < height; j++) {

			Position a = Position.get(collum1, j);
			Position b = Position.get(collum2, j);

			Field tmp = sudoku.getField(a);

			Field target = sudoku.getField(b);

			sudoku.setField(target, a);

			sudoku.setField(tmp, b);
		}
	}

	/* verschiebt jeden Block um eins nach rechts */
	private static void rotate_horizontally_By1(Sudoku sudoku, int numberOfHorizontalBlocks, int blocklength) {
		for (int i = 0; i < numberOfHorizontalBlocks - 1; i++)
			TransformationUtilities.swapCollumOfBlocks(sudoku, i, i + 1, blocklength);
	}

	/* Vertauscht Blockspalten */
	private static void horizontalBlockSwaps(Sudoku sudoku, int numberOfHorizontalBlocks, int collumnsPerBlock) {
		int limit = numberOfHorizontalBlocks / 2 - (1 - numberOfHorizontalBlocks % 2);

		for (int i = 0; i < limit; i++) {

			int first = Transformer.getRandom().nextInt(numberOfHorizontalBlocks);

			int other = randomOtherNumber(first, numberOfHorizontalBlocks);

			swapCollumOfBlocks(sudoku, first, other, collumnsPerBlock);

		}
	}

	/**
	 * Führt in jeder Blockspalte blockWidth Spaltenvertauschungen aus
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem die Permutation ausgeführt werden soll
	 * @param blockWidth
	 *            Anzahl an Spalten pro Block
	 */
	private static void inBlockCollumnPermutation(Sudoku sudoku, int blockWidth) {
		int numberOfHorizontalBlocks = sudoku.getSudokuType().getSize().getX()
				/ sudoku.getSudokuType().getBlockSize().getX();

		for (int i = 0; i < numberOfHorizontalBlocks; i++) {
			for (int j = 0; j < blockWidth; j++) {
				int first = Transformer.getRandom().nextInt(blockWidth);
				swap_collums(sudoku, i * blockWidth + first, i * blockWidth + randomOtherNumber(first, blockWidth));
			}
		}
	}

	/* Symbole vertauschen */
	/**
	 * Vertauscht zufällig die Lösungen des Sudokus, wobei gleiche Lösungen gleiche neue Lösungen erhalten.
	 * 
	 * @param sudoku
	 *            das Sudoku dessen Felder manipuliert werden sollen
	 */
	protected static void changeSymbols(Sudoku sudoku) {
		Map<Integer, Integer> permutationRule = TransformationUtilities.createPermutation(sudoku);

		for (Position p: sudoku.getSudokuType().getValidPositions()) {

			Field f = sudoku.getField(p);
			int oldSymbol = f.getSolution();
			int newSymbol = permutationRule.get(oldSymbol);
			if (newSymbol != oldSymbol) // nur wenn sich was ändert, sonst bleibts ja gleich
				sudoku.setField(new Field(f.isEditable(), newSymbol, f.getId(), f.getNumberOfValues()), p);
			
		}
	}

	/*
	 * gibt eine Map mit einer Permutationsvorschrift zurück. Die Identität ist als Rückgabewert ausgeschlossen
	 */
	private static Map<Integer, Integer> createPermutation(Sudoku sudoku) {
		Map<Integer, Integer> permutationRule = new HashMap<Integer, Integer>();
		int numberOfSymbols = sudoku.getSudokuType().getNumberOfSymbols();
		int tries = (int) Math.sqrt(numberOfSymbols); // wurzel(numberOfElements)
														// versuche, damit wir
														// deterministisch
														// bleiben

		boolean success;
		for (int i = 0; i < numberOfSymbols; i++) {
			success = false;
			for (int j = 0; j < tries && !success; j++) {

				int otherNum = randomOtherNumber(i, numberOfSymbols);
				if (!permutationRule.containsValue(otherNum)) {
					permutationRule.put(i, otherNum);
					success = true;
				}
			}
			for (int j = 0; !success && j < numberOfSymbols; j++) {
				if (!permutationRule.containsValue(j)) {
					permutationRule.put(i, j);
					break;
				}
			}
		}
		return permutationRule;
	}

	/* gibt eine zahl < range zurück aber nicht num */
	private static int randomOtherNumber(int num, int range) {

		int distance = Transformer.getRandom().nextInt(range - 1) + 1;
		return (num + distance) % range;
	}

}
