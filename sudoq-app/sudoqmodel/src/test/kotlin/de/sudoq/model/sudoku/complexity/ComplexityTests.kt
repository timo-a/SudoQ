package de.sudoq.model.sudoku.complexity;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComplexityTests {

	@Test
	public void test() {
		Complexity[] types = Complexity.values();
		for (Complexity type : types) {
			assertTrue(Complexity.valueOf(type.toString()).equals(type));
		}
	}

}
