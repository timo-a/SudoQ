package de.sudoq.model.solverGenerator.solver;


import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

import de.sudoq.model.solverGenerator.solver.ComplexityRelation;

class ComplexityRelationTests {

    @Test
    void test() {
		ComplexityRelation[] types = ComplexityRelation.values();
		for (ComplexityRelation type : types) {
            assertEquals(ComplexityRelation.valueOf(type.toString()), type);
		}
	}

}
