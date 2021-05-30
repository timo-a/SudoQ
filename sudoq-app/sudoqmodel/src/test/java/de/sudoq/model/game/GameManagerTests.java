package de.sudoq.model.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.persistence.xml.game.GameRepo;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GameManagerTests {

	static File profileDir = new File("/tmp/sudoq/GameManagerTests/profile");
	static Profile p;

	@BeforeClass
	public static void init() throws IOException {
		//TestWithInitCleanforSingletons.legacyInit();

		profileDir.mkdirs();
		Utility.clearDir(profileDir);
		p = Profile.Companion.getInstance(profileDir);
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		/*
        java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
        f.setAccessible(true);
        f.set(null, null);
        java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
        s.setAccessible(true);
        s.set(null, null);
        Utility.deleteDir(Utility.profiles);
        Utility.deleteDir(Utility.sudokus);
        */
		Utility.clearDir(profileDir);
    }

	@Test
	public void testDeletingCurrentGame() {
		GameManager gm = GameManager.Companion.getInstance(profileDir);
		Game game = gm.newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		p.setCurrentGame(game.getId());
		gm.deleteGame(p.getCurrentGame(), p);
		assertEquals(Profile.NO_GAME, p.getCurrentGame());
	}

	@After
	public void deleteAllGames() {
		GameRepo gr = new GameRepo(profileDir, p.getCurrentProfileID());
		for (int i = 1; i <= gr.getGamesDir(p).list().length; i++) {
			gr.deleteGame(i, p);
		}
		GameManager.Companion.getInstance(profileDir).updateGamesList();
	}

	@Test
	public void testCreatingAndSolving() {
		Game game = GameManager.Companion.getInstance(profileDir).newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		assertFalse(game.isFinished());
		int count = 0;
		for (Cell f : game.getSudoku()) {
			if (f.isNotSolved()) {
				if (count == 0) {
					game.solveCell(f);
					assertTrue(f.isSolvedCorrect());
				}
				count++;
			}
		}
		game.solveCell();
		for (Cell f : game.getSudoku()) {
			if (f.isNotSolved()) {
				count--;
			}
		}
		assertEquals(count, 2);
		game.solveAll();
		assertTrue(game.isFinished());

	}

	@Test
	public void testAssistanceSetting() {
		GameSettings set = new GameSettings();
		set.setAssistance(Assistances.autoAdjustNotes);
		Game game = GameManager.Companion.getInstance(profileDir).newGame(SudokuTypes.standard9x9, Complexity.difficult, set);
		assertTrue(game.isAssistanceAvailable(Assistances.autoAdjustNotes));
		assertFalse(game.isAssistanceAvailable(Assistances.markRowColumn));
		game.addTime(50);
		game.solveAll();
		assertEquals(0, game.getScore());
	}

	@Test
	public void testLoadingAndSaving() {
		GameManager gm = GameManager.Companion.getInstance(profileDir);
		Game game = gm.newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		gm.save(game, p);
		assertEquals(gm.load(game.getId()), game);
	}

	@Test
	public void testGameList() {
		GameManager gm = GameManager.Companion.getInstance(profileDir);

		Game game1 = gm.newGame(SudokuTypes.standard9x9, Complexity.easy, new GameSettings());
		gm.save(game1, p);
		Game game2 = gm.newGame(SudokuTypes.standard9x9, Complexity.medium, new GameSettings());
		gm.save(game2, p);
		Game game3 = gm.newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		gm.save(game3, p);
		assertEquals(gm.getGameList().size(), 3);
		game3.solveAll();
		assertTrue(game3.isFinished());
		gm.save(game3, p);
		gm.deleteFinishedGames();
		assertEquals(gm.getGameList().size(), 2);
		gm.deleteGame(game2.getId(), p);
		assertEquals(gm.getGameList().size(), 1);
	}

	@Test
	public void testSudokuLoading() {
		GameManager gm = GameManager.Companion.getInstance(profileDir);
		Game game = gm.newGame(SudokuTypes.standard9x9, Complexity.medium, new GameSettings());
		int id = game.getId();
		Cell cell = null;
		for (Cell f : game.getSudoku()) {
			if (f.isNotSolved()) {
				cell = f;
				break;
			}
		}
		assertNotNull(cell);
		assertTrue(game.solveCell());
		gm.save(game, p);
		assertTrue(game.equals(gm.load(id)));
		game.solveAll();
		gm.save(game, p);
		gm.deleteFinishedGames();
		assertEquals(0, gm.getGameList().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnvalidLoadingIds() {
		GameManager.Companion.getInstance(profileDir).load(-5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoadingNonexistentGame() {
		GameManager.Companion.getInstance(profileDir).load(2);
	}

	@Test
	public void testManipulationGamesXml() {
		FileManager.getGamesFile(p).setWritable(false);
		assertFalse(FileManager.getGamesFile(p).canWrite());
		try {
			GameManager.Companion.getInstance(profileDir).deleteFinishedGames();
			fail("No Exception");
		} catch (IllegalStateException e) {
			// fine
		}
		FileManager.getGamesFile(p).setWritable(true);
		assertTrue(FileManager.getGamesFile(p).canWrite());
	}

	@Test
	public void testDeletingGamesXml() {
		File other = new File(FileManager.getGamesFile(p).getParentFile(), "foo");
		FileManager.getGamesFile(p).renameTo(new File(FileManager.getGamesFile(p).getParentFile(), "foo"));
		try {
			GameManager.Companion.getInstance(profileDir).deleteFinishedGames();
			fail("No Exception");
		} catch (IllegalStateException e) {
			other.renameTo(FileManager.getGamesFile(p));
		}
	}

}
