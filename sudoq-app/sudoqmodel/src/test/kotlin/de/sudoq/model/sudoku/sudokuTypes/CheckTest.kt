package de.sudoq.model.sudoku.sudokuTypes;

import kotlin.collections.get
import kotlin.text.get

org.junit.Assert.assertFalse;
org.junit.Assert.assertTrue;
org.junit.Assert.fail;

import org.junit.Test;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class TypeBasicTests {

	int[] su1 = { 9, 5, 8, 3, 1, 2, 7, 6, 4
	            , 4, 6, 1, 5, 7, 9, 8, 2, 3
	            , 3, 7, 2, 4, 6, 8, 9, 5, 1
	            , 8, 9, 6, 1, 2, 3, 5, 4, 7
	            , 1, 4, 3, 7, 9, 5, 2, 8, 6
	            , 5, 2, 7, 6, 8, 4, 3, 1, 9
	            , 7, 8, 5, 9, 4, 1, 6, 3, 2
	            , 2, 1, 9, 8, 3, 6, 4, 7, 5
	            , 6, 3, 4, 2, 5, 7, 1, 9, 8 };



	@Test
	public void Checktest() {
		for (int i = 0; i < su1.length; i++) {
			su1[i]--;
		}
		assertTrue(su1[0] == 8);

        SudokuType s99 = TypeBuilder.getType(SudokuTypes.standard9x9);

        PositionMap<Integer> map = new PositionMap<>(Position.get(9, 9), s99.getValidPositions(),
                p -> su1[p.getY() * 9 + p.getX()]);

		Sudoku sudoku1 = new Sudoku(s99, map, new PositionMap<>(Position.get(9, 9)));
		for (Cell f : sudoku1)
			f.setCurrentValue(f.getSolution());
		assertTrue(sudoku1.getSudokuType().checkSudoku(sudoku1));
		sudoku1.getCell(Position.get(0, 0)).setCurrentValue(5);
		assertFalse(sudoku1.getSudokuType().checkSudoku(sudoku1));

	}

	@Test
	public void toStringTest() {
		SudokuType hy = TypeBuilder.getType(SudokuTypes.HyperSudoku);
		assertTrue(hy.toString().equals(SudokuTypes.HyperSudoku.toString()));
	}

}

/*class TestSudoku extends TypeBasic {

	public TestSudoku(int a, int b) {
		super(a, b);
	}

	@Override
	public SudokuTypes getEnumType() {
		return null;
	}

	@Override
	public ComplexityConstraint buildComplexityConstraint(Complexity complexity) {
		return null;
	}

	@Override
	public float getStandardAllocationFactor() {
		return 0;
	}

}*/