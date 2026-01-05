package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.LastDigitHelper;
import de.sudoq.model.solverGenerator.solver.helper.LeftoverNoteHelper;
import de.sudoq.model.solverGenerator.solver.helper.LockedCandandidatesHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper;
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper;
import de.sudoq.model.solverGenerator.solver.helper.XWingHelper;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class HelperTests {

	@Test
	public void testXWing(){
		SolverSudoku sudoku = new SolverSudoku(SudokuMockUps.getXWing());

		SolveHelper helper = new XWingHelper(sudoku, sudoku.getComplexityValue());

		List<SolveDerivation> sdlist = new ArrayList();

		assertTrue(sudoku.getCurrentCandidates(Position.get(4,3)).get(4));


		while (helper.update(true)){
			sdlist.add(helper.getDerivation());
			System.out.println("print derivation:");
			System.out.println(sdlist.get(sdlist.size()-1));

		}
		System.out.println("sdlist "+sdlist.size());

		assertTrue(sdlist.size() >= 1);

		assertFalse(sudoku.getCurrentCandidates(Position.get(4,3)).get(4));


		/* make sure the solution where "5" is removed from field "5,4" is among the found solutions */

	}

	/* test if xwing is really the first helper that can be applied */
	@Test
	public void testXWing2(){
		SolverSudoku sudoku = new SolverSudoku(SudokuMockUps.getXWing());

		List<SolveHelper> helperList = new ArrayList<>();
		helperList.add(new LastDigitHelper   (sudoku, 1));
		helperList.add(new LeftoverNoteHelper(sudoku, 1));
		helperList.add(new NakedHelper(sudoku, 1,1));
		helperList.add(new NakedHelper(sudoku, 2,1));
		helperList.add(new NakedHelper(sudoku, 3,1));
		helperList.add(new NakedHelper(sudoku, 4,1));
		helperList.add(new HiddenHelper(sudoku, 1,1));
		helperList.add(new HiddenHelper(sudoku, 2,1));
		helperList.add(new HiddenHelper(sudoku, 3,1));
		helperList.add(new HiddenHelper(sudoku, 4,1));
		helperList.add(new LockedCandandidatesHelper(sudoku, 1));
		helperList.add(new XWingHelper(sudoku, 1));

		List<SolveDerivation> sdlist = new ArrayList();

		assertTrue(sudoku.getCurrentCandidates(Position.get(4,3)).get(4));

		for(SolveHelper sh: helperList)
			if (sh.update(true)) {
				System.out.println(""+sh.getDerivation().getType() + sh.getDerivation());
			}
		/*while (helper.update(true)){
			sdlist.add(helper.getDerivation());
			System.out.println("print derivation:");
			System.out.println(sdlist.get(sdlist.size()-1));

		}
		System.out.println("sdlist "+sdlist.size());

		assertTrue(sdlist.size() >= 1);

		assertFalse(sudoku.getCurrentCandidates(Position.get(4,3)).get(4));
        */

		/* make sure the solution where "5" is removed from field "5,4" is among the found solutions */

	}





	@Test
	public void testNakedInvalidCandidateLists() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		for (Position p : sudoku.getPositions()) {
			sudoku.getCurrentCandidates(p).clear();
		}

		BitSet nakedDouble = new BitSet();
		nakedDouble.set(0, 2);
		sudoku.getCurrentCandidates(Position.get(0, 0)).or(nakedDouble);
		sudoku.getCurrentCandidates(Position.get(0, 1)).or(nakedDouble);
		sudoku.getCurrentCandidates(Position.get(0, 2)).or(nakedDouble);

		SubsetHelper helper = new NakedHelper(sudoku, 2, 21);

		while (helper.update(false))//assertion that one will be found and if none is found endless loop -> timeout
			;

		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 0)), nakedDouble);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 1)), nakedDouble);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)), nakedDouble);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentLevelTooLow() {
		new NakedHelper(new SolverSudoku(new Sudoku(TypeBuilder.get99())), 0, 20);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentComplexityTooLow() {
		new NakedHelper(new SolverSudoku(new Sudoku(TypeBuilder.get99())), 1, -1);
	}

	private void setVal(Sudoku s, int x, int y, int val){
		s.getCell(Position.get(x-1, y-1)).setCurrentValue(val-1);
	}

}
