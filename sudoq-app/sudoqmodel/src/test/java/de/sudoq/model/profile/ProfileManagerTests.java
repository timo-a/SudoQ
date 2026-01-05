package de.sudoq.model.profile;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.profile.IProfilesListRepo;

class ProfileManagerTests {

    /*@Test todo fix, probably needs tempdir, but is it even relevant?
    public void testManagerInstantiation() {
        File f = new File("/tmp/123");
        System.out.println(f.setWritable(true));
        System.out.println(f.canWrite());
        IRepo<Profile> pr = getMockProfileRepo();
        IProfilesListRepo plr = getMockProfileListRepo();
        ProfileManager pm = new ProfileManager(f, pr, plr);
    }*/

    IRepo<Profile> getMockProfileRepo() {
        return new IRepo<Profile>() {
            @NotNull
            @Override
            public List<Integer> ids() {
                throw new NotImplementedException();
            }

            @Override
            public Profile create() {
                return null;
            }

            @Override
            public Profile read(int id) {
                return null;
            }

            @Override
            public Profile update(Profile profile) {
                return null;
            }

            @Override
            public void delete(int id) {

            }
        };
    }

    IProfilesListRepo getMockProfileListRepo() {
        return new IProfilesListRepo() {
            @Override
            public boolean profilesFileExists() {
                return false;
            }

            @Override
            public void createProfilesFile() {

            }

            @Override
            public void addProfile(@NotNull Profile newProfile) {

            }

            @NotNull
            @Override
            public List<String> getProfileNamesList() {
                return null;
            }

            @Override
            public int getCurrentProfileId() {
                return 0;
            }

            @Override
            public void deleteProfileFromList(int id) {

            }

            @Override
            public int getNextProfile() {
                return 0;
            }

            @Override
            public int getProfilesCount() {
                return 0;
            }

            @Override
            public void setCurrentProfileId(int id) {

            }

            @Override
            public void updateProfilesList(@NotNull Profile changedProfile) {

            }

            @NotNull
            @Override
            public List<Integer> getProfileIdsList() {
                return null;
            }
        };
    }

    @Test
    void childInstantiation() {
        File f = new File("/tmp/123");
        //Profile p = new Profile(f);
    }

    /*@Test
    public void testInstantiation() {
        File f = new File("/tmp/123");
        //Profile p = new Profile(f);

        ProfileSingleton pci = ProfileSingleton.Companion.getInstance(f, getMockProfileRepo(), getMockProfileListRepo());
    }*/
}
