package de.sudoq.model.sudoku.sudokuTypes;

import org.junit.Test;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SymbolIteratorTest {

	SudokuType sst;

	@Test
	public void test4x4(){
		sst = TypeBuilder.getType(SudokuTypes.standard4x4);
		testEquality(sst);
	}

	@Test
	public void test9x9(){
		sst = TypeBuilder.getType(SudokuTypes.standard9x9);
		testEquality(sst);
	}

	@Test
	public void test6x6(){
		sst = TypeBuilder.getType(SudokuTypes.standard6x6);
		testEquality(sst);
	}

	@Test
	public void test16x16(){
		sst = TypeBuilder.getType(SudokuTypes.standard16x16);
		testEquality(sst);
	}

	@Test
	public void testArbitrary(){
		sst = TypeBuilder.getType(SudokuTypes.standard4x4);
		for(int i : new int[]{1,2,3,20,100}){
			sst.setNumberOfSymbols(i);
			testEquality(sst);
		}
	}

	private void testEquality(SudokuType sst) {
		int counter = 0;
		for (int i:
			 sst.getSymbolIterator()) {
			assertEquals(counter++, i);
		}
		assertEquals(sst.getNumberOfSymbols(), counter);
		Position p = sst.getSize();
	}



}
