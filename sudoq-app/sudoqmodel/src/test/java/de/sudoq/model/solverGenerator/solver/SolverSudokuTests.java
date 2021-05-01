package de.sudoq.model.solverGenerator.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SumConstraintBehavior;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SolverSudokuTests {

	SolverSudoku sudoku;

	@Before
	public void before() {
		sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
	}

	@Test
	public void testStandardSudoku() {
		Position firstPos  = Position.get(5, 7);
		Position secondPos = Position.get(8, 4);
		Position thirdPos  = Position.get(3, 2);

		sudoku.killCurrentBranch(); //nothing should happen todo put it in own test
		sudoku.getCurrentCandidates(firstPos).clear();
		sudoku.getCurrentCandidates(firstPos).set(2, 4);
		assertEquals(sudoku.getCurrentCandidates(firstPos).cardinality(), 2);
		sudoku.startNewBranch(firstPos, 2);
		sudoku.getCurrentCandidates(secondPos).clear();
		sudoku.getCurrentCandidates(secondPos).set(0);
		sudoku.startNewBranch(secondPos, 0);
		assertEquals(sudoku.branchings.size(), 2);
		sudoku.killCurrentBranch();
		assertEquals(sudoku.branchings.size(), 0);
		sudoku.startNewBranch(thirdPos, 0);
		assertEquals(sudoku.getCurrentCandidates(firstPos).cardinality(), 1);
		sudoku.resetCandidates();
	}

	// TODO Tests for a sudoku with at least one constraint behavior that is not
	// the unique one

	@Test(expected = NullPointerException.class)
	public void testNullConstructor() {
		new SolverSudoku(null);
	}

	@Test
	public void testInvalidArguments() {
		sudoku.updateCandidates(null, 1);
		sudoku.setSolution(null, 1);
		sudoku.setSolution(Position.get(1, 0), 7);
		sudoku.setSolution(Position.get(1, 0), -1);
	}

	@Test
	public void testConstraintSaturationChecks() {
		sudoku.setSolution(Position.get(0, 0), 1);
		sudoku.setSolution(Position.get(0, 1), 1);
	}

	@Test
	public void testResetCandidatesStack() {
		sudoku.startNewBranch(Position.get(1, 1), 1);
		sudoku.resetCandidates();
		assertEquals(sudoku.branchings.size(), 0);
		for (Position p : sudoku.positions) {
			if (sudoku.getCell(p).getCurrentValue() != -1) {
				assertEquals(sudoku.getCurrentCandidates(p).cardinality(), 0);
			} else {
				int currentCandidate = -1;
				for (int i = 0; i < sudoku.getCurrentCandidates(p).cardinality(); i++) {
					currentCandidate = sudoku.getCurrentCandidates(p).nextSetBit(currentCandidate + 1);
					for (Constraint c : sudoku.constraints.get(p)) {
						for (Position pos : c) {
							assertFalse(sudoku.getCell(pos).getCurrentValue() == currentCandidate);
						}
					}
				}
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBranchNonExistingPosition() {
		sudoku.startNewBranch(Position.get(10, 4), 1);
	}

	@Test
	public void testAddNegaitveComplexity() {
		sudoku.addComplexityValue(-5, true);
	}

	@Test
	public void testNonUniqueConstraints() {
		// Create new type with a sum constraint
		
		
		SudokuType type = new SudokuType(4, 4, 4);
		type.getConstraints().clear();//TODO dirty da wir nicht wissen dürfen ob getCons nur eine kopie gibt
		//sum constraint
		Constraint c = new Constraint(new SumConstraintBehavior(10), ConstraintType.LINE);
		c.addPosition(Position.get(0, 0));
		c.addPosition(Position.get(1, 0));
		c.addPosition(Position.get(2, 0));
		c.addPosition(Position.get(3, 0));
		type.addConstraint(c);


		SolverSudoku sudoku = new SolverSudoku(new Sudoku(type));
		assertEquals(sudoku.getSudokuType().getNumberOfSymbols(), 4);
		assertEquals(sudoku.getCurrentCandidates(Position.get(0, 0)).cardinality(), 4);

		sudoku.setSolution(Position.get(0, 0), 3);
		sudoku.setSolution(Position.get(1, 0), 2);
		sudoku.startNewBranch(Position.get(2, 0), 3);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(3);
		sudoku.updateCandidates();
		assertEquals(sudoku.getCurrentCandidates(Position.get(3, 0)).cardinality(), 1);
		assertEquals(sudoku.getCurrentCandidates(Position.get(3, 0)).nextSetBit(0), 2);
		sudoku.killCurrentBranch();
		sudoku.setSolution(Position.get(2, 0), 3);
		assertEquals(sudoku.getCurrentCandidates(Position.get(3, 0)).cardinality(), 1);
		assertEquals(sudoku.getCurrentCandidates(Position.get(3, 0)).nextSetBit(0), 2);
		sudoku.setSolution(Position.get(3, 0), 2);
		sudoku.updateCandidates();
		assertTrue(sudoku.getSudokuType().checkSudoku(sudoku));
	}

	@Test(expected = NullPointerException.class)
	public void testStartNewBranchWithoutPosition() {
		sudoku.startNewBranch(null, 1);
	}
}
