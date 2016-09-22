package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.junit.Test;

import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SubsetHelperTests extends HiddenHelper{

	public SubsetHelperTests(){
		super(new SolverSudoku(new Sudoku(TypeBuilder.get99())),4,0 );
	}

	@Test
	public void testGetNextSubset(){

		for(int i : new int[]{0,1,2,3, 5,7})
			constraintSet.set(i);

		for(int i : new int[]{0,1,2,3})
			currentSet.set(i);

		this.level=4;        //just to be sure

		evaluateGetNext();

	}


	public void evaluateGetNext(){
		do
			System.out.println(currentSet);
		while(getNextSubset());
	}


	private void prepareSudoku(SolverSudoku sudoku){
		for(int i : new int[]{1, 3,4,5,6,7,8})
			setVal(sudoku, i,1, i); // row 1 all filled except 2,9

		setVal(sudoku, 1,2, 4); // row 2 all filled except 2,9
		setVal(sudoku, 3,2, 5);
		setVal(sudoku, 4,2, 6);
		setVal(sudoku, 5,2, 7);
		setVal(sudoku, 6,2, 8);
		setVal(sudoku, 7,2, 1);
		setVal(sudoku, 8,2, 3);

		sudoku.resetCandidates();//candidates are also recalculated
	}

	/**
	 * leave 2 fields each in the upper 2 rows. -> 2 naked pairs
	 * then test if they are removed from other field as candidates
	 */
	@Test
	public void testNakedUpdateOne() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));

		prepareSudoku(sudoku);
		

		SubsetHelper helper = new NakedHelper(sudoku, 2, 21);
		assertEquals(helper.getComplexity(), 21);

		assertEquals(2, getNumberOfNotes(sudoku,2, 1));//2 candidates each are expected as all others are set in the constraint.
		assertEquals(2, getNumberOfNotes(sudoku,2, 2));
		assertEquals(2, getNumberOfNotes(sudoku,9, 2));
		assertEquals(2, getNumberOfNotes(sudoku,9, 2));

		assertEquals(5, getNumberOfNotes(sudoku,1, 3));//5 candidates are expected: 3 in row 3 + 2 from empty neighbours
		assertEquals(5, getNumberOfNotes(sudoku,2, 3));//                              in case of (7,3) keep in mind that only the right block deletes candidates in 87,3)
		assertEquals(5, getNumberOfNotes(sudoku,3, 3));//row 1: 1_3  456  78_
		assertEquals(5, getNumberOfNotes(sudoku,7, 3));//row 2: 4_5  678  13_
		assertEquals(5, getNumberOfNotes(sudoku,8, 3));//row 3: abc  ___  de_

		// Use helper 4 times: 2 for updating columns (1 and 8), 2 for updating
		// blocks (0 and 2)
		helper.update(true);
		helper.update(false);
		helper.update(false);
		helper.update(false);

		assertEquals(2, getNumberOfNotes(sudoku,2, 1));//should still be 2
		assertEquals(2, getNumberOfNotes(sudoku,2, 2));
		assertEquals(2, getNumberOfNotes(sudoku,9, 2));
		assertEquals(2, getNumberOfNotes(sudoku,9, 2));

		assertEquals(3, getNumberOfNotes(sudoku,1, 3));//"2" and "9" should not be possible anymore
		assertEquals(3, getNumberOfNotes(sudoku,2, 3));
		assertEquals(3, getNumberOfNotes(sudoku,3, 3));
		assertEquals(3, getNumberOfNotes(sudoku,7, 3));
		assertEquals(3, getNumberOfNotes(sudoku,8, 3));

		assertFalse(sudoku.getCurrentCandidates(Position.get(2-1, 3-1)).get(1));
		assertFalse(sudoku.getCurrentCandidates(Position.get(2-1, 3-1)).get(8));
	}

	@Test
	public void testNakedUpdateAll() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));

		prepareSudoku(sudoku);

		SubsetHelper helper = new NakedHelper(sudoku, 2, 21);

		assertEquals(2, getNumberOfNotes(sudoku, 2, 1));
		assertEquals(2, getNumberOfNotes(sudoku, 2, 2));
		assertEquals(2, getNumberOfNotes(sudoku, 9, 2));
		assertEquals(2, getNumberOfNotes(sudoku, 9, 2));
		assertEquals(5, getNumberOfNotes(sudoku, 2, 3));
		assertEquals(5, getNumberOfNotes(sudoku, 1, 3));
		assertEquals(5, getNumberOfNotes(sudoku, 3, 3));
		assertEquals(5, getNumberOfNotes(sudoku, 7, 3));
		assertEquals(5, getNumberOfNotes(sudoku, 8, 3));

		List<SolveDerivation> derivations = new ArrayList<SolveDerivation>();
		while (helper.update(true))
			derivations.add(helper.getDerivation());
		

		assertEquals(derivations.size(), 4);
		assertEquals(2, getNumberOfNotes( sudoku, 2, 1) );
		assertEquals(2, getNumberOfNotes( sudoku, 2, 2) );
		assertEquals(2, getNumberOfNotes( sudoku, 9, 2) );
		assertEquals(2, getNumberOfNotes( sudoku, 9, 2) );
		assertEquals(3, getNumberOfNotes( sudoku, 2, 3) );
		assertEquals(3, getNumberOfNotes( sudoku, 1, 3) );
		assertEquals(3, getNumberOfNotes( sudoku, 3, 3) );
		assertEquals(3, getNumberOfNotes( sudoku, 7, 3) );
		assertEquals(3, getNumberOfNotes( sudoku, 8, 3) );

		assertFalse(sudoku.getCurrentCandidates(Position.get(1-1, 2-1)).get(0));
		assertFalse(sudoku.getCurrentCandidates(Position.get(1-1, 2-1)).get(2));
	}

	@Test
	public void testNakedInvalidCandidateLists() {
		SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
		for (Position p : sudoku.positions) 
			sudoku.getCurrentCandidates(p).clear();
		

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
		setVal(sudoku, 1,1, 1);
		setVal(sudoku, 3,1, 3);
		setVal(sudoku, 4,1, 4);
		setVal(sudoku, 5,1, 5);
		setVal(sudoku, 6,1, 6);
		setVal(sudoku, 7,1, 7);
		setVal(sudoku, 8,1, 8);
		setVal(sudoku, 1,2, 4);
		setVal(sudoku, 3,2, 5);
		setVal(sudoku, 4,2, 6);
		setVal(sudoku, 5,2, 7);
		setVal(sudoku, 6,2, 8);
		setVal(sudoku, 7,2, 1);
		setVal(sudoku, 8,2, 3);
		setVal(sudoku, 2,3, 6);

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
		for(int i : new int[]{1,  3,4,5,6,7,8})
			setVal(sudoku, i,1, i);

		setVal(sudoku, 1,2, 4);
		setVal(sudoku, 3,2, 5);
		setVal(sudoku, 4,2, 6);
		setVal(sudoku, 5,2, 7);
		setVal(sudoku, 6,2, 8);
		setVal(sudoku, 7,2, 1);
		setVal(sudoku, 8,2, 3);

		setVal(sudoku, 2,3, 6);

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

	private void setVal(Sudoku s, int x, int y, int val){
		s.getField(Position.get(x-1, y-1)).setCurrentValue(val-1);
	}
	private int getNumberOfNotes(SolverSudoku s, int x, int y){
		return s.getCurrentCandidates(Position.get(x-1, y-1)).cardinality();
	}




}
