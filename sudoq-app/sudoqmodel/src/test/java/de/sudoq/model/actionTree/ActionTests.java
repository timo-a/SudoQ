package de.sudoq.model.actionTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.sudoku.Cell;

public class ActionTests {

	@Test
	public void testNoteActionExecution() {
		Cell cell = new Cell(-1, 1);
		ActionFactory factory = new NoteActionFactory();
		Action action = factory.createAction(5, cell);
		assertFalse(cell.isNoteSet(5));

		action.execute();
		assertTrue(cell.isNoteSet(5));

		action.undo();
		assertFalse(cell.isNoteSet(5));
	}

	@Test
	public void testSolveActionExecution() {
		Cell cell = new Cell(-1, 9);
		ActionFactory factory = new SolveActionFactory();
		Action action = factory.createAction(5, cell);
		int value = cell.getCurrentValue();
		assertFalse(value == 5);

		action.execute();
		assertTrue(cell.getCurrentValue() == 5);

		action.undo();
		assertTrue(cell.getCurrentValue() == value);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCellInstantiationForSolveAction() {
		ActionFactory factory = new SolveActionFactory();
		factory.createAction(5, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCellInstantiationForNoteAction() {
		ActionFactory factory = new NoteActionFactory();
		factory.createAction(5, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCellInstantiationForSolveActionWithoutFactory() {
		new SolveAction(5, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCellInstantiationForNoteActionWithoutFactory() {
		new NoteAction(5, null);
	}

	@Test
	public void testEquals() {
		Cell f = new Cell(1, 9);
		assertFalse(new SolveAction(1, f).equals(new NoteAction(1, f)));
		assertTrue(new SolveAction(1, f).equals(new SolveAction(1, f)));
		assertFalse(new SolveAction(2, f).equals(new SolveAction(1, f)));
		assertFalse(new SolveAction(1, f).equals(new SolveAction(1, new Cell(2, 9))));
	}

	@Test
	public void testGetId() {
		Cell f = new Cell(1, 9);
		Action a = new SolveAction(1, f);
		assertEquals(a.getCellId(), 1);
	}
}
