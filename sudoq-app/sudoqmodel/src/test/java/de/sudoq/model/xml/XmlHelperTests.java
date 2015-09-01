package de.sudoq.model.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;

public class XmlHelperTests {

	private XmlHelper helper;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void initTests() {

		helper = new XmlHelper();
	}

	@Test
	public void testLoadXml() throws FileNotFoundException, IllegalArgumentException, IOException {

		XmlTree sudokuTree = helper.loadXml(new File("res/sudoku_example.xml"));
		// helper.buildXmlStructure(sudokuTree);

		assertEquals(sudokuTree.getNumberOfChildren(), 2);
		assertEquals(sudokuTree.getName(), "sudoku");

		XmlTree gameTree = helper.loadXml(new File("res/game_example.xml"));
		// helper.buildXmlStructure(gameTree);

		assertEquals(gameTree.getNumberOfChildren(), 4);

		XmlTree gamesTree = helper.loadXml(new File("res/games_example.xml"));
		// helper.buildXmlStructure(gamesTree);

		assertEquals(gamesTree.getNumberOfChildren(), 1);

	}

	@Test
	public void testLoadXmlFileNotFoundException() throws IllegalArgumentException, IOException {
		thrown.expect(FileNotFoundException.class);
		helper.loadXml(new File("res/not_existing_imaginary_file.xml"));
	}

	@Test
	public void testLoadXmlIOException() throws IOException {
		thrown.expect(IOException.class);
		helper.loadXml(new File("res/compromised.xml"));
	}

	@Test
	public void testLoadXmlIllegalArgumentException() throws IllegalArgumentException, FileNotFoundException, IOException {
		thrown.expect(IllegalArgumentException.class);
		helper.loadXml(null);
	}

	//TODO test content. it does not seem to bee working
	
	@Test
	public void testSaveXml() throws FileNotFoundException, IllegalArgumentException, IOException {

		File testFile = new File("res/tmp.xml");
		testFile.setWritable(true);
		File testFile2 = new File("res/tmp2.xml");

		XmlTree sudoku = new XmlTree("sudoku");
		sudoku.addAttribute(new XmlAttribute("id", "7845"));
		sudoku.addAttribute(new XmlAttribute("type", "6"));
		sudoku.addAttribute(new XmlAttribute("complexity", "3"));

		XmlTree fieldmap1 = new XmlTree("fieldmap");
		fieldmap1.addAttribute(new XmlAttribute("editable", "true"));
		fieldmap1.addAttribute(new XmlAttribute("solution", "9"));
		XmlTree position1 = new XmlTree("position");
		position1.addAttribute(new XmlAttribute("x", "1"));
		position1.addAttribute(new XmlAttribute("y", "8"));
		fieldmap1.addChild(position1);
		sudoku.addChild(fieldmap1);

		XmlTree fieldmap2 = new XmlTree("fieldmap");
		fieldmap2.addAttribute(new XmlAttribute("editable", "true"));
		fieldmap2.addAttribute(new XmlAttribute("solution", "4"));
		XmlTree position2 = new XmlTree("position");
		position2.addAttribute(new XmlAttribute("x", "2"));
		position2.addAttribute(new XmlAttribute("y", "6"));
		fieldmap2.addChild(position2);
		sudoku.addChild(fieldmap2);

		System.out.println(helper.buildXmlStructure(sudoku));

		helper.saveXml(sudoku, testFile);

		XmlTree sudokuTest = helper.loadXml(testFile);

		System.out.println("------------------------------------------");
		System.out.println(helper.buildXmlStructure(sudokuTest));

		assertEquals(sudokuTest.getName(), sudoku.getName());
		assertEquals(sudokuTest.getNumberOfChildren(), sudoku.getNumberOfChildren());

		int i = 0;
		for (Iterator<XmlTree> iterator = sudokuTest.getChildren(); iterator.hasNext();) {
			XmlTree sub = iterator.next();
			if (i == 0) {
				assertEquals(sub.getAttributeValue("solution"), "9");
			} else if (i == 1) {
				assertEquals(sub.getAttributeValue("solution"), "4");
			}
			assertEquals(sub.getAttributeValue("editable"), "true");
			assertEquals(sub.getName(), "fieldmap");
			assertEquals(sub.getNumberOfChildren(), 1);
			i++;
		}
		assertEquals(i, 2);

		XmlTree atomicTest = new XmlTree("sudoku", "xyz");
		helper.saveXml(atomicTest, testFile2);

	}

	@Test
	public void testSaveXmlIOException() throws IllegalArgumentException, IOException {
		thrown.expect(IOException.class);
		File file = new File("res/tmp.xml");
		file.setWritable(false);
		// this test will fail if you use linux and the file gets created on a
		// ntfs partition.
		// seems that the java file implementation uses linux tools like chmod -
		// chmod doesnt work on ntfs...
		assertFalse(file.canWrite());
		helper.saveXml(new XmlTree("sudoku", ""), file);
	}

	@Test
	public void testSaveXmlIllegalArgumentException1() throws IllegalArgumentException, IOException {
		thrown.expect(IllegalArgumentException.class);
		helper.saveXml(new XmlTree("sudoku", ""), null);
	}

	@Test
	public void testSaveXmlIllegalArgumentException2() throws IllegalArgumentException, IOException {
		thrown.expect(IllegalArgumentException.class);
		helper.saveXml(null, new File("res/tmp.xml"));
	}

	@Test
	public void testSaveXmlIllegalArgumentException3() throws IllegalArgumentException, IOException {
		thrown.expect(IllegalArgumentException.class);
		helper.saveXml(null, null);
	}

	@Test
	public void testSaveXmlIllegalArgumentException4() throws IllegalArgumentException, IOException {
		thrown.expect(IllegalArgumentException.class);
		helper.saveXml(new XmlTree("name", ""), new File("res/tmp.xml"));
	}

	@Test
	public void testBuildXmlStructureIllegalArgumentException() throws IllegalArgumentException {
		thrown.expect(IllegalArgumentException.class);
		helper.buildXmlStructure(null);
	}

}
