package de.sudoq.model.actionTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.ActionFactory;
import de.sudoq.model.actionTree.NoteAction;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.actionTree.SolveAction;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Field;

public class ActionTests {

	@Test
	public void testNoteActionExecution() {
		Field field = new Field(-1, 1);
		ActionFactory factory = new NoteActionFactory();
		Action action = factory.createAction(5, field);
		assertFalse(field.isNoteSet(5));

		action.execute();
		assertTrue(field.isNoteSet(5));

		action.undo();
		assertFalse(field.isNoteSet(5));
	}

	@Test
	public void testSolveActionExecution() {
		Field field = new Field(-1, 9);
		ActionFactory factory = new SolveActionFactory();
		Action action = factory.createAction(5, field);
		int value = field.getCurrentValue();
		assertFalse(value == 5);

		action.execute();
		assertTrue(field.getCurrentValue() == 5);

		action.undo();
		assertTrue(field.getCurrentValue() == value);
	}

	@Test
	public void testNullFieldInstanciation() {
		try {
			ActionFactory factory = new SolveActionFactory();
			factory.createAction(5, null);
			fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
		}

		try {
			ActionFactory factory = new NoteActionFactory();
			factory.createAction(5, null);
			fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
		}

		try {
			new NoteAction(5, null);
			fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testEquals() {
		Field f = new Field(1, 9);
		assertFalse(new SolveAction(1, f).equals(new NoteAction(1, f)));
		assertTrue(new SolveAction(1, f).equals(new SolveAction(1, f)));
		assertFalse(new SolveAction(2, f).equals(new SolveAction(1, f)));
		assertFalse(new SolveAction(1, f).equals(new SolveAction(1, new Field(2, 9))));
	}

	@Test
	public void testGetId() {
		Field f = new Field(1, 9);
		Action a = new SolveAction(1, f);
		assertEquals(a.getFieldId(), 1);
	}
}
