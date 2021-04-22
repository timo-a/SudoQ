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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;
import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;

public class SudokuTests {
	private static Sudoku sudoku;

    private static File profiles;
	private static File sudokus ;

	@BeforeClass
	public static void beforeClass() {
        Utility.copySudokus();
        sudokus  = Utility.sudokus;
        profiles = Utility.profiles;

		new Generator().generate(SudokuTypes.standard4x4, Complexity.easy, new GeneratorCallback() {
			@Override
			public void generationFinished(Sudoku sudoku) {
				SudokuTests.sudoku = sudoku;
			}

			@Override
			public void generationFinished(Sudoku sudoku, List<Solution> sl) {
				SudokuTests.sudoku = sudoku;
			}
		});
	}

    @Test(expected = IllegalArgumentException.class)
    public void testNull_0() {
        sudoku = new Sudoku(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull_1() {
        sudoku = new Sudoku(null, null, null);
    }

	@Test
	public void testInitializeStandardSudoku() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		Sudoku sudoku = new Sudoku(sudokuType);

		assertTrue("Sudokutype isn't the same", sudoku.getSudokuType() == sudokuType);
		assertFalse("Sudoku finished on initialization", sudoku.isFinished());

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				assertTrue("some field initialized as null", sudoku.getCell(Position.get(x, y)) != null);
			}
		}

		for (Cell f : sudoku) {
			assertTrue("some field initialized as null (iterator)", f != null);
		}

		assertTrue(sudoku.getTransformCount() == 0);
		assertTrue(sudoku.getId() == 0);
		sudoku.increaseTransformCount();
		assertTrue(sudoku.getTransformCount() == 1);
	}

	@Test
	public void testInitializeWithoutSolutions() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		Sudoku sudoku = new Sudoku(sudokuType, null, null);

		assertTrue("Sudokutype isn't the same", sudoku.getSudokuType() == sudokuType);
		assertFalse("Sudoku finished on initialization", sudoku.isFinished());

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				assertTrue("some field initialized as null", sudoku.getCell(Position.get(x, y)) != null);
			}
		}

		for (Cell f : sudoku) {
			assertTrue("some field initialized as null (iterator)", f != null);
		}
	}

	@Test
	public void testInitializeWithoutSetValues() {
		SudokuType sudokuType = TypeBuilder.getType(SudokuTypes.standard9x9);
		PositionMap<Integer> solutions = new PositionMap<Integer>(Position.get(9, 9));
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				solutions.put(Position.get(x, y), new Integer(0));
			}
		}
		Sudoku sudoku = new Sudoku(sudokuType, solutions, null);

		assertTrue("Sudokutype isn't the same", sudoku.getSudokuType() == sudokuType);
		assertFalse("Sudoku finished on initialization", sudoku.isFinished());

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				assertTrue("some field initialized as null", sudoku.getCell(Position.get(x, y)) != null);
			}
		}

		for (Cell f : sudoku) {
			assertTrue("some field initialized as null (iterator)", f != null);
		}
	}

	@Test
	public void testGetCell() {
		Sudoku sudoku = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9));

		assertNotNull(sudoku);

		assertNull(sudoku.getCell(null));

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
		PositionMap<Integer> map = new PositionMap<Integer>(Position.get(9, 9));
		PositionMap<Boolean> setValues = new PositionMap<Boolean>(Position.get(9, 9));
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				map.put(Position.get(x, y), x + 1);
				if (x != y) {
					setValues.put(Position.get(x, y), true);
				}
			}
		}

		Sudoku sudoku = new Sudoku(TypeBuilder.getType(SudokuTypes.standard9x9), map, setValues);

		Cell cell;
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				if (x == y) {
					cell = sudoku.getCell(Position.get(x, y));
					assertTrue("wrong field initialization or field null", cell.isEditable());
				} else {
					cell = sudoku.getCell(Position.get(x, y));
					assertFalse("wrong field initialization or field null", cell.isEditable());
				}
			}
		}
	}

	@Test
	public void testCellChangeNotification() {
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
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
	public void testToXml() {
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
		sudoku.setId(4357);
		sudoku.setComplexity(Complexity.easy);

		XmlTree tree = sudoku.toXmlTree();
		assertTrue(tree.getNumberOfChildren() > 0);

		assertEquals(tree.getAttributeValue("id"), "4357");
		assertEquals(tree.getAttributeValue("type"), "" + SudokuTypes.standard9x9.ordinal());
		assertEquals(tree.getAttributeValue("complexity"), "" + Complexity.easy.ordinal());
		assertEquals(tree.getNumberOfChildren(), 81);

		// System.out.println(new XmlHelper().buildXmlStructure(tree));
	}

	@Test
	public void testFillFromXml() throws IllegalArgumentException, IOException {
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
		sudoku.setId(6374);

		XmlTree tree = sudoku.toXmlTree();
		System.out.println(new XmlHelper().buildXmlStructure(tree));
		Sudoku rebuilt = new Sudoku();
		rebuilt.fillFromXml(tree);

		assertEquals(sudoku, rebuilt);
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
		PositionMap<Integer> solutions = new PositionMap<Integer>(Position.get(9, 9));
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				solutions.put(Position.get(x, y), new Integer(0));
			}
		}
		Sudoku sudoku = new Sudoku(sudokuType, solutions, null);
		sudoku.getCell(Position.get(0, 0)).setCurrentValue(1);
		assertTrue(sudoku.hasErrors());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromXmlError() {
		Sudoku sudoku = new Sudoku(TypeBuilder.get99());
		XmlTree tree = sudoku.toXmlTree();
		for (Iterator<XmlTree> iterator = tree.getChildren(); iterator.hasNext();) {
			XmlTree sub = iterator.next();
			if (sub.getName().equals("fieldmap")) {
				sub.addChild(new XmlTree("Hallo"));
			}
			assertTrue(sub.getNumberOfChildren() == 2);
		}
		sudoku.fillFromXml(tree);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromXmlError2() {
		Sudoku sudoku = new Sudoku(TypeBuilder.get99());
		XmlTree tree = sudoku.toXmlTree();
		for (Iterator<XmlTree> iterator = tree.getChildren(); iterator.hasNext();) {
			XmlTree sub = iterator.next();
			Iterator<XmlTree> it = sub.getChildren();
			if (it.hasNext()) {
				it.next();
				it.remove();
			}
			sub.addChild(new XmlTree("Test"));
		}
		sudoku.fillFromXml(tree);
	}

	@Test
	public void testFromXmlAdditionalChild() {
		Sudoku sudoku = new Sudoku(TypeBuilder.get99());
		XmlTree tree = sudoku.toXmlTree();
		tree.addChild(new XmlTree("Test"));
		Sudoku s2 = new Sudoku(TypeBuilder.get99());
		s2.fillFromXml(tree);
		assertEquals(sudoku, s2);

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
