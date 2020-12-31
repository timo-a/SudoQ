package de.sudoq.model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

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
        profiles = new File(res + "tmp_profiles");
        sudokus.mkdir();

        try {
            String path = SUDOQ_LOCATION + "sudoq-app/" + "sudoqapp/src/main/assets/sudokus/".replaceAll("/",File.separator);
            FileUtils.copyDirectory(new File(path), sudokus);
            System.out.println("path:");
            System.out.println((new File(res)).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        profiles.mkdir();
        FileManager.initialize(profiles, sudokus);
    }

    public static void print9x9(Sudoku sudoku){
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < sudoku.getSudokuType().getSize().getY(); j++) {
            for (int i = 0; i < sudoku.getSudokuType().getSize().getX(); i++) {
                Cell f = sudoku.getCell(Position.get(i, j));
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
                    sb.append("  ");

                }
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }


    /** returns all positions of non-null Fields of sudoku */
    public static List<Position> getPositionsByRow(Sudoku sudoku){
        List<Position> p = new ArrayList<>();
        for (int y = 0; y < sudoku.getSudokuType().getSize().getY(); y++)
            for (int x = 0; x < sudoku.getSudokuType().getSize().getX(); x++)
                if (sudoku.getCell(Position.get(x, y)) != null)
                    p.add(Position.get(x, y));
        return p;
    }


}