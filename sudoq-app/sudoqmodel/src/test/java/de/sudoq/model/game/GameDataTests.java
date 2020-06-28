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

	@Test(expected = IllegalArgumentException.class)
	public void initTestA()	{
		new GameData(0, null, true, SudokuTypes.squigglya, Complexity.difficult);
		//apparently not yet implemented
	}

	@Test(expected = IllegalArgumentException.class)
	public void initTestB()	{
		new GameData(0, "hugo", true, SudokuTypes.squigglya, Complexity.difficult);
		//apparently not yet implemented
	}


	@Test
	public void initTest() {
		GameData g;

		Date d = new Date();
		String s = new SimpleDateFormat(dateFormat).format(d);
		
		g = new GameData(0, s, true, SudokuTypes.squigglya, Complexity.difficult);

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
		String s = new SimpleDateFormat(dateFormat).format(d);
		GameData gd1 = new GameData(0, s, false, SudokuTypes.squigglya, Complexity.difficult);
		Date d2 = new Date();
		d2.setTime(400000);
		String s2 = new SimpleDateFormat(dateFormat).format(d2);
		GameData gd2 = new GameData(0, s2, false, SudokuTypes.squigglya, Complexity.difficult);

		assertEquals(gd1.compareTo(gd2), -1);
		
		gd2 =new GameData(0, s2, true, SudokuTypes.squigglya, Complexity.difficult);
		assertEquals(1, gd1.compareTo(gd2));
			
		gd1 = new GameData(0, s, true, SudokuTypes.squigglya, Complexity.difficult);
		gd2 = new GameData(0, s2, false, SudokuTypes.squigglya, Complexity.difficult);
		assertEquals(gd1.compareTo(gd2), -1);
		
		
	}
}