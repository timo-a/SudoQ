package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo2;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class BacktrackingTests {

    private static SudokuTypeRepo4Tests sudokuTypeRepo = new SudokuTypeRepo4Tests();

    private PrettySudokuRepo2 sudokuRepo = new PrettySudokuRepo2(sudokuTypeRepo);

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

	@Test(timeout = 6000)
	public void testAlreadySolved() {//todo can we do this with 4x4 (too) so it is simpler?
        Path sudokuPath = Paths.get("sudokus/x_easy_1.pretty");
        Sudoku sudoku = sudokuRepo.read(sudokuPath, Complexity.easy);
        Solver solver = new Solver(sudoku);
        SolverSudoku solverSudoku = solver.getSolverSudoku();//todo once we have defined the output of the solver we can omit solving a soduku first
        assertTrue(solver.solveAll(false, false, false));
		//TODO see if sudoku can be mocked better, use smaller sudoku, understand why no return false
		Backtracking back = new Backtracking(solverSudoku, 10);
		solverSudoku.updateCandidates();
		// there should be no fields to solve any more
        boolean result = back.update(false);
        assertFalse(result);
	}
}
