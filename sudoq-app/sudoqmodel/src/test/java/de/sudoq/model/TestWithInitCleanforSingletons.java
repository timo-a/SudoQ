package de.sudoq.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;

import de.sudoq.model.utility.FileManager;
import de.sudoq.model.profile.ProfileManager;
import de.sudoq.persistence.profile.ProfileRepo;
import de.sudoq.persistence.profile.ProfilesListRepo;

public class TestWithInitCleanforSingletons {

	protected static ProfileManager profileManager;

    @BeforeAll
    static void init() throws IOException {

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

		//todo mock input
		profileManager = new ProfileManager(profilesDir, new ProfileRepo(profilesDir), new ProfilesListRepo(profilesDir));
		profileManager.createInitialProfile();


	}

    @AfterAll
    static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
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
