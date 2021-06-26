package de.sudoq.model.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.game.GameRepo;
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class FileManagerTests extends TestWithInitCleanforSingletons {

	private static File sudokuDir = new File(Utility.RES + "tmp_suds");

	@Test
	public void testInit() {
		assertTrue(Utility.sudokus.exists());
		assertTrue(Utility.profiles.exists());
		assertTrue(profileManager.getProfilesDir().getAbsolutePath().equals(Utility.profiles.getAbsolutePath()));
		assertTrue(sudokuDir.  getAbsolutePath().equals(Utility.sudokus.getAbsolutePath()));
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
	public void getGameThumbnailFile() throws IOException {

		File profileDir = new File("/tmp/sudoq/FileManagerTests/getGameThumbnailFile/profiles");
		profileDir.mkdirs();
		Utility.clearDir(profileDir);
		Profile p = Profile.Companion.getInstance(profileDir); //needs to be called first otherwise it failes as an indiviidual and sometimes as part of all the tests in this class

		IRepo<SudokuTypeBE> str = new IRepo<SudokuTypeBE>() {
			@Override
			public SudokuTypeBE create() {
				return null;
			}

			@Override
			public SudokuTypeBE read(int id) {
				return null;
			}

			@Override
			public SudokuTypeBE update(SudokuTypeBE sudokuTypeBE) {
				return null;
			}

			@Override
			public void delete(int id) {

			}
		};

		GameRepo gameRepo = new GameRepo(p.getProfilesDir(), p.getCurrentProfileID(), sudokuDir, str);
		assertEquals(1, gameRepo.getNextFreeGameId());//currentProfileID==-1
		assertTrue(FileManager.getGamesFile(p).exists());
		//todo ab hier gehört es in gamerepotest
		File game  = gameRepo.getGameFile(1);
		File thumb = gameRepo.getGameThumbnailFile(1);
		assertEquals(game.getName(),  "game_1.xml");
		assertEquals(thumb.getName(), "game_1.png");
		assertTrue(game. createNewFile());
		assertTrue(thumb.createNewFile());
		//cleanup
		gameRepo.delete(1);
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
