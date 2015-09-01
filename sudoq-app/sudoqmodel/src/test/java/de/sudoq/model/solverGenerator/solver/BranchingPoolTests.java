package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.sudoq.model.solverGenerator.solver.BranchingPool;
import de.sudoq.model.sudoku.Position;

public class BranchingPoolTests {

	@Test(expected = IllegalArgumentException.class)
	public void testComplete() {
		BranchingPool pool = new BranchingPool();
		assertEquals(pool.getBranching(Position.get(1, 5), 1).candidate, 1);
		assertEquals(pool.getBranching(Position.get(1, 5), 2).position, Position.get(1, 5));
		// new branchings to be initialized
		pool.getBranching(Position.get(1, 5), 4);

		pool.returnAll();
		// return another branching
		pool.returnBranching();

		// should throw exception
		pool.getBranching(null, 5);
	}
}
