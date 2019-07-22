package de.sudoq.model.solverGenerator.FastSolver.DLX1;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.FastSolver.DLX2.SamuraiSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory;
import de.sudoq.model.solverGenerator.solver.SudokuMockUps;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.XmlHelper;

import static org.junit.Assert.assertTrue;

public class SamuraiRegressionTest {


    @BeforeClass
    public static void init() {
        FileManagerTests.init();
    }


    @Test
    public void testSamurai1(){
        String pattern =  ". . . . 1 . . 3 .       . . . 1 9 . 5 . . "
                        + ". 5 3 . . . . . .       . . . . . . . 7 . "
                        + "4 . . . . . . . .       . . . 2 . . . . . "
                        + "6 9 . . . . . . .       4 . 7 3 1 . . . . "
                        + ". . 7 . 9 5 . . .       9 . . 4 . . . . . "
                        + ". . . . . . . . .       . . . . 2 . . 4 . "
                        + "9 7 8 . . 3 . . . . 3 . . . . 6 . . . . . "
                        + ". . . . . . . . . 1 7 . . . . . 3 . . . . "
                        + ". . . . . . . . . 6 4 . 3 1 . . . . . . . "
                        + "            . 8 5 . 9 1 . . .             "
                        + "            3 6 . 7 . . . . .             "
                        + "            . . . 8 . . . . .             "
                        + "6 . 8 9 . . . . . . . . . . . 1 . . . 9 . "
                        + ". . . . . . . . . . . . . . 4 8 . . 1 . . "
                        + ". 4 . . . . . . 8 . . . . . . . . . 8 . . "
                        + ". . . . . . . . 4       . . 7 . . . . . . "
                        + ". . . . . . . . .       . 4 5 . 7 . . 8 . "
                        + "7 . . 1 . . 3 . .       . . . 2 . . 4 . 3 "
                        + ". 1 2 . 9 5 4 . .       . 3 6 . 1 . . . . "
                        + "4 9 6 3 . . . . .       7 . 2 9 3 . . . . "
                        + ". . . . . . . . .       . . . . . . . . . ";

        Sudoku s = SudokuMockUps.stringToSamuraiSudoku(pattern);
        FastSolver fs = new SamuraiSolver(s);
        assertTrue(fs.isAmbiguous());

    }


    @Test
    public void testSamurai2(){
        String pattern =  ". . . . 1 . . 3 .       . . . 1 9 . 5 . . "
                + ". 5 3 . . . . . .       . . . . . . . 7 . "
                + "4 . . . . . . . .       . . . 2 . . . . . "
                + "6 9 . . . . . . .       4 . 7 3 1 . . . . "
                + ". . 7 . 9 5 . . .       9 . . 4 . . . . . "
                + ". . . . . . . . .       . . . . 2 . . 4 . "
                + "9 7 8 . . 3 . . . . 3 . . . . 6 . . . . . "
                + ". . . . . . . . . 1 7 . . . . . 3 . . . . "
                + ". . . . . . . . . 6 4 . 3 1 . . . . . . . "
                + "            . 8 5 . 9 1 . . .             "
                + "            3 6 . 7 . . . . .             "
                + "            . . . 8 . . . . .             "
                + "6 . 8 9 . . . . . . . . . . . 1 6 . . 9 . "
                + ". . . . . . . . . . . . . . 4 8 . . 1 . . "
                + ". 4 . . . . . . 8 . . . . . . . . . 8 . . "
                + ". . . . . . . . 4       . . 7 . . . . . . "
                + ". . . . . . . . .       . 4 5 . 7 . . 8 . "
                + "7 . . 1 . . 3 . .       . . . 2 . . 4 . 3 "
                + ". 1 2 . 9 5 4 . .       . 3 6 . 1 . . . . "
                + "4 9 6 3 . . . . .       7 . 2 9 3 . . . . "
                + ". . . . . . . . .       . . . . . . . . . ";


        Sudoku s = SudokuMockUps.stringToSamuraiSudoku(pattern);
        FastSolver fs = new SamuraiSolver(s);
        fs.isAmbiguous();

    }

    String badPattern =  ". . . . 2 . . 4 .       . . . 2 1 . 6 . . "
                       + ". 6 4 . . . . . .       . . . . . . . 8 . "
            + "5 . . . . . . . .       . . . 3 . . . . . "
            + "7 1 . . . . . . .       5 . 8 4 2 . . . . "
            + ". . 8 . 1 6 . . .       1 . . 5 . . . . . "
            + ". . . . . . . . .       . . . . 3 . . 5 . "
            + "1 8 9 . . 4 . . . . 4 . . . . 7 . . . . . "
            + ". . . . . . . . . 2 8 . . . . . 4 . . . . "
            + ". . . . . . . . . 7 5 . 4 2 . . . . . . . "
            + "            . 9 6 . 1 2 . . .             "
            + "            4 7 . 8 . . . . .             "
            + "            . . . 9 . . . . .             "
            + "7 . 9 1 . . . . . . . . . . . 2 7 . . 1 . "
            + ". . . . . . . . . . . . . . 5 9 . . 2 . . "
            + ". 5 . . . . . . 9 . . . . . . . . . 9 . . "
            + ". . . . . . . . 5       . . 8 . . . . . . "
            + ". . . . . . . . .       . 5 6 . 8 . . 9 . "
            + "8 . . 2 . . 4 . .       . . . 3 . . 5 . 4 "
            + ". 2 3 . 1 6 5 . .       . 4 7 . 2 . . . . "
            + "5 1 7 4 . . . . .       8 . 3 1 4 . . . . "
            + ". . . . . . . . .       . . . . . . . . . ";



    @Test
    public void testSamuraiAsFound(){
        System.out.println(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
        for (int i = 0; i < 9; i++)
            permutationApplicator(i, badPattern);
    }


    private void permutationApplicator(int nrPemutations, String pattern){
        for (int i=0; i<nrPemutations;i++){
            pattern = decreaseByOne(pattern);
        }
        Sudoku s = SudokuMockUps.stringToSamuraiSudoku(pattern);
        FastSolver fs = new SamuraiSolver(s);
        fs.isAmbiguous();
    }


    private String decreaseByOne(String pattern){
        return pattern.replace('1','A')
                .replace('2','1')
                .replace('3','2')
                .replace('4','3')
                .replace('5','4')
                .replace('6','5')
                .replace('7','6')
                .replace('8','7')
                .replace('9','8')
                .replace('A','9');
    }






}
