package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class BacktrackingTests {

	@Test
	public void testInitialisation() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		Backtracking back = new Backtracking(sudoku, 10);
		assertEquals(back.getComplexity(), 10);
	}

	@Test
	public void testIllegalArguments() {
		try {
			new Backtracking(null, 5);
			fail("No IllegalArgumentException, altough sudoku was null");
		} catch (IllegalArgumentException e) {
		}
		try {
			new Backtracking(new SolverSudoku(new Sudoku(TypeBuilder.get99())), -2);
			fail("No IllegalArgumentException, complexity was < 0");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testUpdateOne() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		Backtracking back = new Backtracking(sudoku, 10);

		sudoku.getCurrentCandidates(Position.get(1, 3)).clear(2, 8);
		back.update(true);
		SolveDerivation deriv = back.getDerivation();
		assertEquals(sudoku.branchings.size(), 1);
		assertEquals(deriv.getFieldIterator().next().getPosition(), Position.get(1, 3));
	}

	@Test
	public void testAlreadySolved() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		sudoku.setComplexity(Complexity.arbitrary);
		assertTrue(new Solver(sudoku).solveAll(false, true));
		Backtracking back = new Backtracking(sudoku, 10);
		sudoku.updateCandidates();
		// there should be no fields to solve any more
		assertFalse(back.update(false));
	}

}
