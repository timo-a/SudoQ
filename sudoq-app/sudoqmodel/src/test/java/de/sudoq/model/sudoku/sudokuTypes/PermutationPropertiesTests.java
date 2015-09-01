package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.sudoku.sudokuTypes.PermutationProperties;

public class PermutationPropertiesTests {

	@Test
	public void test() {
		PermutationProperties[] types = PermutationProperties.values();
		for (PermutationProperties type : types) {
			assertTrue(PermutationProperties.valueOf(type.toString()).equals(type));
		}
	}

}
