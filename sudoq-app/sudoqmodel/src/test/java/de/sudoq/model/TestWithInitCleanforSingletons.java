package de.sudoq.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

import de.sudoq.model.files.FileManager;

public class TestWithInitCleanforSingletons {

	@BeforeClass
	public static void init() throws IOException {
		if (Utility.profiles != null && Utility.profiles.exists())
			FileManager.deleteDir(Utility.profiles);


		System.out.println("init called");
		Utility.copySudokus();
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
		f.setAccessible(true);
		f.set(null, null);
		java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
		s.setAccessible(true);
		s.set(null, null);
		FileManager.deleteDir(Utility.profiles);
		FileManager.deleteDir(Utility.sudokus);
	}
}
