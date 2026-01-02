package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SudokuTests {

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


	@Test
	public void testInitializeStandardSudoku() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		Sudoku sudoku = new Sudoku(sudokuType);

        assertSame("Sudokutype isn't the same", sudoku.getSudokuType(), sudokuType);
		assertFalse("Sudoku finished on initialization", sudoku.isFinished());

        for (Position pos : sudokuType.getValidPositions()) {
            assertNotNull("some field initialized as null", sudoku.getCell(pos));
        }

		for (Cell f : sudoku) {
            assertNotNull("some field initialized as null (iterator)", f);
		}

        assertEquals(0, sudoku.getTransformCount());
        assertEquals(0, sudoku.getId());
		sudoku.increaseTransformCount();
        assertEquals(1, sudoku.getTransformCount());
	}

	@Test
	public void testInitializeWithoutSolutions() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		Sudoku sudoku = new Sudoku(sudokuType, null, null);

        assertSame("Sudokutype isn't the same", sudoku.getSudokuType(), sudokuType);
		assertFalse("Sudoku finished on initialization", sudoku.isFinished());

        for (Position pos : sudokuType.getValidPositions()) {
            assertNotNull("some field initialized as null", sudoku.getCell(pos));
		}

		for (Cell f : sudoku) {
            assertNotNull("some field initialized as null (iterator)", f);
		}
	}

	@Test
	public void testInitializeWithoutSetValues() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		PositionMap<Integer> solutions = new PositionMap<>(Position.get(9, 9));
        for (Position pos : sudokuType.getValidPositions()) {
            solutions.put(pos, 0);
		}
		Sudoku sudoku = new Sudoku(sudokuType, solutions, null);

		assertTrue("Sudokutype isn't the same", sudoku.getSudokuType() == sudokuType);
		assertFalse("Sudoku finished on initialization", sudoku.isFinished());

		for (Position pos : sudokuType.getValidPositions()) {
             assertNotNull("some field initialized as null", sudoku.getCell(pos));
		}

		for (Cell f : sudoku) {
            assertNotNull("some field initialized as null (iterator)", f);
		}
	}

	@Test
	public void testGetCell() {
		Sudoku sudoku = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9));

		assertNotNull(sudoku);

		assertNull(sudoku.getCell(Position.get(9, 10)));
		Cell f = sudoku.getCell(Position.get(1, 2));

		f.setCurrentValue(6);

		assertEquals(6, sudoku.getCell(Position.get(1, 2)).getCurrentValue());
	}

	@Test
	public void testComplexity() {
		Sudoku sudoku = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9));

		assertNull(sudoku.getComplexity());
		sudoku.setComplexity(Complexity.easy);
		assertSame(sudoku.getComplexity(), Complexity.easy);
		assertNotNull(sudoku.getComplexity());

	}

	@Test
	public void testIterator() {
		Sudoku su = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9));

		su.getCell(Position.get(0, 0)).setCurrentValue(5);
		su.getCell(Position.get(1, 4)).setCurrentValue(4);

		Iterator<Cell> i = su.iterator();

		boolean aThere = false;
		boolean bThere = false;

		Cell f;

		while (i.hasNext()) {
			f = i.next();
			if (f.getCurrentValue() == 5 && !aThere)
				aThere = true;
			if (f.getCurrentValue() == 4 && !bThere)
				bThere = true;

		}
	}

	@Test
	public void testInitializeSudokuWithValues() {
        SudokuType s99 = TypeBuilder.getType(SudokuTypes.standard9x9);
		PositionMap<Integer> map = new PositionMap<>(s99.getSize());
		PositionMap<Boolean> setValues = new PositionMap<>(s99.getSize());
		for (Position pos : s99.getValidPositions()) {
            map.put(pos, pos.getX() + 1);
            if (pos.getX() != pos.getY()) {
                setValues.put(pos, true);
			}
		}

        Sudoku sudoku = new Sudoku(s99, map, setValues);

		Cell cell;
		for (Position pos : s99.getValidPositions()) {
            cell = sudoku.getCell(pos);
            if (pos.getX() == pos.getY()) {
                assertTrue("wrong field initialization or field null", cell.isEditable());
            } else {
                assertFalse("wrong field initialization or field null", cell.isEditable());
            }
		}
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
	public void testNotEquals() {
		Sudoku s1 = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9  ));
		Sudoku s2 = new Sudoku(TypeBuilder.getType(SudokuTypes.standard16x16));
		assertNotEquals(s1, s2);
		assertNotEquals(null, s1);
		assertNotEquals(s1, 0);
		s2 = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9) );
		s1.setComplexity(Complexity.easy);
		s2.setComplexity(Complexity.medium);
		assertNotEquals(s1, s2);
		s2 = new Sudoku(TypeBuilder.getType(SudokuTypes.samurai));
		s2.setComplexity(Complexity.easy);
		assertNotEquals(s2, s1);
	}

	@Test
	public void testHasErrors() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		PositionMap<Integer> solutions = new PositionMap<>(Position.get(9, 9));
		for (Position pos : sudokuType.getValidPositions()) {
            solutions.put(pos, 0);
		}
		Sudoku sudoku = new Sudoku(sudokuType, solutions, null);
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(1);
		assertTrue(sudoku.hasErrors());
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
