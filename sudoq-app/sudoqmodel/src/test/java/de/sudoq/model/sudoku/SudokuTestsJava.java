package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;
import de.sudoq.persistence.sudokuType.SudokuTypeRepo;

public class SudokuTestsJava {
	private static Sudoku sudoku;

	//uses null so it compiles todo point xmls from resources
	private static IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo(null);

	@BeforeClass
	public static void beforeClass() {

        //todo use mock
		new Generator(sudokuTypeRepo).generate(SudokuTypes.standard4x4, Complexity.easy, new GeneratorCallback() {
			@Override
			public void generationFinished(Sudoku sudoku) {
				SudokuTestsJava.sudoku = sudoku;
			}

			@Override
			public void generationFinished(Sudoku sudoku, List<Solution> sl) {
				SudokuTestsJava.sudoku = sudoku;
			}
		});
	}


	@Test //todo use mock
	public void testCellChangeNotification() {
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku();
		Listener listener = new Listener();

		sudoku.getCell(Position.get(0, 0)).setCurrentValue(2);
		assertEquals(listener.callCount, 0);

		sudoku.registerListener(listener);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(5);
		assertEquals(listener.callCount, 1);
	}

	class Listener implements ModelChangeListener<Cell> {
		int callCount = 0;

		@Override
		public void onModelChanged(Cell obj) {
			callCount++;
		}

	}


	@Test
	public synchronized void testFinishedAndErrors() {
		int counter = 0;
		while (sudoku == null && counter < 80) {
			try {
				wait(100);
				counter++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		assertFalse(sudoku==null);
		assertFalse(sudoku.hasErrors());
		assertFalse(sudoku.isFinished());
		for (Cell f : sudoku) {
			f.setCurrentValue(f.getSolution());
		}
		assertTrue(sudoku.isFinished());
	}

}
