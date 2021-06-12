package de.sudoq.model.sudoku;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class UniqueConstraintBehaviorTests extends TestWithInitCleanforSingletons {

	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	@Test
	public void testConstraint() {
		TypeBuilder.get99();//just to force init of filemanager
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuDir).createSudoku();

		sudoku.getCell(Position.get(0, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(4);
		sudoku.getCell(Position.get(1, 1)).setCurrentValue(5);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(6);

		Constraint constraint = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
		constraint.addPosition(Position.get(0, 0));
		constraint.addPosition(Position.get(0, 1));
		constraint.addPosition(Position.get(0, 2));
		constraint.addPosition(Position.get(1, 0));
		constraint.addPosition(Position.get(1, 1));
		constraint.addPosition(Position.get(1, 2));

		assertTrue("constraint has no unique behavior", constraint.hasUniqueBehavior());
		assertTrue("constraint not saturated", constraint.isSaturated(sudoku));

		sudoku.getCell(Position.get(0, 0)).setCurrentValue(2);

		assertFalse("constraint still saturated", constraint.isSaturated(sudoku));
	}
}
