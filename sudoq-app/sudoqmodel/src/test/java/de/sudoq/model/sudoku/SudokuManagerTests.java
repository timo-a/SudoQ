package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SudokuManagerTests {

	private static File profiles = new File("res/tmp_profiles");
	private static File sudokus = new File("res/tmp_sudokus");

	@BeforeClass
	public static void init() throws IOException {
		Utility.copySudokus();
		profiles = Utility.profiles;
		sudokus  = Utility.sudokus;
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
		f.setAccessible(true);
		f.set(null, null);
		java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
		s.setAccessible(true);
		s.set(null, null);
		java.lang.reflect.Field p = Profile.class.getDeclaredField("instance");
		p.setAccessible(true);
		p.set(null, null);
		FileManager.deleteDir(profiles);
		FileManager.deleteDir(sudokus);
	}


	@Test
	public void test() {
		assertEquals(10, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
		Sudoku s = SudokuManager.getNewSudoku(SudokuTypes.standard9x9, Complexity.infernal);
		for (int i = 0; i < 10; i++) {
			s.increaseTransformCount();
		}
		SudokuManager sm = new SudokuManager() {
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
		assertEquals(10, FileManager.getSudokuCountOf(SudokuTypes.standard9x9, Complexity.infernal));
	}

}
