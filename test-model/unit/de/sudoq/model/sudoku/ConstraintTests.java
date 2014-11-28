package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SumConstraintBehavior;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;
import de.sudoq.model.sudoku.sudokuTypes.StandardSudokuType9x9;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class ConstraintTests {

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
		c.addPosition(null);
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

		sudo.getField(posA).setCurrentValue(0);
		sudo.getField(posB).setCurrentValue(4);
		sudo.getField(posC).setCurrentValue(4);

		assertTrue(c.isSaturated(sudo));

		c.addPosition(posA);
		assertTrue(c.isSaturated(sudo));

		c.addPosition(posB);
		assertTrue(c.isSaturated(sudo));
		c.addPosition(posC);
		assertFalse(c.isSaturated(sudo));

		assertFalse(c.isSaturated(null));

	}

	@Test
	public void testIllegalArguments() {
		try {
			new Constraint(new UniqueConstraintBehavior(), null, "Test");
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
		}

		try {
			new Constraint(null, ConstraintType.BLOCK, "Test");
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
		}

		try {
			new Constraint(new UniqueConstraintBehavior(), null);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
		}

		try {
			new Constraint(null, ConstraintType.BLOCK);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
		}

	}
}
