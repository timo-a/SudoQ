package de.sudoq.model.solverGenerator.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SumConstraintBehavior;
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

class SolverSudokuTests {

	SolverSudoku sudoku;

    @BeforeEach
    void before() {
		sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
	}

    @Test
    void killBranchWhenThereAreNone() {
        //GIVEN
        SolverSudoku sudoku = new SolverSudoku(new Sudoku(TypeBuilder.get99()));
        //WHEN
        assertThrows(IllegalArgumentException.class, () -> sudoku.killCurrentBranch());
    }

    @Test
    void killBranchShouldRemoveTheGuess() {
        //GIVEN
        Sudoku s = new Sudoku(TypeBuilder.get99());
        Position p = Position.get(5, 7);
        s.getCell(p).toggleNote(2);
        s.getCell(p).toggleNote(3);
        SolverSudoku sudoku = new SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING);

        //WHEN
        sudoku.startNewBranch(p, 2);
        sudoku.killCurrentBranch();

        //THEN
        assertTrue(sudoku.getCurrentCandidates(p).isSet(3)); //3 should remain
        assertFalse(sudoku.getCurrentCandidates(p).isSet(2)); //2 has been proven wrong, should be removed
        assertEquals(1, sudoku.getCurrentCandidates(p).cardinality()); //no other candidates
    }

    @Test
    void killBranchShouldRemoveTheGuess2() {
        //GIVEN
        Sudoku s = new Sudoku(TypeBuilder.get99());
        Position p1 = Position.get(5, 7);
        s.getCell(p1).toggleNote(2);
        s.getCell(p1).toggleNote(3);
        Position p2 = Position.get(8, 4);
        s.getCell(p2).toggleNote(4);
        s.getCell(p2).toggleNote(5);
        SolverSudoku sudoku = new SolverSudoku(s, SolverSudoku.Initialization.USE_EXISTING);

        //WHEN
        sudoku.startNewBranch(p1, 2);
        sudoku.startNewBranch(p2, 4);
        sudoku.killCurrentBranch();

        //THEN
        assertTrue(sudoku.getCurrentCandidates(p2).isSet(5)); //5 should remain
        assertEquals(1, sudoku.getCurrentCandidates(p2).cardinality()); //no other candidates

        //WHEN 2 (I'm too lazy right now, should probably be two tests...)
        sudoku.killCurrentBranch();

        //THEN 2
        assertTrue(sudoku.getCurrentCandidates(p1).isSet(3)); //3 should remain
        assertEquals(1, sudoku.getCurrentCandidates(p1).cardinality()); //no other candidates
    }

    @Test
    void standardSudoku() {
        Position firstPos  = Position.get(5, 7);
        sudoku.getCurrentCandidates(firstPos).clear();
        sudoku.getCurrentCandidates(firstPos).set(2);
        sudoku.getCurrentCandidates(firstPos).set(3);

		Position secondPos = Position.get(8, 4);
        sudoku.getCurrentCandidates(secondPos).clear();
        sudoku.getCurrentCandidates(secondPos).set(0);

		Position thirdPos  = Position.get(3, 2);

        assertFalse(sudoku.hasBranch(), "Verify test initialization: sudoku should have no branch");
		assertEquals(2, sudoku.getCurrentCandidates(firstPos).cardinality());
		sudoku.startNewBranch(firstPos, 2);
        //new branch only one possible candidate
        assertEquals(1, sudoku.getCurrentCandidates(firstPos).cardinality());
        assertFalse(sudoku.getCurrentCandidates(firstPos).get(4), "Other candidate 4 should not be available on new branch");
        assertTrue(sudoku.getCurrentCandidates(firstPos).get(2), "Only 2");

        CandidateSet candidatesOnTopBranch = sudoku.getLastBranch().getCandidates().get(firstPos);
        assertNotSame(candidatesOnTopBranch, sudoku.getCurrentCandidates(firstPos));
        assertTrue(candidatesOnTopBranch.isSet(2));
        assertTrue(candidatesOnTopBranch.isSet(3));


		sudoku.startNewBranch(secondPos, 0);
        assertEquals(2, sudoku.getBranchLevel());
        sudoku.killCurrentBranch();
        assertEquals(1, sudoku.getBranchLevel());
        assertEquals(0, sudoku.getCurrentCandidates(secondPos).cardinality());

        sudoku.killCurrentBranch();
		assertFalse(sudoku.hasBranch());
        assertTrue(sudoku.getCurrentCandidates(firstPos).get(3), "after killing the branch the stashed away possibility should be available again");
        assertFalse(sudoku.getCurrentCandidates(firstPos).get(2), "after killing the branch the wrong guess should no longer be a candidate");
		sudoku.startNewBranch(thirdPos, 0);
		assertEquals(1, sudoku.getCurrentCandidates(firstPos).cardinality());
		sudoku.resetCandidates();
	}

    // TODO Tests for a sudoku with at least one constraint behavior that is not
    // the unique one

    @Test
    void nullConstructor() {
		assertThrows(NullPointerException.class, () -> new SolverSudoku(null));
	}

    @Test
    void invalidArguments() {
		sudoku.updateCandidates(null, 1);
		sudoku.setSolution(null, 1);
		sudoku.setSolution(Position.get(1, 0), 7);
		sudoku.setSolution(Position.get(1, 0), -1);
	}

    @Test
    void constraintSaturationChecks() {
		sudoku.setSolution(Position.get(0, 0), 1);
		sudoku.setSolution(Position.get(0, 1), 1);
	}

    @Test
    void resetCandidatesStack() {
		sudoku.startNewBranch(Position.get(1, 1), 1);
		sudoku.resetCandidates();
		assertFalse(sudoku.hasBranch());
		for (Position p : sudoku.getPositions()) {
			if (sudoku.getCell(p).getCurrentValue() != -1) {
				assertEquals(0, sudoku.getCurrentCandidates(p).cardinality());
			} else {
				int currentCandidate = -1;
				for (int i = 0; i < sudoku.getCurrentCandidates(p).cardinality(); i++) {
					currentCandidate = sudoku.getCurrentCandidates(p).nextSetBit(currentCandidate + 1);
					for (Constraint c : sudoku.getConstraints().get(p)) {
						for (Position pos : c) {
                            assertNotEquals(sudoku.getCell(pos).getCurrentValue(), currentCandidate);
						}
					}
				}
			}
		}
	}

    @Test
    void branchNonExistingPosition() {
		assertThrows(IllegalArgumentException.class, () -> sudoku.startNewBranch(Position.get(10, 4), 1));
	}

    @Test
    void addNegaitveComplexity() {
		sudoku.addComplexityValue(-5, true);
	}

    @Test
    void nonUniqueConstraints() {
		// Create new type with a sum constraint


        SudokuType type = new SudokuType(SudokuTypes.standard4x4, 4, 0f, Position.get(4,4),
                Position.get(1,1), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ComplexityConstraintBuilder());
		type.getConstraints().clear();//TODO dirty da wir nicht wissen dürfen ob getCons nur eine kopie gibt
		//sum constraint
		Constraint c = new Constraint(new SumConstraintBehavior(10), ConstraintType.LINE);
		c.addPosition(Position.get(0, 0));
		c.addPosition(Position.get(1, 0));
		c.addPosition(Position.get(2, 0));
		c.addPosition(Position.get(3, 0));
		type.addConstraint(c);


		SolverSudoku sudoku = new SolverSudoku(new Sudoku(type));
		assertEquals(4, sudoku.getSudokuType().getNumberOfSymbols());
		assertEquals(4, sudoku.getCurrentCandidates(Position.get(0, 0)).cardinality());

		sudoku.setSolution(Position.get(0, 0), 3);
		sudoku.setSolution(Position.get(1, 0), 2);
		sudoku.startNewBranch(Position.get(2, 0), 3);
		sudoku.getCell(Position.get(2, 0)).setCurrentValue(3);
		sudoku.updateCandidates();
		assertEquals(1, sudoku.getCurrentCandidates(Position.get(3, 0)).cardinality());
		assertEquals(2, sudoku.getCurrentCandidates(Position.get(3, 0)).nextSetBit(0));
		sudoku.killCurrentBranch();
		sudoku.setSolution(Position.get(2, 0), 3);
		assertEquals(1, sudoku.getCurrentCandidates(Position.get(3, 0)).cardinality());
		assertEquals(2, sudoku.getCurrentCandidates(Position.get(3, 0)).nextSetBit(0));
		sudoku.setSolution(Position.get(3, 0), 2);
		sudoku.updateCandidates();
		assertTrue(sudoku.getSudokuType().checkSudoku(sudoku));
	}

    @Test
    void startNewBranchWithoutPosition() {
		assertThrows(NullPointerException.class, () -> sudoku.startNewBranch(null, 1));
	}
}
