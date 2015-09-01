package de.sudoq.model.actionTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.SolveAction;
import de.sudoq.model.sudoku.Field;

public class ActionTreeElementTests {

	@Test
	public void testConstruction() {
		Action action = new SolveAction(1, new Field(-1, 1));

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);
		ActionTreeElement ate2 = new ActionTreeElement(2, action, ate1);
		for (ActionTreeElement actionTreeElement : ate1) {
			assertEquals(actionTreeElement, ate2);
		}

		assertEquals(ate1, ate2.getParent());
		assertTrue(ate1.getChildrenList().contains(ate2));
		assertEquals(ate2, ate1.getChildren().next());
		ate2.addChild(null);
		assertEquals(0, ate2.getChildrenList().size());
		assertFalse(ate1.isCorrect());
		assertFalse(ate1.isMistake());
	}

	@Test
	public void testFailConstruction() {
		try {
			new ActionTreeElement(1, null, null);
			fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testEquals() {
		Action action = new SolveAction(1, new Field(-1, 1));
		ActionTreeElement ate = new ActionTreeElement(1, action, null);
		ActionTreeElement ate1 = new ActionTreeElement(2, action, null);
		assertFalse(ate.equals(new Object()));
		assertFalse(ate.equals(ate1));
		ate1 = new ActionTreeElement(1, action, null);
		assertTrue(ate.equals(ate1));
		ate1.mark();
		assertFalse(ate.equals(ate1));
		ate1 = new ActionTreeElement(1, new SolveAction(1, new Field(0, 1)), null);
		assertFalse(ate.equals(ate1));

	}

	@Test
	public void testIsSplitUp() {
		Action action = new SolveAction(1, new Field(-1, 1));

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);
		assertFalse(ate1.isSplitUp());
		new ActionTreeElement(2, action, ate1);
		assertFalse(ate1.isSplitUp());
		new ActionTreeElement(3, action, ate1);
		assertTrue(ate1.isSplitUp());
	}

	@Test
	public void testActionExecution() {
		Field f = new Field(true, 3, -1, 9);
		Action action = new SolveAction(1 - Field.EMPTYVAL, f);

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);

		int value = f.getCurrentValue();
		ate1.execute();
		assertEquals(f.getCurrentValue(), 1);
		ate1.undo();
		assertEquals(f.getCurrentValue(), value);
	}

	@Test
	public void testToXml() {
		Action action = new SolveAction(1, new Field(1, 9));
		ActionTreeElement ate = new ActionTreeElement(1, action, null);
		assertTrue(ate.toXml().getAttributeValue(ActionTreeElement.PARENT).equals(""));
		assertTrue(new ActionTreeElement(2, action, ate).toXml().getAttributeValue(ActionTreeElement.PARENT)
				.equals("1"));
		ate.markCorrect();
		ate.markWrong();
		assertTrue(ate.toXml().getAttributeValue(ActionTreeElement.MISTAKE).equals("true"));
		assertTrue(ate.toXml().getAttributeValue(ActionTreeElement.CORRECT).equals("true"));
		action = new SolveAction(1, new Field(-1, 9));
		ate = new ActionTreeElement(1, action, null);
		assertEquals(ate.toXml(), null);
	}

	@Test
	public void testCompare() {
		ActionTreeElement a1 = new ActionTreeElement(1, new SolveAction(1, new Field(1, 9)), null);
		ActionTreeElement a2 = new ActionTreeElement(3, new SolveAction(1, new Field(1, 9)), null);
		ActionTreeElement a3 = new ActionTreeElement(1, new SolveAction(1, new Field(1, 9)), null);
		assertTrue(a1.compareTo(a2) == -2);
		assertTrue(a2.compareTo(a1) == 2);
		assertTrue(a1.compareTo(a3) == 0);
		assertTrue(a3.compareTo(a1) == 0);
		assertTrue(a2.compareTo(a2) == 0);
	}

	@Test
	public void testMark() {
		ActionTreeElement a1 = new ActionTreeElement(1, new SolveAction(1, new Field(1, 9)), null);
		assertFalse(a1.isMarked());
		a1.mark();
		assertTrue(a1.isMarked());
	}
}
