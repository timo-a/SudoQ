package de.sudoq.model.actionTree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import de.sudoq.model.actionTree.ActionFactory;
import de.sudoq.model.actionTree.ActionTree;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Field;

public class ActionTreeTests {

	@Test
	public void testConstruction() {
		ActionTree at = new ActionTree();
		assertNotNull(at.getRoot());
	}

	@Test
	public void testAddingElements() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		ActionTreeElement ate = at.getRoot();//add(factory.createAction(1, field), null);
		assertEquals(ate.getId(), 1);

		try {
			at.add(factory.createAction(2, field), null);
			fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
		}

		assertEquals(at.add(factory.createAction(1, field), ate).getId(), 2);

		assertEquals(ate.getChildrenList().size(), 1);
	}

	@Test
	public void testEquals() {
		assertFalse(new ActionTree().equals(new Object()));

		ActionTree at = new ActionTree();
		ActionTree at2 = new ActionTree();
		at2.add(new SolveActionFactory().createAction(5, new Field(-1, 1)), at2.getRoot());
		assertFalse(at.equals(at2));
		assertTrue(at.equals(at));
	}

	@Test
	public void testGettingElementsById() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);
		Field field2 = new Field(0, 2);
		Field field3 = new Field(1, 3);
		Field field4 = new Field(2, 4);

		ActionTreeElement ate1 = at.getRoot();
		ActionTreeElement ate2 = at.add(factory.createAction(1, field2), ate1);
		ActionTreeElement ate3 = at.add(factory.createAction(1, field3), ate2);
		ActionTreeElement ate4 = at.add(factory.createAction(2, field4), ate2);

		assertEquals(ate1, at.getElement(1));
		assertEquals(ate2, at.getElement(2));
		assertEquals(ate3, at.getElement(3));
		assertEquals(ate4, at.getElement(4));

		assertTrue(ate2.isSplitUp());
	}

	@Test
	public void testSearchForInexistentId() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		ActionTreeElement ate1 = at.getRoot();
		ActionTreeElement ate2 = at.add(factory.createAction(1, field), ate1);
		at.add(factory.createAction(1, field), ate2);
		at.add(factory.createAction(1, field), ate2);

		assertNull(at.getElement(10));
		assertNull(at.getElement(0));
		assertNull(at.getElement(-2));
	}

	@Test
	public void testConsistencyCheck() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		ActionTreeElement ate1 = at.getRoot();
		ActionTreeElement ate2 = at.add(factory.createAction(1, field), ate1);
		ActionTreeElement ate3 = at.add(factory.createAction(1, field), ate2);
		at.add(factory.createAction(1, field), ate2);
		assertTrue(at.isConsistent());

		ate3.addChild(ate1);
		assertFalse(at.isConsistent());
	}

	// AT170
	@Test
	public void testFindPath() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field0 = new Field( 4, 0);
		Field field1 = new Field( 0, 2);
		Field field2 = new Field( 1, 3);
		Field field3 = new Field( 2, 4);
		Field field4 = new Field( 3, 5);

		ActionTreeElement ate1 = at.getRoot();//at.add(factory.createAction(1, field), null);
		ActionTreeElement ate2 =    at.add(factory.createAction(1, field0), ate1);
		ActionTreeElement ate3 =       at.add(factory.createAction(3, field1), ate2);
		ActionTreeElement ate4 =          at.add(factory.createAction(1, field2), ate3);
		ActionTreeElement ate5 =       at.add(factory.createAction(2, field3), ate2);
		ActionTreeElement ate6 =          at.add(factory.createAction(1, field4), ate5);

		assertArrayEquals(new ActionTreeElement[] { ate4, ate3, ate2, ate5, ate6 }, ActionTree.findPath(ate4, ate6)
				.toArray());

		assertArrayEquals(new ActionTreeElement[] { ate6, ate5, ate2 }, ActionTree.findPath(ate6, ate2).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate2, ate5, ate6 }, ActionTree.findPath(ate2, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate6, ate5, ate2, ate1 }, ActionTree.findPath(ate6, ate1).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate1, ate2, ate5, ate6 }, ActionTree.findPath(ate1, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] {}, ActionTree.findPath(ate6, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] {}, ActionTree.findPath(ate1, ate1).toArray());
	}

	@Test
	public void testFindNonExistingPath() {
		ActionTree at1 = new ActionTree();
		ActionTree at2 = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		ActionTreeElement ate1 = at1.getRoot();
		ActionTreeElement ate2 = at1.add(factory.createAction(4, field), ate1);

		ActionTreeElement ate3 = at2.getRoot();
		ActionTreeElement ate4 = at2.add(factory.createAction(2, field), ate3);

		List<ActionTreeElement> a = ActionTree.findPath(ate2, ate4);
		assertNull(a);
		/*List<ActionTreeElement> b = ActionTree.findPath(at1.getRoot()
				                                       ,at2.getRoot());
		assertNull(b); TODO vertagt, gibt bislang [] zurück. vielleicht sollte nie null zurückgegeben werden sondern immer [] wenns hakt*/

	}
}
