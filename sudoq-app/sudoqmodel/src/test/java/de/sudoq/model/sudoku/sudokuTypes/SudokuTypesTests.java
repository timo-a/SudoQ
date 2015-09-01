package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SudokuTypesTests {

	@Test
	public void test() {
		SudokuTypes[] types = SudokuTypes.values();
		for (SudokuTypes type : types) {
			assertTrue(SudokuTypes.valueOf(type.toString()).equals(type));
		}
	}

}
