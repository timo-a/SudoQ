package my.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTempFileTest {

    @TempDir
    public File tempFolder;

    @Test
    public void testTempFolder() {
        Assertions.assertNotNull(tempFolder);
    }

    @Test
    public void testTempFolderParam(@TempDir File tempFolder) {
        Assertions.assertNotNull(tempFolder);
    }
 }

