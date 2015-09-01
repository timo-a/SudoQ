package de.sudoq.model.profile;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.sudoq.model.profile.Statistics;

public class StatisticsTests {

	@Test
	public void test() {
		Statistics[] types = Statistics.values();
		for (Statistics type : types) {
			assertTrue(Statistics.valueOf(type.toString()).equals(type));
		}
	}

}
