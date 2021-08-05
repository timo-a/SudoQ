package de.sudoq.model.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.game.GameSettings;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.xml.XmlTree;

public class AssistanceSetTests {
	@Test

	@Test(expected = NullPointerException.class)
	public void testFooString() {
		(new GameSettings()).fillFromXml(new XmlTree("foo"));
	}
}
