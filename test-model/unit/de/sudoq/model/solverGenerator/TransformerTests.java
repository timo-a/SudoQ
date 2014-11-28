package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.transformations.Transformer;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class TransformerTests implements GeneratorCallback {

	PositionMap<Integer> map = new PositionMap<Integer>(Position.get(9, 9));

	@Test
	public void transformS99Test1() {

		int[] values = { 9, 5, 8, 3, 1, 2, 7, 6, 4, 4, 6, 1, 5, 7, 9, 8, 2, 3, 3, 7, 2, 4, 6, 8, 9, 5, 1, 8, 9, 6, 1,
				2, 3, 5, 4, 7, 1, 4, 3, 7, 9, 5, 2, 8, 6, 5, 2, 7, 6, 8, 4, 3, 1, 9, 7, 8, 5, 9, 4, 1, 6, 3, 2, 2, 1,
				9, 8, 3, 6, 4, 7, 5, 6, 3, 4, 2, 5, 7, 1, 9, 8 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.get99(), initializeMap(9, values), new PositionMap<Boolean>(Position.get(9, 9)));

		for (int i = 0; i < 1337; i++)
			Transformer.transform(sudoku1);

		// printSudoku9x9(sudoku1, 9);
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformS66Test1() {

		int[] values = { 5, 2, 6, 3, 1, 4, 4, 3, 1, 6, 2, 5, 1, 6, 5, 4, 3, 2, 2, 4, 3, 5, 6, 1, 3, 1, 4, 2, 5, 6, 6,
				5, 2, 1, 4, 3 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.standard6x6), initializeMap(6, values), new PositionMap<Boolean>(Position.get(6, 6)));
		for (int i = 0; i < 3; i++) {
			Transformer.transform(sudoku1);
		}

		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformS44Test1() {

		int[] values = { 3, 2, 4, 1, 1, 4, 2, 3, 4, 3, 1, 2, 2, 1, 3, 4 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.standard4x4), initializeMap(4, values), new PositionMap<Boolean>(Position.get(4, 4)));
		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformS1616Test1() {

		map = new PositionMap<Integer>(Position.get(16, 16));

		int[] values = { 1, 2, 13, 7, 11, 3, 4, 15, 16, 9, 10, 6, 12, 14, 8, 5, 8, 15, 14, 5, 10, 13, 7, 9, 2, 1, 12,
				11, 16, 3, 4, 6, 6, 12, 3, 11, 8, 1, 14, 16, 4, 7, 15, 5, 2, 9, 10, 13, 4, 16, 9, 10, 5, 12, 2, 6, 8,
				3, 13, 14, 11, 15, 7, 1,

				10, 5, 11, 13, 3, 4, 12, 14, 1, 2, 16, 15, 6, 8, 9, 7, 3, 6, 12, 8, 15, 11, 9, 1, 10, 4, 5, 7, 13, 2,
				14, 16, 9, 4, 1, 16, 7, 5, 13, 2, 6, 14, 8, 12, 10, 11, 3, 15, 14, 7, 15, 2, 16, 10, 6, 8, 11, 13, 3,
				9, 5, 12, 1, 4,

				16, 9, 4, 3, 13, 15, 8, 12, 7, 6, 2, 1, 14, 5, 11, 10, 15, 10, 2, 6, 1, 7, 11, 5, 13, 12, 14, 8, 9, 4,
				16, 3, 7, 13, 5, 12, 9, 14, 10, 3, 15, 11, 4, 16, 1, 6, 2, 8, 11, 14, 8, 1, 2, 6, 16, 4, 3, 5, 9, 10,
				7, 13, 15, 12,

				13, 3, 10, 14, 6, 9, 15, 7, 5, 8, 1, 2, 4, 16, 12, 11, 12, 11, 6, 15, 4, 2, 3, 10, 9, 16, 7, 13, 8, 1,
				5, 14, 5, 8, 7, 9, 14, 16, 1, 11, 12, 15, 6, 4, 3, 10, 13, 2, 2, 1, 16, 4, 12, 8, 5, 13, 14, 10, 11, 3,
				15, 7, 6, 9 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.standard16x16), initializeMap(16, values), new PositionMap<Boolean>(Position.get(16, 16)));

		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformSudokuXTest1() {

		map = new PositionMap<Integer>(Position.get(9, 9));

		int[] values = { 2, 8, 5, 6, 7, 9, 4, 3, 1, 3, 9, 1, 5, 4, 2, 6, 8, 7, 4, 7, 6, 8, 1, 3, 9, 5, 2, 8, 4, 2, 1,
				6, 5, 3, 7, 9, 1, 5, 9, 7, 3, 4, 8, 2, 6, 6, 3, 7, 2, 9, 8, 5, 1, 4, 9, 2, 4, 3, 5, 1, 7, 6, 8, 5, 6,
				8, 9, 2, 7, 1, 4, 3, 7, 1, 3, 4, 8, 6, 2, 9, 5 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.Xsudoku), initializeMap(9, values), new PositionMap<Boolean>(Position.get(9, 9)));

		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformSudokuHyperTest1() {

		map = new PositionMap<Integer>(Position.get(9, 9));

		int[] values = { 8, 4, 5, 2, 1, 3, 6, 7, 9, 9, 3, 7, 4, 6, 5, 2, 1, 8, 2, 6, 1, 9, 7, 8, 3, 4, 5,

				1, 2, 8, 5, 4, 7, 9, 6, 3, 4, 7, 6, 8, 3, 9, 1, 5, 2, 5, 9, 3, 6, 2, 1, 7, 8, 4,

				6, 8, 4, 1, 9, 2, 5, 3, 7, 3, 5, 2, 7, 8, 6, 4, 9, 1, 7, 1, 9, 3, 5, 4, 8, 2, 6 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.HyperSudoku), initializeMap(9, values), new PositionMap<Boolean>(Position.get(9, 9)));
		assertTrue(validSudoku(sudoku1));
		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformSudokuStairStepTest1() {

		map = new PositionMap<Integer>(Position.get(9, 9));

		int[] values = { 7, 8, 1, 3, 4, 6, 5, 2, 9, 2, 9, 6, 1, 8, 3, 4, 5, 7, 5, 4, 7, 2, 9, 1, 6, 8, 3,

				9, 6, 8, 7, 2, 4, 1, 3, 5, 3, 2, 4, 8, 5, 9, 7, 1, 6, 1, 5, 3, 6, 7, 2, 8, 9, 4,

				6, 7, 5, 9, 3, 8, 2, 4, 1, 4, 3, 2, 5, 1, 7, 9, 6, 8, 8, 1, 9, 4, 6, 5, 3, 7, 2 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.stairstep), initializeMap(9, values), new PositionMap<Boolean>(Position.get(9, 9)));
		assertTrue(validSudoku(sudoku1));
		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformSudokuSquigglyATest1() {

		map = new PositionMap<Integer>(Position.get(9, 9));

		int[] values = { 6, 7, 4, 3, 9, 5, 1, 8, 2, 5, 4, 1, 8, 7, 2, 6, 3, 9, 9, 3, 8, 2, 6, 1, 5, 7, 4,

				3, 1, 9, 6, 5, 8, 4, 2, 7, 4, 5, 6, 7, 2, 3, 9, 1, 8, 7, 8, 3, 1, 4, 9, 2, 6, 5,

				2, 6, 5, 4, 1, 7, 8, 9, 3, 1, 2, 7, 9, 8, 4, 3, 5, 6, 8, 9, 2, 5, 3, 6, 7, 4, 1 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.squigglya), initializeMap(9, values), new PositionMap<Boolean>(Position.get(9, 9)));
		assertTrue(validSudoku(sudoku1));
		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformSudokuSquigglyBTest1() {

		map = new PositionMap<Integer>(Position.get(9, 9));

		int[] values = { 8, 9, 2, 5, 1, 4, 6, 3, 7,
				6, 3, 4, 7, 8, 9, 5, 2, 1,
				7, 1, 5, 3, 4, 6, 2, 9, 8,
				4, 8, 7, 6, 9, 2, 3, 1, 5,
				2, 4, 3, 8, 5, 7, 1, 6, 9,
				1, 5, 6, 9, 2, 8, 7, 4, 3,
				5, 2, 9, 1, 6, 3, 8, 7, 4,
				9, 7, 1, 2, 3, 5, 4, 8, 6,
				3, 6, 8, 4, 7, 1, 9, 5, 2 };

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.squigglyb), initializeMap(9, values), new PositionMap<Boolean>(Position.get(9, 9)));
		assertTrue(validSudoku(sudoku1));
		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	@Test
	public void transformSamuraiTest() {

		String values = "819247365   983472651" +
				"274365198   751863429" +
				"653891472   426951378" +
				"496183257   697514283" +
				"731529846   235689714" +
				"582674931   814237965" +
				"328956714598362148597" +
				"145738629731548796132" +
				"967412583642179325846" +
				"      398416257      " +
				"      452387916      " +
				"      176259834      " +
				"814327965124783251649" +
				"732695841973625947813" +
				"956481237865491836572" +
				"329148576   156792384" +
				"167253489   249183756" +
				"548976123   837564921" +
				"695834712   378419265" +
				"283719654   564328197" +
				"471562398   912675438";

		Sudoku sudoku1 = new Sudoku(TypeBuilder.getType(SudokuTypes.samurai), initializeSamuraiMap(values), new PositionMap<Boolean>(Position.get(21, 21)));
		assertTrue(validSudoku(sudoku1));
		for (int i = 0; i < 100; i++) {
			Transformer.transform(sudoku1);
		}
		assertTrue(validSudoku(sudoku1));
	}

	private PositionMap<Integer> initializeSamuraiMap(String values) {
		PositionMap<Integer> map = new PositionMap<Integer>(Position.get(21, 21));
		int length = 21;
		for (int y = 0; y < length; y++)
			for (int x = 0; x < length; x++)
				if (values.charAt(y * 21 + x) != ' ')
					map.put(Position.get(x, y), Integer.parseInt(values.charAt(y * 21 + x) + "") - 1);

		return map;

	}

	private PositionMap<Integer> initializeMap(int length, int[] values) {
		for (int i = 0; i < values.length; i++) {
			values[i]--;
		}
		// assertTrue(ss99[0] == 8);

		for (int y = 0; y < length; y++)
			for (int x = 0; x < length; x++)
				map.put(Position.get(x, y), values[y * length + x]);

		return map;
	}

	private boolean validSudoku(Sudoku sudoku) {
		int elcount;

		for (Constraint c : sudoku.getSudokuType()) {
			for (int i = 0; i < c.getSize(); i++) {
				elcount = 0;
				for (Position p : c) {
					if (sudoku.getField(p).getSolution() == i)
						elcount++;
				}
				if (elcount != 1) {
					return false;
				}
			}
		}
		return true;
	}

	public static void printSudoku9x9(Sudoku sudoku, int length) {
		System.out.println("tada:");
		for (int y = 0; y < length; y++) {
			for (int x = 0; x < length; x++)
				System.out.print(" " + sudoku.getField(Position.get(x, y)).getSolution());
			System.out.println();
		}
		System.out.println();
	}

	@Override
	public void generationFinished(Sudoku sudoku) {
		assertTrue(validSudoku(sudoku));
		// printSudoku9x9(sudoku, 9);
		notifyAll();
	}

}
