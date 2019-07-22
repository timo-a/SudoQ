package de.sudoq.playaround;

import java.io.IOException;

import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.XmlHelper;

public class Files {



    public static Sudoku getSudoku(java.io.File dir, SudokuTypes st, Complexity c, int i){
        java.io.File f = new java.io.File(dir, st.toString()
                + java.io.File.separator
                + c.toString()
                + java.io.File.separator
                + "sudoku_" + i + ".xml");

        Sudoku s = new Sudoku(SudokuType.getSudokuType(st));
        try {
            s.fillFromXml(new XmlHelper().loadXml(f));
            s.setComplexity(Complexity.arbitrary);//justincase
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
