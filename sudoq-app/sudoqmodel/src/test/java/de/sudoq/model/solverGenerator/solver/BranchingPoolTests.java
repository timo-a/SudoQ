package de.sudoq.model.solverGenerator.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

class BranchingPoolTests {
    private PositionMap<CandidateSet> dummyPositionMap = new PositionMap<>(Position.get(9,9));

    @Test
    void complete() {
		BranchingPool pool = new BranchingPool();
        assertEquals(1, pool.getBranching(Position.get(1, 5), 1, dummyPositionMap).candidate);
		assertEquals(pool.getBranching(Position.get(1, 5), 2, dummyPositionMap).position, Position.get(1, 5));
		// new branchings to be initialized
		pool.getBranching(Position.get(1, 5), 4, dummyPositionMap);

		pool.recycleAllBranchings();
		// return another branching
		pool.recycleLastBranching();

	}

    @Test
    void getBranchingNull() {
		BranchingPool pool = new BranchingPool();

		// should throw exception
		assertThrows(NullPointerException.class, () -> pool.getBranching(null, 5, dummyPositionMap));
	}

}
