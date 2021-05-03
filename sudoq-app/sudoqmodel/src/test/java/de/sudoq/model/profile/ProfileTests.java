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
		Profile.forceInitialize();
		Profile pi = Profile.getInstance();
		pi.loadCurrentProfile();
		assertTrue("no profile was created", Profile.getInstance().getNumberOfAvailableProfiles() > 0);
		assertTrue("a default value is wrong", Profile.getInstance().getName().equals("unnamed"));
		assertTrue("a default value is wrong", Profile.getInstance().isGestureActive() == false);
		assertTrue("a default value is wrong", Profile.getInstance().getAssistance(Assistances.markRowColumn));
		assertTrue("a default value is wrong",
				Profile.getInstance().getStatistic(Statistics.fastestSolvingTime) == 5999);

		assertEquals("profile id is wrong", 1,Profile.getInstance().getCurrentProfileID());
		assertTrue("next profile ID is wrong", Profile.getInstance().getNewProfileID() == 2);

	}

	@Test
	public void setValues() {
		Profile.getInstance().setName("testname");
		assertTrue("set name doesnt work", Profile.getInstance().getName().equals("testname"));
		Profile.getInstance().setGestureActive(true);
		assertTrue("set gesture doesnt work", Profile.getInstance().isGestureActive() == true);
		Profile.getInstance().setAssistance(Assistances.restrictCandidates, true);
		assertTrue("assistance isnt set", Profile.getInstance().getAssistance(Assistances.restrictCandidates));
		Profile.getInstance().setAssistance(Assistances.restrictCandidates, false);
		assertTrue("assistance is set", !Profile.getInstance().getAssistance(Assistances.restrictCandidates));
		Profile.getInstance().setStatistic(Statistics.maximumPoints, 1000);
		assertTrue("maximumPoints is wrong", Profile.getInstance().getStatistic(Statistics.maximumPoints) == 1000);
		Profile.getInstance().setStatistic(null, 123);
		assertTrue("different value than -1 when passing null to getStatistics",
				Profile.getInstance().getStatistic(null) == -1);
	}

	@Test
	public void createProfileAndSwitch() {
		Profile.getInstance().createProfile();

		assertTrue("number of profiles is wrong", Profile.getInstance().getNumberOfAvailableProfiles() == 2);
		assertTrue("id is wrong", Profile.getInstance().getCurrentProfileID() == 2);

		Profile.getInstance().changeProfile(1);
		assertTrue("id is wrong", Profile.getInstance().getCurrentProfileID() == 1);

		Profile.getInstance().saveChanges();

		assertTrue("profiles id list size is wrong", Profile.getInstance().getProfilesIdList().size() == 2);

		assertTrue("profiles names list size is wrong", Profile.getInstance().getProfilesNameList().size() == 2);

		Profile.getInstance().deleteProfile();

		assertTrue("number of profiles is wrong", Profile.getInstance().getNumberOfAvailableProfiles() == 1);

		assertEquals(1, FileManager.getNumberOfProfiles());

		assertTrue("id is wrong", Profile.getInstance().getCurrentProfileID() == 2);
	}

}
