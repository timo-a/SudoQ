package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SudokuTestsJava {
	private static Sudoku sudoku;

	//this is a dummy so it compiles todo use xmls from resources
	//private static IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo();

	@BeforeClass
	public static void beforeClass() {

        //todo use mock
		/*new Generator(sudokuTypeRepo).generate(SudokuTypes.standard4x4, Complexity.easy, new GeneratorCallback() {
			@Override
			public void generationFinished(Sudoku sudoku) {
				SudokuTests.sudoku = sudoku;
			}

			@Override
			public void generationFinished(Sudoku sudoku, List<Solution> sl) {
				SudokuTests.sudoku = sudoku;
			}
		});*/
	}


/*	@Test todo use mock
	public void testCellChangeNotification() {
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku();
		Listener listener = new Listener();

		sudoku.getCell(Position.get(0, 0)).setCurrentValue(2);
		assertEquals(listener.callCount, 0);

		sudoku.registerListener(listener);
		sudoku.getCell(Position.get(3, 2)).setCurrentValue(5);
		assertEquals(listener.callCount, 1);
	}*/

	class Listener implements ModelChangeListener<Cell> {
		int callCount = 0;

		@Override
		public void onModelChanged(Cell obj) {
			callCount++;
		}

	}







	@Test
	public void testCellModification() {
		Sudoku s = new Sudoku(TypeBuilder.get99());
		Cell f = new Cell(1000, 9);
		s.setCell(f, Position.get(4, 4));
		assertTrue(f.equals(s.getCell(Position.get(4, 4))));
		assertEquals(s.getPosition(f.getId()), Position.get(4, 4));
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

	@Test
	public synchronized void testToString() {

		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard4x4);
		Sudoku sudoku = new Sudoku(sudokuType);
		sudoku.getCell(Position.get(1,1)).setCurrentValue(3);
		sudoku.cells.remove(Position.get(1,2));
		assertEquals("x x x x\n"
		            +"x 3 x x\n"
		            +"x   x x\n"
		            +"x x x x",sudoku.toString());



		sudokuType = TypeBuilder.getType(SudokuTypes.standard16x16);
		sudoku = new Sudoku(sudokuType);
		sudoku.getCell(Position.get(1,1)).setCurrentValue(12);
		assertEquals("xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx 12 xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx\n"
		            +"xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx",sudoku.toString());
	}
}
