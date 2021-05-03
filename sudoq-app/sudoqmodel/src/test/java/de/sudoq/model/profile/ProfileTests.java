package de.sudoq.model.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.Statistics;

public class ProfileTests extends TestWithInitCleanforSingletons {

	@Test
	public void initProfileAndCheckValues() {
		assertTrue("no profile was created", Profile.Companion.getInstance().getNumberOfAvailableProfiles() > 0);
		assertTrue("a default value is wrong", Profile.Companion.getInstance().getName().equals("unnamed"));
		assertTrue("a default value is wrong", Profile.Companion.getInstance().isGestureActive() == false);
		assertTrue("a default value is wrong", Profile.Companion.getInstance().getAssistance(Assistances.markRowColumn));
		assertTrue("a default value is wrong",
				Profile.Companion.getInstance().getStatistic(Statistics.fastestSolvingTime) == 5999);

		assertEquals("profile id is wrong", 1,Profile.Companion.getInstance().getCurrentProfileID());
		assertTrue("next profile ID is wrong", Profile.Companion.getInstance().getNewProfileID() == 2);

	}

	@Test
	public void setValues() {
		Profile.Companion.getInstance().setName("testname");
		assertTrue("set name doesnt work", Profile.Companion.getInstance().getName().equals("testname"));
		Profile.Companion.getInstance().setGestureActive(true);
		assertTrue("set gesture doesnt work", Profile.Companion.getInstance().isGestureActive() == true);
		Profile.Companion.getInstance().setAssistance(Assistances.restrictCandidates, true);
		assertTrue("assistance isnt set", Profile.Companion.getInstance().getAssistance(Assistances.restrictCandidates));
		Profile.Companion.getInstance().setAssistance(Assistances.restrictCandidates, false);
		assertTrue("assistance is set", !Profile.Companion.getInstance().getAssistance(Assistances.restrictCandidates));
		Profile.Companion.getInstance().setStatistic(Statistics.maximumPoints, 1000);
		assertTrue("maximumPoints is wrong", Profile.Companion.getInstance().getStatistic(Statistics.maximumPoints) == 1000);
		Profile.Companion.getInstance().setStatistic(null, 123);
		assertTrue("different value than -1 when passing null to getStatistics",
				Profile.Companion.getInstance().getStatistic(null) == -1);
	}

	@Test
	public void createProfileAndSwitch() {
		Profile.Companion.getInstance().createProfile();

		assertTrue("number of profiles is wrong", Profile.Companion.getInstance().getNumberOfAvailableProfiles() == 2);
		assertTrue("id is wrong", Profile.Companion.getInstance().getCurrentProfileID() == 2);

		Profile.Companion.getInstance().changeProfile(1);
		assertTrue("id is wrong", Profile.Companion.getInstance().getCurrentProfileID() == 1);

		Profile.Companion.getInstance().saveChanges();

		assertTrue("profiles id list size is wrong", Profile.Companion.getInstance().getProfilesIdList().size() == 2);

		assertTrue("profiles names list size is wrong", Profile.Companion.getInstance().getProfilesNameList().size() == 2);

		Profile.Companion.getInstance().deleteProfile();

		assertTrue("number of profiles is wrong", Profile.Companion.getInstance().getNumberOfAvailableProfiles() == 1);

		assertEquals(1, FileManager.getNumberOfProfiles());

		assertTrue("id is wrong", Profile.Companion.getInstance().getCurrentProfileID() == 2);
	}

}
