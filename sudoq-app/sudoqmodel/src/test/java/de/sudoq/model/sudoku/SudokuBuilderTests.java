package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SudokuBuilderTests extends TestWithInitCleanforSingletons {

	private IRepo<SudokuTypeBE> str = new IRepo<SudokuTypeBE>() {
		@Override
		public SudokuTypeBE create() {
			return null;
		}

		@Override
		public SudokuTypeBE read(int id) {
			SudokuTypes e = SudokuTypes.values()[id];
			SudokuTypeBE stbe = null;
			switch (e){
				case standard9x9:
					stbe = new SudokuTypeBE();
					stbe.setEnumType(SudokuTypes.standard9x9);
					stbe.setSize(Position.get(9,9));
					stbe.setNumberOfSymbols(9);
					stbe.setBlockSize(Position.get(3,3));
			}
			return stbe;
		}

		@Override
		public SudokuTypeBE update(SudokuTypeBE sudokuTypeBE) {
			return null;
		}

		@Override
		public void delete(int id) {

		}
	};


	@BeforeClass
	public static void initFileManager() {
		TestWithInitCleanforSingletons.legacyInit();
	}


	Cell cell;

	static Map<SudokuTypes, Integer> specialParam = new HashMap<SudokuTypes, Integer>(4);
	
	static{
		specialParam.put(SudokuTypes.samurai, 21);
		specialParam.put(SudokuTypes.standard16x16, 16);
		specialParam.put(SudokuTypes.standard6x6, 6);
		specialParam.put(SudokuTypes.standard4x4, 4);
	}
	
	@Test
	public void testInitialisation() {
		for (SudokuTypes t : SudokuTypes.values()) {

			if(specialParam.containsKey(t))
				testBuildergeneric(t, specialParam.get(t));
			else
				testBuildergeneric(t, 9);
		}
	}

	private void testBuildergeneric(SudokuTypes t, int length) {
		Sudoku sudoku = new SudokuBuilder(t, str).createSudoku();
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				cell = sudoku.getCell(Position.get(i, j));
				if (cell != null)
					assertEquals(cell.getCurrentValue(), Cell.EMPTYVAL);
			}
		}
	}

	@Test
	public void testBuilderWithSolutions() {
		SudokuBuilder sb = new SudokuBuilder(SudokuTypes.standard9x9, str);
		sb.addSolution(Position.get(0, 0), 5);
		sb.setFixed(Position.get(0, 0));
		sb.addSolution(Position.get(0, 1), 3);
		Sudoku s = sb.createSudoku();

		assertEquals(s.getCell(Position.get(0, 0)).getSolution(), 5);
		assertEquals(s.getCell(Position.get(0, 0)).getCurrentValue(), 5);
		assertEquals(s.getCell(Position.get(0, 1)).getSolution(), 3);
		assertEquals(s.getCell(Position.get(0, 1)).getCurrentValue(), Cell.EMPTYVAL);

		try {
			sb.addSolution(Position.get(1, 3), -5);
			fail("no exception");
		} catch (IllegalArgumentException e) {
			// great
		}
		try {
			sb.addSolution(Position.get(1, 3), 9);
			fail("no exception");
		} catch (IllegalArgumentException e) {
			// great
		}
	}

}