package de.sudoq.model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import de.sudoq.model.files.FileManager;

/**
 * abstract utility class for operations shared by several tests
 */
public abstract class Utility {

    public static File sudokus;
    public static File profiles;

    /*
    * Copy files from assets to temporary dir for testing
    * also init Filemanager
    * */
    public static void copySudokus() {
        String res = "res" + File.separator;
        sudokus = new File(res  + "tmp_suds");
        profiles = new File(res + "tmp_profiles");
        sudokus.mkdir();

        try {
            String path = "sudoqapp/src/main/assets/sudokus/".replaceAll("/",File.separator);
            FileUtils.copyDirectory(new File(path), sudokus);
            System.out.println("path:");
            System.out.println((new File(res)).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        profiles.mkdir();
        FileManager.initialize(profiles, sudokus);
    }
}