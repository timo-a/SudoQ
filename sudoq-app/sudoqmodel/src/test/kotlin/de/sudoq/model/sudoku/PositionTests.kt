package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.sudoku.Position;

public class PositionTests {

	@Test
	// constructor
	public void test() {

		int[] iList = { Integer.MIN_VALUE, -73, -1, 0, 1, 64, Integer.MAX_VALUE };
		int[] jList = { Integer.MIN_VALUE, -83, -1, 0, 1, 63, Integer.MAX_VALUE };

		for (int i : iList) {
			for (int j : jList) {

				if (i < 0 || j < 0) {

					try {
						Position.get(i, j);
						fail("No IllegalArgumentException is thrown");
					} catch (IllegalArgumentException expected) {
					}
				} else {

					try {
						Position pos = Position.get(i, j);
						assertTrue("getpos does not return initialised value", pos.getX() == i && pos.getY() == j);
					} catch (IllegalArgumentException unexpected) {
						fail("IllegalArgumentException is thrown");
					}

				}
			}
		}
	}

	@Test
	public void equalTest() {

		int[][] cases = { { 1, 2, 3, 4, 0 }, { 0, 0, 0, 0, 1 }, { 4, 4, 4, 3, 0 }, { 4, 5, 4, 5, 1 },
				{ 7987, 21523, 7987, 21523, 1 }, { 7987, 21523, 7988, 21521, 0 } };

		for (int[] i : cases) {

			Position posA = Position.get(i[0], i[1]);
			Position posB = Position.get(i[2], i[3]);
			boolean result = posA.equals(posB);
			assertTrue("equals() works not correct.", (result && i[4] == 1) || !result && i[4] == 0);
		}
		Position pos = Position.get(1, 0);
		assertFalse("equal accepts null", pos.equals(null));
		assertFalse("equal accepts int", pos.equals(9));
	}

	@Test
	public void hashCodeTest() {
		int[][] cases = { { 1, 2, 3, 4, 0 }, { 0, 0, 0, 0, 1 }, { 4, 4, 4, 3, 0 }, { 4, 5, 4, 5, 1 },
				{ 7987, 21523, 7987, 21523, 1 }, { 7987, 21523, 7988, 21521, 0 } };

		for (int[] i : cases) {

			Position posA = Position.get(i[0], i[1]);
			Position posB = Position.get(i[2], i[3]);
			boolean resEquals = posA.equals(posB);
			boolean resHash = posA.hashCode() == posB.hashCode();
			assertFalse("hashCode() works not correct.", resEquals && !resHash);
		}
	}

	@Test
	public void testToString() {
		Position p = Position.get(5, 9);
		assertEquals(p.toString(), "5, 9");
	}

}