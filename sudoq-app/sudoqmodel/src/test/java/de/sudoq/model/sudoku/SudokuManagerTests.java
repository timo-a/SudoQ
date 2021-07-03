package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SudokuManagerTests extends TestWithInitCleanforSingletons {
	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	//this is a dummy so it compiles todo use xmls from resources
	private IRepo<SudokuType> sudokuTypeRepo = new IRepo<SudokuType>() {
		@Override
		public void delete(int id) { throw new NotImplementedException(); }

		@Override
		public SudokuType update(SudokuType sudokuBE) { throw new NotImplementedException(); }

		@Override
		public SudokuType read(int id) {
			throw new NotImplementedException();
		}

		@Override
		public SudokuType create() { throw new NotImplementedException(); }

	};

	@Test(timeout = 120) // threw an exception and ran forever in the past -> timeout
	public void test() {
		//assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
		Sudoku s = new SudokuManager(sudokuDir, sudokuTypeRepo)
				.getNewSudoku(SudokuTypes.standard9x9, Complexity.infernal);
		for (int i = 0; i < 10; i++) {
			s.increaseTransformCount();
		}
		SudokuManager sm = new SudokuManager(sudokuDir, sudokuTypeRepo) {
			public void generationFinished(Sudoku sudoku) {
				synchronized (SudokuManagerTests.this) {
					super.generationFinished(sudoku);
					SudokuManagerTests.this.notifyAll();
				}
			}
		};
		sm.usedSudoku(s);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
	}

}
