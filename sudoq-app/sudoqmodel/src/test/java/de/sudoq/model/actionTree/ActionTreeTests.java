package de.sudoq.model.actionTree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.sudoq.model.sudoku.Field;

public class ActionTreeTests {

	@Test
	public void testConstruction() {
		ActionTree at = new ActionTree();
		assertNotNull(at.getRoot());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddingElementsMountOnNull() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		ActionTreeElement ate = at.getRoot();

		at.add(factory.createAction(2, field), null);
	}

	@Test
	public void testAddingElements() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		ActionTreeElement ate = at.getRoot();//add(factory.createAction(1, field), null);
		assertEquals(ate.getId(), 1);

		assertEquals(at.add(factory.createAction(1, field), ate).getId(), 2);

		assertEquals(ate.getChildrenList().size(), 1);
	}

	@Test
	public void testEquals() {
		assertNotEquals(new ActionTree(), new Object());

		ActionTree at = new ActionTree();
		ActionTree at2 = new ActionTree();
		at2.add(new SolveActionFactory().createAction(5, new Field(-1, 1)), at2.getRoot());
		assertNotEquals(at, at2);
		assertEquals(at, at);
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
	public void testFindPathBetweenDifferentTrees() {
		ActionTree at1 = new ActionTree();
		ActionTree at2 = new ActionTree();

		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		//at1: r -> n1 -> (n2, n3 -> n4)
		ActionTreeElement aRoot = at1.getRoot();
		ActionTreeElement a1 = at1.add(factory.createAction(4, field), aRoot);
		ActionTreeElement a2 = at1.add(factory.createAction(5, field), a1);
		ActionTreeElement a3 = at1.add(factory.createAction(6, field), a1);
		ActionTreeElement a4 = at1.add(factory.createAction(7, field), a3);

		ActionTreeElement bRoot = at2.getRoot();
		ActionTreeElement b1 = at2.add(factory.createAction(0, field), bRoot);
		ActionTreeElement b2 = at2.add(factory.createAction(1, field), b1);
		ActionTreeElement b3 = at2.add(factory.createAction(2, field), b1);
		ActionTreeElement b4 = at2.add(factory.createAction(3, field), b3);

		List<ActionTreeElement> path = ActionTree.findPath(a2, b4);
		assertNull(path);
	}

	@Test
	public void testFindNonExistingPath() {
		ActionTree a = new ActionTree();
		ActionTree b = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Field field = new Field(-1, 1);

		//at1 -> at2

		ActionTreeElement aRoot = a.getRoot();
		ActionTreeElement a1 = a.add(factory.createAction(4, field), aRoot);

		ActionTreeElement bRoot = b.getRoot();
		ActionTreeElement b1 = b.add(factory.createAction(2, field), bRoot);

		List<ActionTreeElement> path = ActionTree.findPath(a1, b1);
		assertTrue(path.isEmpty());//return empty list because elements have same id

	}
}
