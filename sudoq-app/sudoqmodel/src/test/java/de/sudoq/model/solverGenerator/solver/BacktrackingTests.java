package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class BacktrackingTests {

	@BeforeClass
	public static void init() throws IOException {
		Utility.copySudokus();
		Profile.getInstance();
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
        java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
        f.setAccessible(true);
        f.set(null, null);
        java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
        s.setAccessible(true);
        s.set(null, null);
        java.lang.reflect.Field p = Profile.class.getDeclaredField("instance");
        p.setAccessible(true);
        p.set(null, null);
        FileManager.deleteDir(Utility.profiles);
        FileManager.deleteDir(Utility.sudokus);
    }


	@Test
	public void testInitialisation() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		Backtracking back = new Backtracking(sudoku, 10);
		assertEquals(back.getComplexityScore(), 10);
	}

	@Test(expected = AssertionError.class)
	public void testIllegalArgumentNull() {
		new Backtracking(null, 5);
	}

	@Test(expected = AssertionError.class)
	public void testIllegalComplexity() {
		new Backtracking(new SolverSudoku(new Sudoku(TypeBuilder.get99())), -2);
	}

	@Test
	public void testUpdateOne() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		Backtracking back = new Backtracking(sudoku, 10);

		sudoku.getCurrentCandidates(Position.get(1, 3)).clear(2, 8);
		back.update(true);
		SolveDerivation deriv = back.getDerivation();
		assertEquals(sudoku.branchings.size(), 1);
		assertEquals(deriv.getCellIterator().next().getPosition(), Position.get(1, 3));
	}

	@Test
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
