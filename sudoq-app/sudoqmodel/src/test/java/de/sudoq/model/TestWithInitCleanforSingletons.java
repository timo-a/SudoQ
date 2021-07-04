package de.sudoq.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.persistence.xml.ProfileRepo;
import de.sudoq.model.persistence.xml.ProfilesListRepo;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.ProfileManager;

public class TestWithInitCleanforSingletons {

	protected static ProfileManager profileManager;

	@BeforeClass
	public static void init() throws IOException {

		setupProfiles();


		System.out.println("init called");
		Utility.copySudokus();

		//p.Companion.forceReinitialize();


	}

	public static void setupProfiles() throws IOException {

		File profilesDir = new File(Utility.RES, "tmp_profiles");

		//ensure profiles dir exists and is empty
		if (profilesDir != null && profilesDir.exists())
			Utility.clearDir(profilesDir);
		else
			profilesDir.mkdirs();

		ProfileRepo profileRepo = new ProfileRepo(profilesDir);
		ProfilesListRepo profilesListRepo = new ProfilesListRepo(profilesDir);
		profileManager = new ProfileManager(profileRepo, profilesListRepo, profilesDir);
		profileManager.createInitialProfile();


	}

	public static void legacyInit(){
		Utility.copySudokus();
		Profile.Companion.forceReinitialize();
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		//java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
		//f.setAccessible(true);
		//f.set(null, null);
		java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
		s.setAccessible(true);
		s.set(null, null);
		//Utility.deleteDir(Utility.profiles);
		Utility.deleteDir(Utility.sudokus);
	}
}
