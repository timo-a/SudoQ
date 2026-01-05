package de.sudoq.model.actionTree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.sudoq.model.sudoku.Cell;

class ActionTreeElementTests {

    @Test
    void construction() {
		Action action = new SolveAction(1, new Cell(-1, 1));

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);
		ActionTreeElement ate2 = new ActionTreeElement(2, action, ate1);
		for (ActionTreeElement actionTreeElement : ate1) {
			assertEquals(actionTreeElement, ate2);
		}

		assertEquals(ate1, ate2.getParent());
		assertTrue(ate1.getChildrenList().contains(ate2));
		assertEquals(ate2, ate1.getChildren().next());
		assertFalse(ate1.isCorrect());
		assertFalse(ate1.isMistake());
	}

    @Test
    void addNullChild() {
		Action action = new SolveAction(1, new Cell(-1, 1));

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);
		ActionTreeElement ate2 = new ActionTreeElement(2, action, ate1);

		assertThrows(NullPointerException.class, () -> ate2.addChild(null));
	}

    @Test
    void failConstruction() {
		assertThrows(NullPointerException.class, () -> new ActionTreeElement(1, null, null));
	}

    @Test
    void equals() {
		Action action = new SolveAction(1, new Cell(-1, 1));
		ActionTreeElement ate = new ActionTreeElement(1, action, null);
		ActionTreeElement ate1 = new ActionTreeElement(2, action, null);
        assertNotEquals(new Object(), ate);
        assertNotEquals(ate, ate1);
		ate1 = new ActionTreeElement(1, action, null);
        assertEquals(ate, ate1);
		ate1.mark();
        assertNotEquals(ate, ate1);
		ate1 = new ActionTreeElement(1, new SolveAction(1, new Cell(0, 1)), null);
        assertNotEquals(ate, ate1);

	}

    @Test
    void isSplitUp() {
		Action action = new SolveAction(1, new Cell(-1, 1));

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);
		assertFalse(ate1.isSplitUp());
		new ActionTreeElement(2, action, ate1);
		assertFalse(ate1.isSplitUp());
		new ActionTreeElement(3, action, ate1);
		assertTrue(ate1.isSplitUp());
	}

    @Test
    void actionExecution() {
		Cell f = new Cell(true, 3, -1, 9);
		Action action = new SolveAction(1 - Cell.EMPTYVAL, f);

		ActionTreeElement ate1 = new ActionTreeElement(1, action, null);

		int value = f.getCurrentValue();
		ate1.execute();
        assertEquals(1, f.getCurrentValue());
		ate1.undo();
		assertEquals(f.getCurrentValue(), value);
	}

    @Test
    void compare() {
		ActionTreeElement a1 = new ActionTreeElement(1, new SolveAction(1, new Cell(1, 9)), null);
		ActionTreeElement a2 = new ActionTreeElement(3, new SolveAction(1, new Cell(1, 9)), null);
		ActionTreeElement a3 = new ActionTreeElement(1, new SolveAction(1, new Cell(1, 9)), null);
        assertEquals(-2, a1.compareTo(a2));
        assertEquals(2, a2.compareTo(a1));
        assertEquals(0, a1.compareTo(a3));
        assertEquals(0, a3.compareTo(a1));
        assertEquals(0, a2.compareTo(a2));
	}

    @Test
    void mark() {
		ActionTreeElement a1 = new ActionTreeElement(1, new SolveAction(1, new Cell(1, 9)), null);
		assertFalse(a1.isMarked());
		a1.mark();
		assertTrue(a1.isMarked());
	}
}
