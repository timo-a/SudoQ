package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Test;

import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SubsetHelperTests {

	@Test
	public void testNakedUpdateOne() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(2, 0)).setCurrentValue(2);
		sudoku.getField(Position.get(3, 0)).setCurrentValue(3);
		sudoku.getField(Position.get(4, 0)).setCurrentValue(4);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(7);
		sudoku.getField(Position.get(0, 1)).setCurrentValue(3);
		sudoku.getField(Position.get(2, 1)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 1)).setCurrentValue(5);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(6);
		sudoku.getField(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(6, 1)).setCurrentValue(0);
		sudoku.getField(Position.get(7, 1)).setCurrentValue(2);

		sudoku.resetCandidates();

		SubsetHelper helper = new NakedHelper(sudoku, 2, 21);
		assertEquals(helper.getComplexity(), 21);

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(6, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(7, 2)).cardinality(), 5);

		// Use helper 4 times: 2 for updating columns (1 and 8), 2 for updating
		// blocks (0 and 2)
		helper.update(true);
		helper.update(false);
		helper.update(false);
		helper.update(false);

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(6, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(7, 2)).cardinality(), 3);

		assertFalse(sudoku.getCurrentCandidates(Position.get(1, 2)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(1, 2)).get(2));
	}

	@Test
	public void testNakedUpdateAll() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(2, 0)).setCurrentValue(2);
		sudoku.getField(Position.get(3, 0)).setCurrentValue(3);
		sudoku.getField(Position.get(4, 0)).setCurrentValue(4);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(7);
		sudoku.getField(Position.get(0, 1)).setCurrentValue(3);
		sudoku.getField(Position.get(2, 1)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 1)).setCurrentValue(5);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(6);
		sudoku.getField(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(6, 1)).setCurrentValue(0);
		sudoku.getField(Position.get(7, 1)).setCurrentValue(2);

		sudoku.resetCandidates();

		SubsetHelper helper = new NakedHelper(sudoku, 2, 21);
		assertEquals(helper.getComplexity(), 21);

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(6, 2)).cardinality(), 5);
		assertEquals(sudoku.getCurrentCandidates(Position.get(7, 2)).cardinality(), 5);

		List<SolveDerivation> derivations = new ArrayList<SolveDerivation>();
		while (helper.update(true)) {
			derivations.add(helper.getDerivation());
		}

		assertEquals(derivations.size(), 4);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(6, 2)).cardinality(), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(7, 2)).cardinality(), 3);

		assertFalse(sudoku.getCurrentCandidates(Position.get(1, 2)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(1, 2)).get(2));
	}

	@Test
	public void testNakedInvalidCandidateLists() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		for (Position p : sudoku.positions) {
			sudoku.getCurrentCandidates(p).clear();
		}

		BitSet nakedDouble = new BitSet();
		nakedDouble.set(0, 2);
		sudoku.getCurrentCandidates(Position.get(0, 0)).or(nakedDouble);
		sudoku.getCurrentCandidates(Position.get(0, 1)).or(nakedDouble);
		sudoku.getCurrentCandidates(Position.get(0, 2)).or(nakedDouble);

		SubsetHelper helper = new NakedHelper(sudoku, 2, 21);

		while (helper.update(false))
			;

		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 0)), nakedDouble);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 1)), nakedDouble);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)), nakedDouble);
	}

	@Test
	public void testHiddenUpdateOne() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(2, 0)).setCurrentValue(2);
		sudoku.getField(Position.get(3, 0)).setCurrentValue(3);
		sudoku.getField(Position.get(4, 0)).setCurrentValue(4);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(7);
		sudoku.getField(Position.get(0, 1)).setCurrentValue(3);
		sudoku.getField(Position.get(2, 1)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 1)).setCurrentValue(5);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(6);
		sudoku.getField(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(6, 1)).setCurrentValue(0);
		sudoku.getField(Position.get(7, 1)).setCurrentValue(2);
		sudoku.getField(Position.get(1, 2)).setCurrentValue(5);

		sudoku.resetCandidates();

		SubsetHelper helper = new HiddenHelper(sudoku, 2, 22);
		assertEquals(helper.getComplexity(), 22);

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 4);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 4);

		// Use helper 1 time to remove candidates 1 and 8 from Positions 0,2 and
		// 2,2
		helper.update(true);

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 2);

		assertFalse(sudoku.getCurrentCandidates(Position.get(0, 2)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(0, 2)).get(2));
		assertFalse(sudoku.getCurrentCandidates(Position.get(2, 2)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(2, 2)).get(2));
	}

	@Test
	public void testHiddenUpdateAll() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		sudoku.getField(Position.get(0, 0)).setCurrentValue(0);
		sudoku.getField(Position.get(2, 0)).setCurrentValue(2);
		sudoku.getField(Position.get(3, 0)).setCurrentValue(3);
		sudoku.getField(Position.get(4, 0)).setCurrentValue(4);
		sudoku.getField(Position.get(5, 0)).setCurrentValue(5);
		sudoku.getField(Position.get(6, 0)).setCurrentValue(6);
		sudoku.getField(Position.get(7, 0)).setCurrentValue(7);
		sudoku.getField(Position.get(0, 1)).setCurrentValue(3);
		sudoku.getField(Position.get(2, 1)).setCurrentValue(4);
		sudoku.getField(Position.get(3, 1)).setCurrentValue(5);
		sudoku.getField(Position.get(4, 1)).setCurrentValue(6);
		sudoku.getField(Position.get(5, 1)).setCurrentValue(7);
		sudoku.getField(Position.get(6, 1)).setCurrentValue(0);
		sudoku.getField(Position.get(7, 1)).setCurrentValue(2);
		sudoku.getField(Position.get(1, 2)).setCurrentValue(5);

		sudoku.resetCandidates();

		SubsetHelper helper = new HiddenHelper(sudoku, 2, 22);
		assertEquals(helper.getComplexity(), 22);

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(8, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 4);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 4);

		while (helper.update(true))
			;

		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 0)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(1, 1)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 2)).cardinality(), 2);
		assertEquals(sudoku.getCurrentCandidates(Position.get(2, 2)).cardinality(), 2);

		assertFalse(sudoku.getCurrentCandidates(Position.get(0, 2)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(0, 2)).get(2));
		assertFalse(sudoku.getCurrentCandidates(Position.get(2, 2)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(2, 2)).get(2));
	}

	@Test
	public void testIllegalArguments() {
		try {
			new NakedHelper(null, 1, 20);
			fail("No IllegalArgumentException thrown, altough sudoku was null");
		} catch (IllegalArgumentException e) {
		}

		try {
			new NakedHelper(new SolverSudoku(new Sudoku(TypeBuilder.get99())), 0, 20);
			fail("No IllegalArgumentException thrown, altough level was too low");
		} catch (IllegalArgumentException e) {
		}

		try {
			new NakedHelper(new SolverSudoku(new Sudoku(TypeBuilder.get99())), 1, -1);
			fail("No IllegalArgumentException thrown, altough complexity was too low");
		} catch (IllegalArgumentException e) {
		}
	}
}
