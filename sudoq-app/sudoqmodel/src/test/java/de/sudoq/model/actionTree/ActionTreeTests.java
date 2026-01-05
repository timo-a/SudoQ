package de.sudoq.model.actionTree;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.sudoq.model.sudoku.Cell;

public class ActionTreeTests {

    @Test
    void construction() {
		ActionTree at = new ActionTree();
		assertNotNull(at.getRoot());
	}

    @Test
    void addingElementsMountOnNull() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate = at.getRoot();

		assertThrows(NullPointerException.class, () -> at.add(factory.createAction(2, cell), null));
	}

    @Test
    void addingElements() {
		ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();
		Cell cell = new Cell(-1, 1);

		ActionTreeElement ate = at.getRoot();//add(factory.createAction(1, field), null);
        assertEquals(1, ate.getId());

        assertEquals(2, at.add(factory.createAction(1, cell), ate).getId());

        assertEquals(1, ate.getChildrenList().size());
	}

    @Test
    void equals() {
		assertNotEquals(new ActionTree(), new Object());

		ActionTree at = new ActionTree();
		ActionTree at2 = new ActionTree();
		at2.add(new SolveActionFactory().createAction(5, new Cell(-1, 1)), at2.getRoot());
        assertNotEquals(at, at2);
        assertEquals(at, at);
	}

    @Test
    void gettingElementsById() {
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
    void searchForInexistentId() {
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
    void findPath() {
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
    void findPathBetweenDifferentTrees() {
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
