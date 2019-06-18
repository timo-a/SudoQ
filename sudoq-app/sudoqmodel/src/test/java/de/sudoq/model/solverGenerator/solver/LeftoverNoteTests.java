package de.sudoq.model.solverGenerator.solver;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import de.sudoq.model.Utility;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.LeftoverNoteHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SubsetHelper;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by timo on 17.03.17.
 */
public class LeftoverNoteTests extends LeftoverNoteHelper {

    public LeftoverNoteTests(){
        super(new SolverSudoku(new Sudoku(TypeBuilder.get99())),  0 );
    }

    @BeforeClass
	public static void init() {
		Utility.copySudokus();
		//Profile.getInstance();
	}


    @Test
    public void testIllegalArguments() {
        try {
            new LeftoverNoteHelper(null, 20);
            fail("No IllegalArgumentException thrown, altough sudoku was null");
        } catch (IllegalArgumentException e) {
        }

        try {
            new LeftoverNoteHelper(new SolverSudoku(new Sudoku(TypeBuilder.get99())), -1);
            fail("No IllegalArgumentException thrown, altough complexity was too low");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void LeftoverTest(){
        String pattern = "¹²³⁴ .     .   . \n"
                       + ".    .     .   . \n"

                       + ".    .     .   . \n"
                       + "1    .     .   . \n";

        Sudoku        s = SudokuMockUps.stringToSudoku(SudokuTypes.standard4x4, pattern);
        SolverSudoku ss = new SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING);
        LeftoverNoteHelper loh = new LeftoverNoteHelper(ss,0);
        assertTrue(loh.update(true));
    }

    @Test
    public void hasLeftoverTest(){
        String pattern = "¹²³⁴ .     .   . \n"
                       + ".    .     .   . \n"

                       + ".    .     .   . \n"
                       + "1    .     .   . \n";

        Sudoku        s = SudokuMockUps.stringToSudoku(SudokuTypes.standard4x4, pattern);
        super.sudoku    = new SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING);

        Constraint cc= new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
        for (Constraint c: sudoku.getSudokuType())
            if(c.includes(Position.get(0,0)) && c.includes(Position.get(0,3))){
                System.out.println(c);
                cc=c;
            }
        boolean result = hasLeftoverNotes(cc);
        assertTrue(result);
    }
}
