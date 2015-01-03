package de.sudoq.model.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.game.GameSettings;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.xml.XmlTree;

public class AssistanceSetTests {
	@Test
	public void test() {
		GameSettings a = new GameSettings();
		a.setAssistance(Assistances.autoAdjustNotes);
		a.setAssistance(Assistances.markWrongSymbol);

		assertTrue("autoAdjustNotes has wrong value", a.getAssistance(Assistances.autoAdjustNotes));
		assertFalse("markRowColumn has wrong value", a.getAssistance(Assistances.markRowColumn));
		assertTrue("markWrongSymbol has wrong value", a.getAssistance(Assistances.markWrongSymbol));
		assertFalse("restrictCandidates has wrong value", a.getAssistance(Assistances.restrictCandidates));

		XmlTree t = a.toXmlTree();
		a = new GameSettings();
		a.fillFromXml(t);

		assertTrue("autoAdjustNotes has wrong value", a.getAssistance(Assistances.autoAdjustNotes));
		assertFalse("markRowColumn has wrong value", a.getAssistance(Assistances.markRowColumn));
		assertTrue("markWrongSymbol has wrong value", a.getAssistance(Assistances.markWrongSymbol));
		assertFalse("restrictCandidates has wrong value", a.getAssistance(Assistances.restrictCandidates));

		a.clearAssistance(Assistances.markWrongSymbol);
		assertFalse("markWrongSymbol has wrong value", a.getAssistance(Assistances.markWrongSymbol));
	}

	@Test
	public void testNull() {
		GameSettings a = new GameSettings();
		a.setAssistance(null);
		assertFalse(a.getAssistance(null));
		a.clearAssistance(null);
		assertFalse(a.getAssistance(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFooString() {
		(new GameSettings()).fillFromXml(new XmlTree("foo"));
	}
}
