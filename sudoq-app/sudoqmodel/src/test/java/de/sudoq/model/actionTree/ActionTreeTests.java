package de.sudoq.model.actionTree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import de.sudoq.model.sudoku.Cell;

public class ActionTreeTests {

	@Test
	public void testConstruction() {
		ActionTree at = new ActionTree();
		assertNotNull(at.getRoot());
	}

	@Test(expected = NullPointerException.class)
	public void testAddingElementsMountOnNull() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate = at.getRoot();

		at.add(factory.createAction(2, cell), null);
	}

	@Test
	public void testAddingElements() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate = at.getRoot();//add(factory.createAction(1, field), null);
		assertEquals(ate.getId(), 1);

		assertEquals(at.add(factory.createAction(1, cell), ate).getId(), 2);

		assertEquals(ate.getChildrenList().size(), 1);
	}

	@Test
	public void testEquals() {
		assertNotEquals(new ActionTree(), new Object());

		ActionTree at = new ActionTree();
		ActionTree at2 = new ActionTree();
		at2.add(new SolveActionFactory().createAction(5, new Cell(-1, 1)), at2.getRoot());
		assertFalse(at.equals(at2));
		assertTrue(at.equals(at));
	}

	@Test
	public void testGettingElementsById() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);
		Cell cell2 = new Cell(0, 2);
		Cell cell3 = new Cell(1, 3);
		Cell cell4 = new Cell(2, 4);

		ActionTreeElement ate1 = at.getRoot();
		ActionTreeElement ate2 = at.add(factory.createAction(1, cell2), ate1);
		ActionTreeElement ate3 = at.add(factory.createAction(1, cell3), ate2);
		ActionTreeElement ate4 = at.add(factory.createAction(2, cell4), ate2);

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
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate1 = at.getRoot();
		ActionTreeElement ate2 = at.add(factory.createAction(1, cell), ate1);
		at.add(factory.createAction(1, cell), ate2);
		at.add(factory.createAction(1, cell), ate2);

		assertNull(at.getElement(10));
		assertNull(at.getElement(0));
		assertNull(at.getElement(-2));
	}

	//@Test method no longer exists -> removed
	public void testConsistencyCheck() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate1 = at.getRoot();
		ActionTreeElement ate2 = at.add(factory.createAction(1, cell), ate1);
		ActionTreeElement ate3 = at.add(factory.createAction(1, cell), ate2);
		at.add(factory.createAction(1, cell), ate2);
		//assertTrue(at.isConsistent());

		ate3.addChild(ate1);
		//assertFalse(at.isConsistent());
	}

	// AT170
	@Test
	public void testFindPath() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell0 = new Cell( 4, 0);
		Cell cell1 = new Cell( 0, 2);
		Cell cell2 = new Cell( 1, 3);
		Cell cell3 = new Cell( 2, 4);
		Cell cell4 = new Cell( 3, 5);

		ActionTreeElement ate1 = at.getRoot();//at.add(factory.createAction(1, field), null);
		ActionTreeElement ate2 =    at.add(factory.createAction(1, cell0), ate1);
		ActionTreeElement ate3 =       at.add(factory.createAction(3, cell1), ate2);
		ActionTreeElement ate4 =          at.add(factory.createAction(1, cell2), ate3);
		ActionTreeElement ate5 =       at.add(factory.createAction(2, cell3), ate2);
		ActionTreeElement ate6 =          at.add(factory.createAction(1, cell4), ate5);

		assertArrayEquals(new ActionTreeElement[] { ate4, ate3, ate2, ate5, ate6 },
				ActionTree.Companion.findPath(ate4, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate6, ate5, ate2 }, ActionTree.Companion.findPath(ate6, ate2).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate2, ate5, ate6 }, ActionTree.Companion.findPath(ate2, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate6, ate5, ate2, ate1 }, ActionTree.Companion.findPath(ate6, ate1).toArray());

		assertArrayEquals(new ActionTreeElement[] { ate1, ate2, ate5, ate6 }, ActionTree.Companion.findPath(ate1, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] {}, ActionTree.Companion.findPath(ate6, ate6).toArray());

		assertArrayEquals(new ActionTreeElement[] {}, ActionTree.Companion.findPath(ate1, ate1).toArray());
	}



	@Test
	public void testFindPathBetweenDifferentTrees() {
		ActionTree at1 = new ActionTree();
		ActionTree at2 = new ActionTree();

		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate1 = at1.getRoot();
		ActionTreeElement ate2 = at1.add(factory.createAction(4, cell), ate1);

		ActionTreeElement ate3 = at2.getRoot();
		ActionTreeElement ate4 = at2.add(factory.createAction(2, cell), ate3);

		List<ActionTreeElement> path = ActionTree.Companion.findPath(ate2, ate4);
		assertTrue(path.isEmpty());//return empty list because elements have same id

	}
}
