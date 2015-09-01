package de.sudoq.model.game;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.game.Assistances;

public class AssistancesTests {

	@Test
	public void test() {
		Assistances[] types = Assistances.values();
		for (Assistances type : types) {
			assertTrue(Assistances.valueOf(type.toString()).equals(type));
		}
	}

}
