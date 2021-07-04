package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.sudoq.model.sudoku.Position;

public class BranchingPoolTests {

	@Test
	public void testComplete() {
		BranchingPool pool = new BranchingPool();
		assertEquals(pool.getBranching(Position.get(1, 5), 1).candidate, 1);
		assertEquals(pool.getBranching(Position.get(1, 5), 2).position, Position.get(1, 5));
		// new branchings to be initialized
		pool.getBranching(Position.get(1, 5), 4);

		pool.recycleAllBranchings();
		// return another branching
		pool.recycleLastBranching();

	}

	@Test(expected = NullPointerException.class)
	public void testGetBranchingNull() {
		BranchingPool pool = new BranchingPool();

		// should throw exception
		pool.getBranching(null, 5);
	}

}
