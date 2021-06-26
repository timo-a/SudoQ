package de.sudoq.model.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.persistence.xml.profile.ProfileRepo;
import de.sudoq.model.persistence.xml.profile.ProfilesListRepo;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class XmlHandlerTests {

	private static File sudokus;
	private static File profiles;
	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	@Before
	public void init() {
		profiles = new File("res" + File.separator + "tmp_profiles");
		profiles.mkdir();
		Profile.Companion.getInstance(profiles).setProfileRepo(new ProfileRepo(profiles));
		Profile.Companion.getInstance(profiles).setProfilesListRepo( new ProfilesListRepo(profiles));
		Profile.Companion.getInstance(profiles).setProfilesDir(profiles);

		sudokus = new File("res" + File.separator + "tmp_suds");
		sudokus.mkdir();
		FileManager.initialize(sudokus);
	}

	@After
	public void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Utility.deleteDir(profiles);
		Utility.deleteDir(sudokus);
	}


	@Test
	public void testSaveAsXmlStringIllegalArgumentException() throws SecurityException, IllegalArgumentException, IOException, NoSuchFieldException, IllegalAccessException {
		class Foo implements Xmlable {
			public XmlTree toXmlTree() {
				return new XmlTree("sudoku");
			}

			public void fillFromXml(XmlTree xmlTreeRepresentation) {
			}
		}

		XmlHandler<Foo> handler = new XmlHandler<Foo>() {
			@Override
			protected File getFileFor(Foo obj) {
				File file = new File("foo");
				try {
					file.createNewFile();
				} catch (IOException e) {
					fail("cannot write");
				}
				file.setWritable(false);
				return file;
			}
		};
		assertFalse(handler.getFileFor(new Foo()).canWrite());

		try {
			handler.saveAsXml(new Foo());
			fail("No Exception");
		} catch (IllegalArgumentException e) {
			// great
		}

		assertTrue(new File("foo").delete());
	}

}
