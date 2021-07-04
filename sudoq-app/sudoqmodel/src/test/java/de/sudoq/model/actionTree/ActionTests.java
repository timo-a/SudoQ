package de.sudoq.model.actionTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.sudoku.Cell;

public class ActionTests {

	//in kotlin no longer needed since covered by type system
	@Test(expected = NullPointerException.class)
	public void testNullCellInstantiationForSolveAction() {
		ActionFactory factory = new SolveActionFactory();
		factory.createAction(5, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullCellInstantiationForNoteAction() {
		ActionFactory factory = new NoteActionFactory();
		factory.createAction(5, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullCellInstantiationForSolveActionWithoutFactory() {
		new SolveAction(5, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullCellInstantiationForNoteActionWithoutFactory() {
		new NoteAction(5, null);
	}

	@Test
	public void testGetId() {
		Cell f = new Cell(1, 9);
		Action a = new SolveAction(1, f);
		assertEquals(a.getCellId(), 1);
	}
}
