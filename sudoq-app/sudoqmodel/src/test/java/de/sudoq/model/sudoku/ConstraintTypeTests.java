package de.sudoq.model.sudoku;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstraintTypeTests {

	@Test
	public void test() {
		ConstraintType[] types = ConstraintType.values();
		for (ConstraintType type : types) {
			assertTrue(ConstraintType.valueOf(type.toString()).equals(type));
		}
	}

}
