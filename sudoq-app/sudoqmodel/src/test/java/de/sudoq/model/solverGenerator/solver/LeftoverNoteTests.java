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
import de.sudoq.model.solvingAssistant.HintTypes;
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
public class LeftoverNoteTests {

    @BeforeClass
	public static void init() {
		Utility.copySudokus();
	}


    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentComplexity() {
        new LeftoverNoteHelper(new SolverSudoku(new Sudoku(TypeBuilder.get99())), -1);
    }

    @Test
    public void LeftoverTest(){
        //GIVEN
        String pattern = "¹²³⁴ .     .   . \n"
                       + ".    .     .   . \n"

                       + ".    .     .   . \n"
                       + "1    .     .   . \n";

        Sudoku        s = SudokuMockUps.stringToSudoku(SudokuTypes.standard4x4, pattern);
        SolverSudoku ss = new SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING);
        LeftoverNoteHelper loh = new LeftoverNoteHelper(ss,0);

        //WHEN
        boolean result = loh.update(true);

        //THEN
        assertTrue(result);
        assertEquals(HintTypes.LeftoverNote, loh.getHintType());
        assertEquals(1, loh.getDerivation().getActionList(ss).size());
    }

}
