package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CellTests {

	@Test
	public void testNewCellWithSolution() {
		assertTrue(new Cell(false, 5, -1, 5).getCurrentValue() == 5);
	}

	@Test
	public void testSolution() {
		Cell f;

		try {
			f = new Cell(false, -2, -1, 3);
			fail("Initialisation with negative solution possible.");
		} catch (IllegalArgumentException e) {

		}

		for (int i = 0; i < 20; i += 5) {
			f = new Cell(false, i, -1, i);
			assertTrue("solutionFail", f.getSolution() == i);
		}
	}

	@Test
	public void testCurrent() {
		Cell f = new Cell(true, 8, -1, 20);

		try {
			f.setCurrentValue(Cell.EMPTYVAL - 1);
			fail("Initialisation with negative solution possible.");
		} catch (IllegalArgumentException e) {

		}

		try {
			f.setCurrentValue(Cell.EMPTYVAL - 1, false);
			fail("Initialisation with negative solution possible.");
		} catch (IllegalArgumentException e) {

		}

		for (int i = 0; i < 20; i += 5) {
			f.setCurrentValue(i);
			assertTrue("currentFail", f.getCurrentValue() == i);
		}
	}

	@Test
	public void testToggleNote() {
		Cell f = new Cell(-1, 1);

		for (int i = 0; i < 20; i++) {
			f.toggleNote(i);
			assertTrue("toggleN1 doesn't work", f.isNoteSet(i));
		}

		for (int i = 0; i < 20; i++) {
			f.toggleNote(i);
			assertTrue("toggleN2 doesn't work", !f.isNoteSet(i));
		}

		assertFalse("toggleN2 doesn't work", f.isNoteSet(-4));

	}

	@Test
	public void testEditable() {
		Cell f = new Cell(true, 6, -1, 9);
		assertTrue("editableFail", f.isEditable());
		assertTrue(f.getNumberOfValues() == 9);
		assertTrue(f.getId() == -1);
		f = new Cell(false, 6, -1, 9);
		assertFalse("editableFail", f.isEditable());

		f.setCurrentValue(3);
		assertTrue("editable doesn't lock", f.isNotWrong());
		f.setCurrentValue(-1);// darf nichts bewirken
		assertTrue("editable doesn't lock", f.isNotWrong());
	}

	@Test
	public void testIsSolvedCorrect() {
		Cell f = new Cell(true, 5, -1, 9);
		assertFalse("correctFail", f.isSolvedCorrect());
		f.setCurrentValue(5);
		assertTrue("correctFail", f.isSolvedCorrect());
		f.setCurrentValue(4);
		assertFalse("correctFail", f.isSolvedCorrect());
		f.setCurrentValue(5);
		assertTrue("correctFail", f.isSolvedCorrect());
	}

	@Test
	public void testIsNotWrong() {
		Cell f = new Cell(true, 4, -1, 9);
		f.setCurrentValue(0);
		assertFalse(f.isNotWrong());
	}

	@Test
	public void testEqual() {
		Cell f = new Cell(true, 3, -1, 9);
		Cell g = new Cell(true, 3, -1, 9);
		assertTrue(f.equals(g));

		g.toggleNote(2);

		assertFalse(f.equals(g));
		g.toggleNote(2);
		g.setCurrentValue(4);
		assertFalse(f.equals(g));

		assertFalse(f.equals(null));

		Cell h = new Cell(false, 2, -1, 9);
		assertFalse(f.equals(h));
	}

	@Test
	public void testClearIsEmpty() {
		Cell f = new Cell(true, 3, -1, 9);
		f.setCurrentValue(5);
		assertEquals(f.getCurrentValue(), 5);
		assertTrue(!f.isNotSolved());
		f.clearCurrentValue();
		assertEquals(f.getCurrentValue(), Cell.EMPTYVAL);
		assertTrue(f.isNotSolved());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCurrent() {
		Cell cell = new Cell(true, 0, 0, 9);
		cell.setCurrentValue(2, true);
		cell.setCurrentValue(-2, true);
	}

	@Test
	public void testToString() {
		Cell cell = new Cell(true, 0, 0, 21);
		cell.setCurrentValue(20);
		assertEquals("20", cell.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValueTooHigh() {
		Cell cell = new Cell(true, 2, 0, 4);
		cell.setCurrentValue(4);
	}
}