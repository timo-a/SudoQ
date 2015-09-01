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
	
	@Test
	public void initTest() {
		GameData g;
		try {
			g = new GameData(0, null, true, SudokuTypes.squigglya, Complexity.difficult);
			fail("Not yet implemented");
		} catch (IllegalArgumentException e) {		}
		try {
			g = new GameData(0, "hugo", true, SudokuTypes.squigglya, Complexity.difficult);
			fail("Not yet implemented");
		} catch (IllegalArgumentException e) {		}
		
		Date d = new Date();
		String s = new SimpleDateFormat(dateFormat).format(d);
		
		g = new GameData(0, s, true, SudokuTypes.squigglya, Complexity.difficult);
		
		assertTrue(g.getComplexity() == Complexity.difficult);
		assertTrue(g.getType() == SudokuTypes.squigglya);
		assertTrue(g.getId()==0);
		assertTrue(g.getPlayedAt().toString().equals(d.toString()));
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
		
		assertTrue(gd1.compareTo(gd2) == -1);
		
		gd2 =new GameData(0, s2, true, SudokuTypes.squigglya, Complexity.difficult);
		assertTrue(gd1.compareTo(gd2) == 1);
			
		gd1 = new GameData(0, s, true, SudokuTypes.squigglya, Complexity.difficult);
		gd2 = new GameData(0, s2, false, SudokuTypes.squigglya, Complexity.difficult);
		assertTrue(gd1.compareTo(gd2) == -1);
		
		
	}
}