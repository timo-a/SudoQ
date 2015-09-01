package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityConstraintTests;
import de.sudoq.model.sudoku.sudokuTypes.SquigglyBSudokuType9x9;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;
import de.sudoq.model.sudoku.sudokuTypes.TypeSquiggly;

public class SquigglyBSudokuTest extends SquigglySudokuTypesTest {

	TypeBasic squigglyB = new SquigglyBSudokuType9x9();

	
	public void printsession(TypeSquiggly s) {
		for (Constraint c : s) {
			System.out.println(c);
			for (Position p : c)
				System.out.println("    " + p);
		}
	}

	@Override
	public void initialiser() {
		SquigglyBSudokuType9x9 sq = new SquigglyBSudokuType9x9();
		printsession(sq);
		ConstraintTest(sq);
	}

	@Override
	protected void constraintsA(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(0, 0));
		m.add(Position.get(1, 0));
		m.add(Position.get(2, 0));
		m.add(Position.get(3, 0));
		m.add(Position.get(4, 0));
		m.add(Position.get(0, 1));
		m.add(Position.get(1, 1));
		m.add(Position.get(0, 2));
		m.add(Position.get(0, 3));

		assertions(m, c);
	}

	@Override
	protected void constraintsB(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(5, 0));
		m.add(Position.get(6, 0));
		m.add(Position.get(7, 0));
		m.add(Position.get(8, 0));
		m.add(Position.get(7, 1));
		m.add(Position.get(8, 1));
		m.add(Position.get(8, 2));
		m.add(Position.get(8, 3));
		m.add(Position.get(8, 4));

		assertions(m, c);
	}

	@Override
	protected void constraintsC(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(2, 1));
		m.add(Position.get(3, 1));
		m.add(Position.get(4, 1));
		m.add(Position.get(5, 1));
		m.add(Position.get(1, 2));
		m.add(Position.get(2, 2));
		m.add(Position.get(5, 2));
		m.add(Position.get(5, 3));
		m.add(Position.get(6, 3));

		for (Position p : c)
			assertTrue("unvollst. set " + c, m.contains(p));
	}

	@Override
	protected void constraintsD(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(6, 1));
		m.add(Position.get(6, 2));
		m.add(Position.get(7, 2));
		m.add(Position.get(7, 3));
		m.add(Position.get(7, 4));
		m.add(Position.get(5, 5));
		m.add(Position.get(6, 5));
		m.add(Position.get(7, 5));
		m.add(Position.get(5, 6));

		assertions(m, c);
	}

	@Override
	protected void constraintsE(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(3, 2));
		m.add(Position.get(1, 3));
		m.add(Position.get(2, 3));
		m.add(Position.get(3, 3));
		m.add(Position.get(1, 4));
		m.add(Position.get(1, 5));
		m.add(Position.get(1, 6));
		m.add(Position.get(2, 6));
		m.add(Position.get(2, 7));

		assertions(m, c);
	}

	@Override
	protected void constraintsF(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(4, 2));
		m.add(Position.get(4, 3));
		m.add(Position.get(2, 4));
		m.add(Position.get(3, 4));
		m.add(Position.get(4, 4));
		m.add(Position.get(5, 4));
		m.add(Position.get(6, 4));
		m.add(Position.get(4, 5));
		m.add(Position.get(4, 6));

		assertions(m, c);
	}

	@Override
	protected void constraintsG(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(0, 4));
		m.add(Position.get(0, 5));
		m.add(Position.get(0, 6));
		m.add(Position.get(0, 7));
		m.add(Position.get(1, 7));
		m.add(Position.get(0, 8));
		m.add(Position.get(1, 8));
		m.add(Position.get(2, 8));
		m.add(Position.get(3, 8));

		assertions(m, c);
	}

	@Override
	protected void constraintsH(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(2, 5));
		m.add(Position.get(3, 5));
		m.add(Position.get(3, 6));
		m.add(Position.get(6, 6));
		m.add(Position.get(7, 6));
		m.add(Position.get(3, 7));
		m.add(Position.get(4, 7));
		m.add(Position.get(5, 7));
		m.add(Position.get(6, 7));

		assertions(m, c);
	}

	@Override
	protected void constraintsI(Constraint c) {
		List<Position> m = new ArrayList<Position>();
		m.add(Position.get(8, 5));
		m.add(Position.get(8, 6));
		m.add(Position.get(7, 7));
		m.add(Position.get(8, 7));
		m.add(Position.get(4, 8));
		m.add(Position.get(5, 8));
		m.add(Position.get(6, 8));
		m.add(Position.get(7, 8));
		m.add(Position.get(8, 8));

		assertions(m, c);
	}

	@Test
	public void getEnumTypeTests() {
		assertTrue(squigglyB.getEnumType() == SudokuTypes.squigglyb);
	}
	
	
	
	@Test
	public void buildComplexityConstraintTest() {
		
		ComplexityConstraint comCo = squigglyB.buildComplexityConstraint(Complexity.easy);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.easy, 40, 500, 800, 2);

		comCo = squigglyB.buildComplexityConstraint(Complexity.medium);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.medium, 32, 750, 1050, 3);

		comCo = squigglyB.buildComplexityConstraint(Complexity.difficult);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE);

		comCo = squigglyB.buildComplexityConstraint(Complexity.infernal);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE);

		comCo = squigglyB.buildComplexityConstraint(Complexity.arbitrary);
		ComplexityConstraintTests.returnsValues(comCo, Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertTrue(squigglyB.buildComplexityConstraint(null) == null);

	}

	@Test
	public void getAllocationFactorTest() {
		TypeBasic squigglyB = new SquigglyBSudokuType9x9();
		assertTrue(squigglyB.getStandardAllocationFactor() == 0.20f);
	}

}
