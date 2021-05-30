package de.sudoq.model.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.persistence.xml.game.GameRepo;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;
import de.sudoq.model.xml.SudokuXmlHandler;

public class FileManagerTests extends TestWithInitCleanforSingletons {

	@Test
	public void testInit() {
		assertTrue(Utility.sudokus.exists());
		assertTrue(Utility.profiles.exists());
		assertTrue(profileManager.getProfilesDir().getAbsolutePath().equals(Utility.profiles.getAbsolutePath()));
		assertTrue(FileManager.getSudokuDir().  getAbsolutePath().equals(Utility.sudokus.getAbsolutePath()));
		assertTrue(Utility.sudokus.list().length > 0);
	}



	@Test(expected = IllegalArgumentException.class)
	public void testNullInit() {
		FileManager.initialize(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitNonWriteable() {
		File tmp = new File("tmp");
		tmp.mkdir();
		tmp.setWritable(false);
		assertFalse(tmp.canWrite());

		FileManager.initialize(tmp);

		//tmp.setWritable(true);
		//assertTrue(tmp.delete());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnableToWriteInit() {
		File foo = new File("foo");
		foo.setWritable(false);
		assertFalse(foo.canWrite());
        FileManager.initialize(foo);
	}


    @Test
	public void testSudokuManagement() {
        //assure empty directory
        String p=StringUtils.join(new String[]{Utility.RES,"tmp_suds","standard16x16","difficult"},File.separator);
        try {
            FileUtils.cleanDirectory(new File(p));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(0, FileManager.getSudokuCountOf(SudokuTypes.standard16x16, Complexity.difficult));

        Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard16x16).createSudoku();
		sudoku.setComplexity(Complexity.difficult);
		new SudokuXmlHandler().saveAsXml(sudoku);
		new SudokuXmlHandler().saveAsXml(sudoku);
		assertEquals(2, FileManager.getSudokuCountOf(SudokuTypes.standard16x16, Complexity.difficult));
		assertTrue(FileManager.getRandomSudoku(SudokuTypes.standard16x16, Complexity.difficult).delete());
		assertTrue(FileManager.getRandomSudoku(SudokuTypes.standard16x16, Complexity.difficult).delete());
		assertEquals(0, FileManager.getSudokuCountOf(SudokuTypes.standard16x16, Complexity.difficult));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFalseSudokuMangement() {
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard16x16).createSudoku();
		sudoku.setComplexity(Complexity.difficult);
        FileManager.deleteSudoku(sudoku);
	}

	@Test
	public void testLoadingOfNonexistentSudoku() {
        //assure empty directory
        String p=StringUtils.join(new String[]{Utility.RES,"tmp_suds","samurai","difficult"},File.separator);
        try {
            FileUtils.cleanDirectory(new File(p));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(0, FileManager.getSudokuCountOf(SudokuTypes.samurai, Complexity.difficult));

		File f = FileManager.getRandomSudoku(SudokuTypes.samurai, Complexity.difficult);
		assertNull(f);
	}

//	@Test
//	public void

	@Test
	public void getGameThumbnailFile() throws IOException {

		File profileDir = new File("/tmp/sudoq/FileManagerTests/getGameThumbnailFile/profiles");
		profileDir.mkdirs();
		Utility.clearDir(profileDir);
		Profile p = Profile.Companion.getInstance(profileDir); //needs to be called first otherwise it failes as an indiviidual and sometimes as part of all the tests in this class

		GameRepo gameRepo = new GameRepo(p.getProfilesDir(), p.getCurrentProfileID());
		assertEquals(1, gameRepo.getNextFreeGameId(p));//currentProfileID==-1
		assertTrue(FileManager.getGamesFile(p).exists());
		File game  = gameRepo.getGameFile(1, p);
		File thumb = gameRepo.getGameThumbnailFile(1, p);
		assertEquals(game.getName(),  "game_1.xml");
		assertEquals(thumb.getName(), "game_1.png");
		assertTrue(game. createNewFile());
		assertTrue(thumb.createNewFile());
		assertTrue(gameRepo.deleteGame(1, p));
	}
	

		/*@Test difficult to test now that we no longer give out files commented out for now
	public void testUnwritableProfileFile() {
		profileManager.createProfileFiles(15);
		FileManager.setCurrentProfile(15);
		FileManager.getGamesFile().setWritable(false);
		assertFalse(FileManager.getGamesFile().canWrite());
		try {
			FileManager.createProfileFiles(15);
			fail("No Exception");
		} catch (IllegalStateException e) {
			// fine
		}
		FileManager.getGamesFile().setWritable(true);
		assertTrue(FileManager.getGamesFile().canWrite());
		FileManager.setCurrentProfile(-1);
	}*/


	/* Filemanager now creates a new file in `createProfilesFile`
	   and getProfilesFile() returns a new file-object, so setting it (un)writeable has no effect
	public void testUnwritableProfilesFile() {
        FileManager.getProfilesFile().setWritable(false);
		assertFalse(FileManager.getProfilesFile().canWrite());
		try {
            FileManager.createProfilesFile();
			fail("No Exception");
		} catch (IllegalStateException e) {
			// fine
		}
        FileManager.getProfilesFile().setWritable(true);
		assertTrue(FileManager.getProfilesFile().canWrite());
	}*/

}
