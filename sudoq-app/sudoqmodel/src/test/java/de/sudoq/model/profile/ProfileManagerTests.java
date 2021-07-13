package de.sudoq.model.profile;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class ProfileManagerTests {

    @Test
    public void testManagerInstantiation() {
        File f = new File("/tmp/123");
        System.out.println(f.setWritable(true));
        System.out.println(f.canWrite());
        ProfileManager pm = new ProfileManager(f);
    }

    @Test
    public void testChildInstantiation() {
        File f = new File("/tmp/123");
        //Profile p = new Profile(f);
    }

    @Test
    public void testInstantiation() {
        File f = new File("/tmp/123");
        //Profile p = new Profile(f);

        ProfileSingleton pci = ProfileSingleton.Companion.getInstance(f);
    }
}
