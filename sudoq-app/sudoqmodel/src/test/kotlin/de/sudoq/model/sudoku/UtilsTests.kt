package de.sudoq.model.sudoku;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UtilsTests {

	/**
	 *
	 */
	@Test
	public void classifyTest() {

		List<Position> positionList = new ArrayList();
		positionList.add(new Position(2,4));
		positionList.add(new Position(2,5));
		positionList.add(new Position(2,6));
		positionList.add(new Position(2,7));
		positionList.add(new Position(2,9));
		assertEquals("Column constraint is not recognized as such"
				,Utils.ConstraintShape.Column
				,UtilsKt.getGroupShape(positionList));
		assertEquals("Column constraint is not recognized as such"
				,"col 3"
				,Utils.classifyGroup(positionList));

		//Row
		positionList.clear();
		positionList.add(new Position(2,6));
		positionList.add(new Position(3,6));
		positionList.add(new Position(4,6));
		positionList.add(new Position(5,6));
		assertEquals("Row constraint is not recognized as such"
				,Utils.ConstraintShape.Row
				,UtilsKt.getGroupShape(positionList));
		assertEquals("Row constraint is not recognized as such"
				,"row 7"
				,Utils.classifyGroup(positionList));


		//diagonal
		positionList.clear();
		positionList.add(new Position(2,4));
		positionList.add(new Position(3,5));
		positionList.add(new Position(4,6));
		positionList.add(new Position(5,7));
		assertEquals("Diagonal constrant is not recognized as such"
				,Utils.ConstraintShape.Diagonal
				,UtilsKt.getGroupShape(positionList));
		assertEquals("Diagonal constrant is not recognized as such"
				,"a diagonal"
				,Utils.classifyGroup(positionList));

		//diagonal
		positionList.clear();
		positionList.add(new Position(2,4));
		positionList.add(new Position(2,5));
		positionList.add(new Position(3,4));
		positionList.add(new Position(3,5));
		assertEquals("Block constrant is not recognized as such"
				,Utils.ConstraintShape.Block
				,UtilsKt.getGroupShape(positionList));
		assertEquals("Block constrant is not recognized as such"
				,"a block containing (3, 5)"
				,Utils.classifyGroup(positionList));

	} 
}