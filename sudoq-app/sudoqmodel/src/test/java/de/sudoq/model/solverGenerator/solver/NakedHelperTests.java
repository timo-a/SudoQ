package de.sudoq.model.solverGenerator.solver;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by timo on 15.10.16.
 */
public class NakedHelperTests extends NakedHelper {


    @BeforeClass
	public static void init() {
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

    public NakedHelperTests(){
        super(new SolverSudoku(new Sudoku(TypeBuilder.get99())),4,0 );
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

    @Test
    public void NakedSingleTest(){
        String pattern = "¹²³⁴ ¹²³⁴  ¹²³⁴ ¹ \n"
                       + "1    2     3    4 \n"

                       + "1    2     3    4 \n"
                       + "1    2     3    4 \n";

        Sudoku        s = SudokuMockUps.stringToSudoku(SudokuTypes.standard4x4, pattern);
        SolverSudoku ss = new SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING);
        NakedHelper nh = new NakedHelper (ss,1,0);
        assertTrue(nh.update(true));


    }


    private void prepareSudoku(SolverSudoku sudoku){

        // 1 _ 3 4 5 6 7 8 _
        // 4 _ 5 6 7 8 1 3 _
        //
        int indx [] = {1,  3, 4, 5, 6, 7, 8};
        int row2 [] = {4,  5, 6, 7, 8, 1, 3};

        for(int x=0; x < indx.length; x++) {
            SubsetHelperTests.setVal(sudoku, indx[x], 1, indx[x]); // row 1 all filled except 2,9
            SubsetHelperTests.setVal(sudoku, indx[x], 2, row2[x]); // row 2 all filled except 2,9
        }

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
        assertEquals(helper.getComplexityScore(), 21);

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


    private int getNumberOfNotes(SolverSudoku s, int x, int y){
        return s.getCurrentCandidates(Position.get(x-1, y-1)).cardinality();
    }

}
