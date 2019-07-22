package de.sudoq.model.solverGenerator;

import de.sudoq.model.solverGenerator.solver.SudokuMockUps;
import de.sudoq.model.sudoku.Sudoku;

public class RegressionBoards16 {

    public static final Sudoku r2 = SudokuMockUps.stringTo16x16Sudoku(increase16By1(
            "8  0  .  2  .  .  .  .  .  5  .  .  .  6  3  . "
                                       + " .  9  .  .  8  . 10  .  2  .  6  . 11  . 15  . "
                                       + " .  .  6  .  1 13  .  0  .  .  . 12  .  .  8  . "
                                       + " .  .  .  .  2  3  .  .  .  .  .  .  7  . 10  . "
                                       + " 0  . 11 13  .  .  .  .  1  3  .  .  9 10  .  . "
                                       + " .  . 15  .  5  7  . 10  .  .  .  .  .  . 11  . "
                                       + " 2  1  .  6 15  .  9  8 13  . 11  .  .  .  .  3 "
                                       + " .  .  .  .  .  .  .  1  .  .  9  .  .  .  .  8 "
                                       + "13  .  .  8  .  .  . 15  .  .  .  .  .  .  9 10 "
                                       + "11  .  .  .  4  6  .  3  .  .  0  .  .  . 13  7 "
                                       + " . 10  .  1 12  .  .  .  .  8  2  .  .  .  .  . "
                                       + " 3  .  . 12  .  .  .  2  .  6  . 13  . 11  .  . "
                                       + " . 13 12  3  7 15  .  .  0  .  5  .  .  .  .  . "
                                       + " .  .  .  .  .  .  .  .  .  .  .  .  .  .  . 12 "
                                       + " 4  .  1 11  .  . 12  6  .  .  .  . 10  .  .  . "
                                       + " .  .  . 14  .  .  2  .  .  1  .  .  .  3  4  . "));


    public static String increase16By1(String pattern){
        return pattern.replace("15","16")
                .replace("14","15")
                .replace("13","14")
                .replace("12","13")
                .replace("11","12")
                .replace("10","11")
                .replace(" 9","10")
                .replace("8 ","9 ")
                .replace("7 "," 8 ")
                .replace(" 6 "," 7 ")
                .replace(" 5 "," 6 ")
                .replace(" 4 "," 5 ")
                .replace(" 3 "," 4 ")
                .replace(" 2 "," 3 ")
                .replace(" 1 "," 2 ")
                .replace(" 0 "," 1 ");
    }

}
