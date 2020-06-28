package de.sudoq.model.actionTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import de.sudoq.model.actionTree.ActionFactory;
import de.sudoq.model.actionTree.ActionTree;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Field;

public class ActionTreeIteratorTests {

	@Test
	public void testCompleteness() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		Action a2 = new SolveAction(1, field); // -> value 1
		Action a3 = new SolveAction(0, field); // -> value 1
		Action a4 = new SolveAction(0, field); // -> value 1
		Action a5 = new SolveAction(1, field); // -> value 2

		ActionTreeElement ate2 = at.add(a2, at.getRoot()); //one child
		at.add(a3, ate2);//this should be ignored by the actionTree, cause redundant to parent TODO no ot shouldnd, only in gamestatehandler scenario. make that scenario!!!
		at.add(a4, ate2);//this should be ignored by the actionTree, cause we've been there already
		at.add(a5, ate2);

		int i = 0;
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (ActionTreeElement ate : at) {
			assertFalse(ids.contains(ate.getId()));
			ids.add(ate.getId());
			i++;
		}

		assertEquals(5, i);
	}
	@Test
	public void testCompleteness2() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		Action a2 = factory.createAction(1, field);//actions are created by factory in relation to current field
		Action a3 = factory.createAction(3, field);//creating them as needed makes it hard to see what is what
		Action a4 = factory.createAction(3, field);
		Action a5 = factory.createAction(2, field);

		ActionTreeElement ate2 = at.add(a2, at.getRoot()); //one child
		ActionTreeElement ate3 = at.add(a3, ate2);
		ActionTreeElement ate4 = at.add(a4, ate2);
		ActionTreeElement ate5 = at.add(a5, ate2);//this should be ignored by the actionTree, cause we've been there already

		int i = 0;
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (ActionTreeElement ate : at) {
			assertFalse(ids.contains(ate.getId()));
			ids.add(ate.getId());
			i++;
		}

		assertEquals(5, i);
	}

	@Test
	public void testExceptions() {
		ActionTree at = new ActionTree();//has automatic root element
		Iterator<ActionTreeElement> iterator = at.iterator();

		try {
			iterator.next();
			iterator.next();
			fail("No Exception thrown");
		} catch (NoSuchElementException e) {
		}
		try {
			iterator.remove();
			fail("No Exception thrown");
		} catch (UnsupportedOperationException e) {
		}
	}

}
