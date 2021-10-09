package de.sudoq.model.game;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import de.sudoq.model.game.GameData;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GameDataTests {


	protected final static String dateFormat = "yyyy:MM:dd HH:mm:ss";

	@Test(expected = NullPointerException.class)
	public void initTestA()	{
		new GameData(0, null, true, SudokuTypes.squigglya, Complexity.difficult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void initTestB()	{
		new GameData(0, new Date(), true, SudokuTypes.squigglya, Complexity.difficult);
	}


	@Test
	public void initTest() {
		GameData g;

		Date d = new Date();

		g = new GameData(0, d, true, SudokuTypes.squigglya, Complexity.difficult);

		assertSame(g.getComplexity(), Complexity.difficult);
		assertSame(g.getType(), SudokuTypes.squigglya);
		assertTrue(g.getId() == 0);
		assertEquals(g.getPlayedAt().toString(), d.toString());
		assertTrue(g.isFinished());
	}
	
	@Test
	public void compareTest() {
		Date d = new Date();
		d.setTime(2);
		GameData gd1 = new GameData(0, d, false, SudokuTypes.squigglya, Complexity.difficult);
		Date d2 = new Date();
		d2.setTime(400000);
		GameData gd2 = new GameData(0, d2, false, SudokuTypes.squigglya, Complexity.difficult);

		assertEquals(gd1.compareTo(gd2), -1);
		
		gd2 =new GameData(0, d2, true, SudokuTypes.squigglya, Complexity.difficult);
		assertEquals(1, gd1.compareTo(gd2));
			
		gd1 = new GameData(0, d, true, SudokuTypes.squigglya, Complexity.difficult);
		gd2 = new GameData(0, d2, false, SudokuTypes.squigglya, Complexity.difficult);
		assertEquals(gd1.compareTo(gd2), -1);
		
		
	}
}