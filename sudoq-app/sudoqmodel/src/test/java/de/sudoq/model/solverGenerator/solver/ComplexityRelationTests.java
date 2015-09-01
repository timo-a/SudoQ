package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.solverGenerator.solver.ComplexityRelation;

public class ComplexityRelationTests {

	@Test
	public void test() {
		ComplexityRelation[] types = ComplexityRelation.values();
		for (ComplexityRelation type : types) {
			assertTrue(ComplexityRelation.valueOf(type.toString()).equals(type));
		}
	}

}
