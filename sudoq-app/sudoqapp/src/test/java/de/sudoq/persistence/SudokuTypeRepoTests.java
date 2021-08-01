package de.sudoq.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.persistence.sudokuType.SudokuTypeBE;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.persistence.sudokuType.SudokuTypeRepo;
import kotlin.NotImplementedError;

public class SudokuTypeRepoTests {

    @TempDir
    public File tempFolder;

    /**
     * Create is not implemented.
     * The types are copied directly.
     * todo do the initial fill via create and update?
     */
    @Test
    public void testCreate() {
        SudokuTypeRepo sTR = new SudokuTypeRepo(new File("dummy"));
        Assertions.assertThrows(NotImplementedError.class, () -> sTR.create());
    }

    /**
     * Verifies that read can successfully load a sudokuType.
     */
    @Test
    public void testRead() throws IOException {
        //reference needed files from resources
        ClassLoader classLoader = getClass().getClassLoader();
        File sourceFile = new File(classLoader
                .getResource("persistence/SudokuTypeRepo/standard9x9.xml")
                .getFile());

        File tempFolder = new File("/tmp/junit-tmp-space");//local todo remove when @TempDir works
        //mock target file structure
        final File typeDir = new File(tempFolder, "standard9x9");
        typeDir.mkdirs();//todo remove, once https://stackoverflow.com/questions/68143330/tempdir-is-null is answered
        Path targetPath = Paths.get(typeDir.getAbsolutePath(), "standard9x9.xml");
        System.out.println(targetPath.toString());

        Assertions.assertTrue(tempFolder. exists());
        Assertions.assertTrue(tempFolder.isDirectory());
        System.out.println(tempFolder.getAbsoluteFile());
        Assertions.assertTrue(sourceFile.exists());
        Files.copy(sourceFile.toPath(), targetPath);

        SudokuTypeRepo sTR = new SudokuTypeRepo(tempFolder);
        SudokuType st = sTR.read(0);

        Assertions.assertSame(0, SudokuTypes.standard9x9.ordinal(), "standard should have ordinal 0");
        Assertions.assertSame(SudokuTypes.standard9x9, st.getEnumType());
    }

    /**
     * Update is not implemented.
     * The types are copied directly in the beginning and not supposed to change.
     * todo do the initial fill via create and update?
     */
    @Test
    public void testUpdate() {
        SudokuTypeRepo sTR = new SudokuTypeRepo(new File("dummy"));
        Assertions.assertThrows(NotImplementedError.class, () -> sTR.update(new SudokuType(9,9,9)));
    }

    /**
     * Delete is not implemented.
     * There is no use case for it.
     */
    @Test
    public void testDelete() {
        SudokuTypeRepo sTR = new SudokuTypeRepo(new File("dummy"));
        Assertions.assertThrows(NotImplementedError.class, () -> sTR.delete(0));
    }

    @AfterAll //todo delete after tempdir works
    public static void deleteTmpDir() throws IOException {
        delete(new File("/tmp/junit-tmp-space"));
    }
    static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

}

