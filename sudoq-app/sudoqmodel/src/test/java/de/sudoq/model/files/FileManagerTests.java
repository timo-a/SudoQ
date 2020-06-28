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

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;
import de.sudoq.model.xml.SudokuXmlHandler;

public class FileManagerTests {

	@BeforeClass
	public static void init() {
		Utility.copySudokus();
		System.out.println("hu - HA!");
		//Profile.getInstance();
        /*try {
			FileManager.deleteDir(Utility.profiles);
		} catch (IOException e) {
        	//e.printStackTrace();
        	fail("ioexception");
		}*/


	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
		f.setAccessible(true);
		f.set(null, null);
		java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
		s.setAccessible(true);
		s.set(null, null);
		java.lang.reflect.Field p = Profile.class.getDeclaredField("instance");
		p.setAccessible(true);
		p.set(null, null);
		FileManager.deleteDir(Utility.profiles);
		FileManager.deleteDir(Utility.sudokus);
	}

	@Test
	public void testInit() {
		assertTrue(Utility.sudokus.exists());
		assertTrue(Utility.profiles.exists());
		assertTrue(FileManager.getProfilesDir().getAbsolutePath().equals(Utility.profiles.getAbsolutePath()));
		assertTrue(FileManager.getSudokuDir().  getAbsolutePath().equals(Utility.sudokus.getAbsolutePath()));
		assertTrue(Utility.sudokus.list().length > 0);
	}



	@Test(expected = IllegalArgumentException.class)
	public void testNullInit() {
		FileManager.initialize(null, null);
	}

	@Test
	public void testFalseInit() {
		File tmp = new File("tmp");
		tmp.mkdir();
		tmp.setWritable(false);
		assertFalse(tmp.canWrite());

		try {
            FileManager.initialize(tmp, null);
			fail("No Exception");
		} catch (IllegalArgumentException e) {
			// fine
		}
		try {
            FileManager.initialize(Utility.profiles, tmp);
			fail("No Exception");
		} catch (IllegalArgumentException e) {
			// fine
		}

		tmp.setWritable(true);
		assertTrue(tmp.delete());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnableToWriteInit() {
		File foo = new File("foo");
		foo.setWritable(false);
		assertFalse(foo.canWrite());
        FileManager.initialize(foo, foo);
	}

    @Test
	public void testProfiles() {
		if (FileManager.getProfilesDir().exists())
			for (File f : FileManager.getProfilesDir().listFiles())
				f.delete();


		assertFalse(FileManager.getProfilesFile().exists());
		assertEquals(0, FileManager.getNumberOfProfiles());
		Profile.getInstance();
		assertTrue(FileManager.getProfilesFile().exists());
		File profile = FileManager.getProfileXmlFor(1);
		assertTrue(profile.exists() && !profile.isDirectory());
		assertEquals(1, FileManager.getNumberOfProfiles());
		assertEquals(1, Integer.parseInt(profile.getParentFile().getName().substring(8)));

		FileManager.createProfileFiles(2);
		assertEquals(2, FileManager.getNumberOfProfiles());
		FileManager.deleteProfile(2);
		assertEquals(1, FileManager.getNumberOfProfiles());
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


	@Test
	public void getGameThumbnailFile() throws IOException {


		Profile.getInstance(); //needs to be called first otherwise it failes as an indiviidual and sometimes as part of all the tests in this class

		assertEquals(1, FileManager.getNextFreeGameId());//currentProfileID==-1
		assertTrue(FileManager.getGamesFile().exists());
		File game  = FileManager.getGameFile(1);
		File thumb = FileManager.getGameThumbnailFile(1);
		assertEquals(game.getName(),  "game_1.xml");
		assertEquals(thumb.getName(), "game_1.png");
		assertTrue(game. createNewFile());
		assertTrue(thumb.createNewFile());
		assertTrue(FileManager.deleteGame(1));
	}
	
	@Test
	public void testGetCurrentGestureFile() {
		assertNotNull(FileManager.getCurrentGestureFile());
	}

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

	@Test
	public void testUnwritableProfileFile() {
        FileManager.createProfileFiles(15);
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
	}
}
