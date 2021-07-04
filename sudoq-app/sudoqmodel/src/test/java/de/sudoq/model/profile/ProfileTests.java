package de.sudoq.model.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;


import java.io.File;
import java.io.IOException;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.game.Assistances;

public class ProfileTests extends TestWithInitCleanforSingletons {

	/*@Test
	public void testProfiles() {
		if (profileManager.getProfilesDir().exists())
			for (File f : profileManager.getProfilesDir().listFiles())
				f.delete();


		assertFalse(profileManager.getProfilesListRepo().ProfilesFile().exists());
		assertEquals(0, FileManager.getNumberOfProfiles());
		Profile.Companion.getInstance(profileDir);
		assertTrue(FileManager.getProfilesFile().exists());
		File profile = FileManager.getProfileXmlFor(1);
		assertTrue(profile.exists() && !profile.isDirectory());
		assertEquals(1, FileManager.getNumberOfProfiles());
		assertEquals(1, Integer.parseInt(profile.getParentFile().getName().substring(8)));

		FileManager.createProfileFiles(2);
		assertEquals(2, FileManager.getNumberOfProfiles());
		FileManager.deleteProfile(2);
		assertEquals(1, FileManager.getNumberOfProfiles());
	}*/

	static File profileDir = new File("/tmp/sudoq/ProfileTests/profile");

	@BeforeClass
	public static void myinit() throws IOException {
		profileDir.mkdirs();
		Utility.clearDir(profileDir);
	}

	@Test
	public void initProfileAndCheckValues() {
		assertTrue("no profile was created", Profile.Companion.getInstance(profileDir).getNumberOfAvailableProfiles() > 0);
		assertTrue("a default value is wrong", Profile.Companion.getInstance(profileDir).getName().equals("unnamed"));
		assertTrue("a default value is wrong", Profile.Companion.getInstance(profileDir).isGestureActive() == false);
		assertTrue("a default value is wrong", Profile.Companion.getInstance(profileDir).getAssistance(Assistances.markRowColumn));
		assertTrue("a default value is wrong",
				Profile.Companion.getInstance(profileDir).getStatistic(Statistics.fastestSolvingTime) == 5999);

		assertEquals("profile id is wrong", 1,Profile.Companion.getInstance(profileDir).getCurrentProfileID());
	}

	@Test
	public void setValues() {
		Profile.Companion.getInstance(profileDir).setName("testname");
		assertTrue("set name doesnt work", Profile.Companion.getInstance(profileDir).getName().equals("testname"));
		Profile.Companion.getInstance(profileDir).setGestureActive(true);
		assertTrue("set gesture doesnt work", Profile.Companion.getInstance(profileDir).isGestureActive() == true);
		Profile.Companion.getInstance(profileDir).setAssistance(Assistances.restrictCandidates, true);
		assertTrue("assistance isnt set", Profile.Companion.getInstance(profileDir).getAssistance(Assistances.restrictCandidates));
		Profile.Companion.getInstance(profileDir).setAssistance(Assistances.restrictCandidates, false);
		assertTrue("assistance is set", !Profile.Companion.getInstance(profileDir).getAssistance(Assistances.restrictCandidates));
		Profile.Companion.getInstance(profileDir).setStatistic(Statistics.maximumPoints, 1000);
		assertTrue("maximumPoints is wrong", Profile.Companion.getInstance(profileDir).getStatistic(Statistics.maximumPoints) == 1000);
		Profile.Companion.getInstance(profileDir).setStatistic(null, 123);
		assertTrue("different value than -1 when passing null to getStatistics",
				Profile.Companion.getInstance(profileDir).getStatistic(null) == -1);
	}

	@Test
	public void createProfileAndSwitch() {
		Profile.Companion.getInstance(profileDir).createAnotherProfile();

		assertTrue("number of profiles is wrong", Profile.Companion.getInstance(profileDir).getNumberOfAvailableProfiles() == 2);
		assertTrue("id is wrong", Profile.Companion.getInstance(profileDir).getCurrentProfileID() == 2);

		Profile.Companion.getInstance(profileDir).changeProfile(1);
		assertTrue("id is wrong", Profile.Companion.getInstance(profileDir).getCurrentProfileID() == 1);

		Profile.Companion.getInstance(profileDir).saveChanges();

		assertTrue("profiles id list size is wrong", Profile.Companion.getInstance(profileDir).getProfilesIdList().size() == 2);

		assertTrue("profiles names list size is wrong", Profile.Companion.getInstance(profileDir).getProfilesNameList().size() == 2);

		Profile.Companion.getInstance(profileDir).deleteProfile();

		assertTrue("number of profiles is wrong", Profile.Companion.getInstance(profileDir).getNumberOfAvailableProfiles() == 1);

		assertEquals(1, Profile.Companion.getInstance(profileDir).getNumberOfAvailableProfiles());
		assertFalse(Profile.Companion.getInstance(profileDir).noProfiles());

		assertTrue("id is wrong", Profile.Companion.getInstance(profileDir).getCurrentProfileID() == 2);
	}

}
