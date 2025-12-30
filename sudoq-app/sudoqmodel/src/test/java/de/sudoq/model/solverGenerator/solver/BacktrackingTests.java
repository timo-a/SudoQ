package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class BacktrackingTests extends TestWithInitCleanforSingletons {

	@Test
	public void testInitialisation() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		Backtracking back = new Backtracking(sudoku, 10);
		assertEquals(back.getComplexityScore(), 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIInitialisationWithInvalidComplexity() {
		new Backtracking(new SolverSudoku(new Sudoku(TypeBuilder.get99())), -2);
	}

	@Test
	public void testUpdateOne() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		Backtracking back = new Backtracking(sudoku, 10);

		sudoku.getCurrentCandidates(Position.get(1, 3)).clear(2, 8);
		back.update(true);
		SolveDerivation deriv = back.getDerivation();
		assertEquals(1, sudoku.getBranchLevel());
		assertEquals(deriv.getCellIterator().next().getPosition(), Position.get(1, 3));
	}

	@Test(timeout = 60)
	public void testAlreadySolved() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		sudoku.setComplexity(Complexity.arbitrary);
		assertTrue(new Solver(sudoku).solveAll(false, true));
		//TODO see if sudoku can be mocked better, use smaller sudoku, understand why no return false
		Backtracking back = new Backtracking(sudoku, 10);
		sudoku.updateCandidates();
		// there should be no fields to solve any more
		assertFalse(back.update(false));
	}

}
