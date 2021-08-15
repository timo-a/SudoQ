package de.sudoq.model.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.sudoq.model.xml.XmlAttribute;

public class XmlAttributeTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private String returnNull(){return null;}


	@Test
	public void testConstructorStringStringIllegalArgumentException2() {
		thrown.expect(IllegalArgumentException.class);
		new XmlAttribute("", "value");
	}


	@Test
	public void testGetName() {
		XmlAttribute attribute = new XmlAttribute("xyzName", "");
		assertEquals(attribute.getName(), "xyzName");
	}

	@Test
	public void testGetValue() {
		XmlAttribute attribute = new XmlAttribute("xyzName", "xyzValue");
		assertEquals(attribute.getValue(), "xyzValue");
	}

	@Test
	public void testIsSameAttribute() {
		assertTrue(new XmlAttribute("xyzName", "value").isSameAttribute(new XmlAttribute("xyzName", "differentvalue")));
	}

}
