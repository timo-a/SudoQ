package de.sudoq.model.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;

public class XmlTreeTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConstructorStringIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		new XmlTree(null);
	}

	@Test
	public void testConstructorStringStringIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		new XmlTree("root", null);
	}

	@Test
	public void testGetName() {
		XmlTree tree = new XmlTree("xyz");
		assertEquals(tree.getName(), "xyz");
	}

	@Test
	public void testGetContent() {
		XmlTree tree = new XmlTree("xyz", "some content");
		assertEquals(tree.getContent(), "some content");
	}

	@Test
	public void testGetNumberOfChildren() {
		XmlTree tree = new XmlTree("root");
		tree.addChild(new XmlTree("sub"));
		tree.addChild(new XmlTree("sub"));
		assertEquals(tree.getNumberOfChildren(), 2);
	}

	@Test
	public void testGetAttributeValue() {
		XmlTree tree = new XmlTree("root");
		XmlAttribute attribute = new XmlAttribute("xyzName", "xyzValue");
		tree.addAttribute(attribute);

		assertEquals(tree.getAttributeValue("xyzName"), "xyzValue");
		assertNull(tree.getAttributeValue("notExistingAttribute"));
	}

	@Test
	public void testGetAttributeValueIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		new XmlTree("root").getAttributeValue(null);
	}

	@Test
	public void testGetAttributes() {
		XmlTree tree = new XmlTree("root");
		XmlAttribute attribute1 = new XmlAttribute("attribute1", "value1");
		XmlAttribute attribute2 = new XmlAttribute("attribute2", "value2");
		tree.addAttribute(attribute1);
		tree.addAttribute(attribute2);
		tree.addAttribute(attribute2);

		int i = 0;
		for (Iterator<XmlAttribute> iterator = tree.getAttributes(); iterator.hasNext();) {
			XmlAttribute attribute = iterator.next();
			if (i == 0) {
				assertEquals(attribute.getName(), "attribute1");
				assertEquals(attribute.getValue(), "value1");
			} else if (i == 1) {
				assertEquals(attribute.getName(), "attribute2");
				assertEquals(attribute.getValue(), "value2");
			}
			i++;
		}
		assertEquals(i, 2);
	}

	@Test
	public void testGetChildren() {
		XmlTree tree = new XmlTree("root");
		XmlTree subtree1 = new XmlTree("sub");
		XmlTree subtree2 = new XmlTree("sub");
		tree.addChild(subtree1);
		tree.addChild(subtree2);

		int i = 0;
		for (Iterator<XmlTree> iterator = tree.getChildren(); iterator.hasNext();) {
			assertEquals(iterator.next().getName(), "sub");
			i++;
		}
		assertEquals(i, 2);
	}

	@Test
	public void testAddChildIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		new XmlTree("root").addChild(null);
	}

	@Test
	public void testAddAttributeIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		new XmlTree("root").addAttribute(null);
	}

	@Test
	public void testUpdateAttribute1() {
		XmlTree tree = new XmlTree("root");
		tree.addAttribute(new XmlAttribute("attr", "first_value"));
		tree.updateAttribute(new XmlAttribute("attr", "second_value"));
		assertTrue(tree.getAttributeValue("attr").equals("second_value"));
	}

	@Test
	public void testUpdateAttribute2() {
		XmlTree tree = new XmlTree("root");
		tree.updateAttribute(new XmlAttribute("attr", "first_value"));
		assertTrue(tree.getAttributeValue("attr").equals("first_value"));
	}
}
