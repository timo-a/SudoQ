package de.sudoq.model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.utility.FileManager;

/**
 * abstract utility class for operations shared by several tests
 */
public abstract class Utility {

    public static File sudokus;
    public static File profiles;

    public static final String SUDOQ_LOCATION = "/home/t/Code/SudoQ/";
    public static final String RES = SUDOQ_LOCATION + "sudoq-app/sudoqapp/src/main/" + "res" + File.separator;


    /*
    * Copy files from assets to temporary dir for testing
    * also init Filemanager
    * */
    public static void copySudokus() {
        String res = RES;
        sudokus  = new File(res + "tmp_suds");
        sudokus.mkdir();

        try {
            String path = SUDOQ_LOCATION + "sudoq-app/" + "sudoqapp/src/main/assets/sudokus/".replaceAll("/",File.separator);
            FileUtils.copyDirectory(new File(path), sudokus);
            System.out.println("path:");
            System.out.println((new File(res)).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileManager.initialize(sudokus);
    }

    public static void print9x9(Sudoku sudoku){
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < sudoku.getSudokuType().getSize().getY(); j++) {
            for (int i = 0; i < sudoku.getSudokuType().getSize().getX(); i++) {
                Cell f = sudoku.getCellNullable(Position.Companion.get(i, j));
                String op;
                if (f != null){//feld existiert
                    int value = f.getCurrentValue();
                    op = value + "";
                    if (value < 10)
                        op = "" + value;
                    if (value == -1)
                        op = "x";
                    sb.append(op + " ");
                }else{
                    sb.append("  ");//todo why wouldn't it exist?

                }
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }


    /** returns all positions of non-null Fields of sudoku */
    public static List<Position> getPositionsByRow(Sudoku sudoku){
        return StreamSupport.stream(sudoku.getSudokuType().getValidPositions().spliterator(), false)
                .filter(p -> sudoku.getCellNullable(p) != null)
                .collect(Collectors.toList());
    }


    /**
     * Removes everything in the Directory but not the directory itself
     * @param f
     *            das Verzeichnis
     * @throws IOException
     *             falls etwas nicht gelöscht werden konnte
     */
    public static void clearDir(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteDir(c);
        }
    }

    /**
     * Löscht rekursiv das gegebene Verzeichnis
     *
     * @param f
     *            das Verzeichnis
     * @throws IOException
     *             falls etwas nicht gelöscht werden konnte
     */
    public static void deleteDir(File f) throws IOException {
        clearDir(f);

        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
}