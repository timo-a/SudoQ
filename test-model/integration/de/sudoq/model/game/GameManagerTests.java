package de.sudoq.model.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GameManagerTests {

	private static File profiles = new File("res/tmp_profiles");
	private static File sudokus = new File("res/tmp_sudokus");

	@BeforeClass
	public static void init() throws IOException {
		profiles.mkdir();
		sudokus.mkdir();
		copyDirectory(new File(".." + File.separator + "sudokus"), sudokus);
		FileManager.initialize(profiles, sudokus);
		Profile.getInstance();
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
		f.setAccessible(true);
		f.set(null, null);
		java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
		s.setAccessible(true);
		s.set(null, null);
		java.lang.reflect.Field p = Profile.class.getDeclaredField("instance");
		p.setAccessible(true);
		p.set(null, null);
		FileManager.deleteDir(profiles);
		FileManager.deleteDir(sudokus);
	}

	private static void copyDirectory(File src, File target) throws IOException {
		for (File subFile : src.listFiles()) {
			File targetSubFile = new File(target, subFile.getName());
			if (!targetSubFile.exists() && !subFile.isDirectory()) {
				InputStream in = null;
				OutputStream out = null;
				in = new FileInputStream(subFile);
				out = new FileOutputStream(targetSubFile);
				copyFile(in, out);
				in.close();
				out.flush();
				out.close();
			} else {
				targetSubFile.mkdir();
				copyDirectory(subFile, targetSubFile);
			}
		}
	}

	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	@Test
	public void testDeletingCurrentGame() {
		Game game = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		Profile.getInstance().setCurrentGame(game.getId());
		GameManager.getInstance().deleteGame(Profile.getInstance().getCurrentGame());
		assertEquals(Profile.NO_GAME, Profile.getInstance().getCurrentGame());
	}

	@After
	public void deleteAllGames() {
		for (int i = 1; i <= FileManager.getGamesDir().list().length; i++) {
			FileManager.deleteGame(i);
		}
		GameManager.getInstance().updateGamesList();
	}

	@Test
	public void testCreatingAndSolving() {
		Game game = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		assertFalse(game.isFinished());
		int count = 0;
		for (Field f : game.getSudoku()) {
			if (f.isEmpty()) {
				if (count == 0) {
					game.solveField(f);
					assertTrue(f.isSolvedCorrect());
				}
				count++;
			}
		}
		game.solveField();
		for (Field f : game.getSudoku()) {
			if (f.isEmpty()) {
				count--;
			}
		}
		assertEquals(count, 2);
		game.solveAll();
		assertTrue(game.isFinished());

	}

	@Test
	public void testAssitenceSetting() {
		GameSettings set = new GameSettings();
		set.setAssistance(Assistances.autoAdjustNotes);
		Game game = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.difficult, set);
		assertTrue(game.isAssistanceAvailable(Assistances.autoAdjustNotes));
		assertFalse(game.isAssistanceAvailable(Assistances.markRowColumn));
		game.addTime(50);
		game.solveAll();
		assertEquals(game.getScore(), 0);
	}

	public void testLoadingAndSaving() {
		Game game = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		GameManager.getInstance().save(game);
		assertTrue(GameManager.getInstance().load(game.getId()).equals(game));
	}

	@Test
	public void testGameList() {
		Game game1 = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.easy, new GameSettings());
		GameManager.getInstance().save(game1);
		Game game2 = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.medium, new GameSettings());
		GameManager.getInstance().save(game2);
		Game game3 = GameManager.getInstance().newGame(SudokuTypes.standard9x9, Complexity.difficult, new GameSettings());
		GameManager.getInstance().save(game3);
		assertEquals(GameManager.getInstance().getGameList().size(), 3);
		game3.solveAll();
		assertTrue(game3.isFinished());
		GameManager.getInstance().save(game3);
		GameManager.getInstance().deleteFinishedGames();
		assertEquals(GameManager.getInstance().getGameList().size(), 2);
		GameManager.getInstance().deleteGame(game2.getId());
		assertEquals(GameManager.getInstance().getGameList().size(), 1);
	}

	@Test
	public void testSudokuLoading() {
		GameManager gm = GameManager.getInstance();
		Game game = gm.newGame(SudokuTypes.standard9x9, Complexity.medium, new GameSettings());
		int id = game.getId();
		Field field = null;
		for (Field f : game.getSudoku()) {
			if (f.isEmpty()) {
				field = f;
				break;
			}
		}
		assertNotNull(field);
		assertTrue(game.solveField());
		gm.save(game);
		assertTrue(game.equals(gm.load(id)));
		game.solveAll();
		gm.save(game);
		gm.deleteFinishedGames();
		assertEquals(0, gm.getGameList().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnvalidLoadingIds() {
		GameManager.getInstance().load(-5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoadingNonexistentGame() {
		GameManager.getInstance().load(2);
	}

	@Test
	public void testManipulationGamesXml() {
		FileManager.getGamesFile().setWritable(false);
		assertFalse(FileManager.getGamesFile().canWrite());
		try {
			GameManager.getInstance().deleteFinishedGames();
			fail("No Exception");
		} catch (IllegalStateException e) {
			// fine
		}
		FileManager.getGamesFile().setWritable(true);
		assertTrue(FileManager.getGamesFile().canWrite());
	}

	@Test
	public void testDeletingGamesXml() {
		File other = new File(FileManager.getGamesFile().getParentFile(), "foo");
		FileManager.getGamesFile().renameTo(new File(FileManager.getGamesFile().getParentFile(), "foo"));
		try {
			GameManager.getInstance().deleteFinishedGames();
			fail("No Exception");
		} catch (IllegalStateException e) {
			other.renameTo(FileManager.getGamesFile());
		}
	}

}
