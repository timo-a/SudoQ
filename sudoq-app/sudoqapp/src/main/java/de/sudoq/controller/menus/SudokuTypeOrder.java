package de.sudoq.controller.menus;

import java.util.Comparator;

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class SudokuTypeOrder {

    public static int getKey(SudokuTypes s){
        return ordinals[s.ordinal()];
    }

    private static int[] ordinals;
    static{

        SudokuTypes[] order = {SudokuTypes.standard9x9,
                               SudokuTypes.standard4x4,
                               SudokuTypes.standard6x6,
                               SudokuTypes.standard16x16,
                               SudokuTypes.samurai,
                               SudokuTypes.Xsudoku,
                               SudokuTypes.HyperSudoku,
                               SudokuTypes.squigglya,
                               SudokuTypes.squigglyb,
                               SudokuTypes.stairstep};

        ordinals = new int[SudokuTypes.values().length];

        for (int i = 0; i < order.length; i++) {
            ordinals[order[i].ordinal()] = i;
        }

    }

}
