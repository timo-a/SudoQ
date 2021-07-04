package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class ConstraintTests {

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
	public void initialisation() {
		UniqueConstraintBehavior uc = new UniqueConstraintBehavior();
		Constraint c = new Constraint(uc, ConstraintType.LINE, null);
		assertTrue(c.hasUniqueBehavior());
		assertEquals(c.toString(), "Constraint with " + uc);
		Constraint c1 = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
		assertTrue(c1.getType().equals(ConstraintType.LINE));
		c = new Constraint(new SumConstraintBehavior(9), ConstraintType.LINE, null);
		assertFalse(c.hasUniqueBehavior());
	}

	@Test
	public void testAddPositionAndIterate() {
		Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);

		Position p1 = Position.get(1, 1);
		Position p2 = Position.get(2, 2);
		Position p3 = Position.get(42, 42);

		c.addPosition(p1);
		c.addPosition(p2);
		c.addPosition(p3);
		c.addPosition(p1);
		assertTrue(c.getSize() == 3);
		assertTrue(c.includes(p1));
		assertTrue(c.includes(p2));
		assertTrue(c.includes(p3));

		Iterator<Position> i = c.iterator();
		Position p = i.next();
		assertTrue(p.equals(p1));
		p = i.next();
		assertTrue(p.equals(p2));
		p = i.next();
		assertTrue(p.equals(p3));
	}

	@Test
	public void testSaturation() {
		Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);

		Position posA = Position.get(0, 0);
		Position posB = Position.get(0, 1);
		Position posC = Position.get(0, 2);

		PositionMap<Integer> map = new PositionMap<Integer>(Position.get(9, 9));

		for (int i = 0; i < 81; i++) {
			map.put(Position.get(i / 9, i % 9), 0);
		}
		
		SudokuType s99 = TypeBuilder.getType(SudokuTypes.standard9x9);
		
		Sudoku sudo = new Sudoku(s99, map, new PositionMap<Boolean>(Position.get(9, 9)));

		sudo.getCell(posA).setCurrentValue(0);
		sudo.getCell(posB).setCurrentValue(4);
		sudo.getCell(posC).setCurrentValue(4);

		assertTrue(c.isSaturated(sudo));

		c.addPosition(posA);
		assertTrue(c.isSaturated(sudo));

		c.addPosition(posB);
		assertTrue(c.isSaturated(sudo));
		c.addPosition(posC);
		assertFalse(c.isSaturated(sudo));
	}

}
