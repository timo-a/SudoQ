package de.sudoq.model.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

import de.sudoq.model.profile.Statistics;

class StatisticsTests {

    @Test
    void test() {
		Statistics[] types = Statistics.values();
		for (Statistics type : types) {
			assertEquals(Statistics.valueOf(type.toString()), type);
		}
	}

}
