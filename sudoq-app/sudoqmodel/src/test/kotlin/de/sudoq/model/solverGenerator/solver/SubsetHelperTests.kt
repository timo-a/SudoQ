package de.sudoq.model.solverGenerator.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SubsetHelperTests extends HiddenHelper{

	public SubsetHelperTests(){
		super(new SolverSudoku(new Sudoku(TypeBuilder.get99())),4,0 );
	}

    @Test
    void testGetNextSubset(){

		for(int i : new int[]{0,1,2,3, 5,7})
			constraintSet.set(i);

		for(int i : new int[]{0,1,2,3})
			currentSet.set(i);

		assert super.getLevel() == 4;        //just to be sure

		evaluateGetNext();

	}


	public void evaluateGetNext(){
		do
			System.out.println(currentSet);
		while(getNextSubset());
	}


    @Test
    void hiddenUpdateOne() {
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
        assertEquals(22, helper.getComplexityScore());

        assertEquals(2, getCardinality(sudoku, 1, 0));
        assertEquals(2, getCardinality(sudoku, 1, 1));
        assertEquals(2, getCardinality(sudoku, 8, 1));
        assertEquals(2, getCardinality(sudoku, 8, 1));
        assertEquals(4, getCardinality(sudoku, 0, 2));
        assertEquals(4, getCardinality(sudoku, 2, 2));

		// Use helper 1 time to remove candidates 1 and 8 from Positions 0,2 and
		// 2,2
		helper.update(true);

        assertEquals(2, getCardinality(sudoku, 1, 0));
        assertEquals(2, getCardinality(sudoku, 1, 1));
        assertEquals(2, getCardinality(sudoku, 0, 2));
        assertEquals(2, getCardinality(sudoku, 2, 2));

		assertFalse(getCandidate(sudoku, 0, 2,  0));
		assertFalse(getCandidate(sudoku, 0, 2,  2));
		assertFalse(getCandidate(sudoku, 2, 2,  0));
		assertFalse(getCandidate(sudoku, 2, 2,  2));
	}

    @Test
    void hiddenUpdateAll() {
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
        assertEquals(22, helper.getComplexityScore());

        assertEquals(2, getCardinality(sudoku, 1, 0));
        assertEquals(2, getCardinality(sudoku, 1, 1));
        assertEquals(2, getCardinality(sudoku, 8, 1));
        assertEquals(2, getCardinality(sudoku, 8, 1));
        assertEquals(4, getCardinality(sudoku, 0, 2));
        assertEquals(4, getCardinality(sudoku, 2, 2));

		while (helper.update(true))
			;

        assertEquals(2, getCardinality(sudoku, 1, 0));
        assertEquals(2, getCardinality(sudoku, 1, 1));
        assertEquals(2, getCardinality(sudoku, 0, 2));
        assertEquals(2, getCardinality(sudoku, 2, 2));

		assertFalse(getCandidate(sudoku, 0, 2,  0));
		assertFalse(getCandidate(sudoku, 0, 2,  2));
		assertFalse(getCandidate(sudoku, 2, 2,  0));
		assertFalse(getCandidate(sudoku, 2, 2,  2));
	}

	/**
	 *
	 * @param s
	 * @param x number of column starting with 1
	 * @param y number of row starting with 1
     * @param val value starting with 1
     */
	public static void setVal(Sudoku s, int x, int y, int val){
		s.getCell(Position.get(x-1, y-1)).setCurrentValue(val-1);
	}

	public static int getCardinality(SolverSudoku s, int x, int y){
		return s.getCurrentCandidates(Position.get(x, y)).cardinality();
	}

	public static boolean getCandidate(SolverSudoku s, int x, int y, int candidate){
		return s.getCurrentCandidates(Position.get(x, y)).get(candidate);
	}
}
