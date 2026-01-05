package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

public class BranchingPoolTests {
    private PositionMap<CandidateSet> dummyPositionMap = new PositionMap<>(Position.get(9,9));

	@Test
	public void testComplete() {
		BranchingPool pool = new BranchingPool();
		assertEquals(pool.getBranching(Position.get(1, 5), 1, dummyPositionMap).candidate, 1);
		assertEquals(pool.getBranching(Position.get(1, 5), 2, dummyPositionMap).position, Position.get(1, 5));
		// new branchings to be initialized
		pool.getBranching(Position.get(1, 5), 4, dummyPositionMap);

		pool.recycleAllBranchings();
		// return another branching
		pool.recycleLastBranching();

	}

	@Test(expected = NullPointerException.class)
	public void testGetBranchingNull() {
		BranchingPool pool = new BranchingPool();

		// should throw exception
		pool.getBranching(null, 5, dummyPositionMap);
	}

}
