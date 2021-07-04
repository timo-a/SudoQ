package de.sudoq.model.sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.BitSet;

import org.junit.Test;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

public class PositionMapTests {

	@Test
	public void testStandardUsage() {
		PositionMap<BitSet> map = new PositionMap<>(Position.get(9, 9));
		BitSet b = new BitSet();
		map.put(Position.get(3, 2), b);
		b.set(7);
		assertTrue(map.get(Position.get(3, 2)).get(7));
		b.clear();
		assertEquals(map.get(Position.get(3, 2)).cardinality(), 0);

		PositionMap<BitSet> map2 = map.clone();
		assertEquals(map.get(Position.get(3, 2)), map2.get(Position.get(3, 2)));
		assertEquals(map.get(Position.get(2, 3)), map2.get(Position.get(2, 3)));
	}

	@Test
	public void testIllegalArguments() {

		try {
			new PositionMap<BitSet>(Position.get(1, 0));
			fail("No IllegalArgumentException was thrown, altough the dimension was null");
		} catch (IllegalArgumentException e) {
		}

		try {
			new PositionMap<BitSet>(Position.get(0, 1));
			fail("No IllegalArgumentException was thrown, altough the dimension was null");
		} catch (IllegalArgumentException e) {
		}

		PositionMap<BitSet> map = new PositionMap<>(Position.get(9, 9));

		try {
			map.put(Position.get(10, 9), new BitSet());
			fail("No IllegalArgumentException was thrown, altough the Position was invalid");
		} catch (IllegalArgumentException e) {
		}

		try {
			map.get(Position.get(10, 9));
			fail("No IllegalArgumentException was thrown, altough the Position was invalid");
		} catch (IllegalArgumentException e) {
		}

		try {
			map.get(Position.get(9, 10));
			fail("No IllegalArgumentException was thrown, altough the Position was invalid");
		} catch (IllegalArgumentException e) {
		}
	}

}
