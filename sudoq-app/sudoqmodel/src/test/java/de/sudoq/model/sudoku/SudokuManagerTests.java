package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SudokuManagerTests extends TestWithInitCleanforSingletons {
	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	@Test(timeout = 120) // threw an exception and ran forever in the past -> timeout
	public void test() {
		assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
		Sudoku s = SudokuManager.getNewSudoku(SudokuTypes.standard9x9, Complexity.infernal, sudokuDir);
		for (int i = 0; i < 10; i++) {
			s.increaseTransformCount();
		}
		SudokuManager sm = new SudokuManager(sudokuDir) {
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
		assertEquals(21, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
	}

}
