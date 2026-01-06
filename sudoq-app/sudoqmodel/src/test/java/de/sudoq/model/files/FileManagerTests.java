package de.sudoq.model.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.ports.persistence.ReadRepo;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.utility.FileManager;

public class FileManagerTests {

	private static File sudokuDir = new File(Utility.RES + "tmp_suds");

	//@Test
	public void testInit() {
		assertTrue(Utility.sudokus.exists());
		assertTrue(Utility.profiles.exists());
        //assertTrue(profileManager.getProfilesDir().getAbsolutePath().equals(Utility.profiles.getAbsolutePath()));
        assertEquals(sudokuDir.getAbsolutePath(), Utility.sudokus.getAbsolutePath());
		assertTrue(Utility.sudokus.list().length > 0);
	}


    @Test
    void nullInit() {
		assertThrows(IllegalArgumentException.class, () -> FileManager.initialize(null));
	}

    @Test
    void initNonWriteable() {
		File tmp = new File("tmp");
		tmp.mkdir();
		tmp.setWritable(false);
		assertFalse(tmp.canWrite());

		assertThrows(IllegalArgumentException.class, () -> FileManager.initialize(tmp));
	}

    @Test
    void unableToWriteInit() {
		File foo = new File("foo");
		foo.setWritable(false);
		assertFalse(foo.canWrite());
		assertThrows(IllegalArgumentException.class, () -> FileManager.initialize(foo));
	}


    @Test
    void getGameThumbnailFile() throws Exception {

		File profileDir = new File("/tmp/sudoq/FileManagerTests/getGameThumbnailFile/profiles");
		profileDir.mkdirs();
		Utility.clearDir(profileDir);
		//ProfileSingleton p = ProfileSingleton.Companion.getInstance(profileDir); //needs to be called first otherwise it failes as an indiviidual and sometimes as part of all the tests in this class

		ReadRepo<SudokuType> str = id -> null;

		//GameRepo gameRepo = new GameRepo(p.getProfilesDir(), p.getCurrentProfileID(), str);
		//assertEquals(1, gameRepo.getNextFreeGameId());//currentProfileID==-1
		//assertTrue(FileManager.getGamesFile(p).exists());
		//todo ab hier gehört es in gamerepotest
		//File game  = gameRepo.getGameFile(1);
		//File thumb = gameRepo.getGameThumbnailFile(1);
		//assertEquals(game.getName(),  "game_1.xml");
		//assertEquals(thumb.getName(), "game_1.png");
		//assertTrue(game. createNewFile());
		//assertTrue(thumb.createNewFile());
		//cleanup
		//gameRepo.delete(1);
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
