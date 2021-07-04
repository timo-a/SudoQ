package de.sudoq.playaround;

import java.io.File;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;


public class EvaluateAssets {

    public static void setup(String SUDOKU_LOCATION_old, String PROFILE_LOCATION, String SUDOKU_LOCATION_new){
        //old location is needed for the type files
        FileManager.initialize(new File(PROFILE_LOCATION), new File(SUDOKU_LOCATION_old));
        evaluateAssets(SUDOKU_LOCATION_new);
        System.exit(9);
    }

    public static void evaluateAssets(String path){

        File dir = new File(path);
        /*Sudoku deb1 = Files.getSudoku(dir, SudokuTypes.samurai, Complexity.infernal, 42);
        //Sudoku deb2 = Files.getSudoku(dir, SudokuTypes.samurai, Complexity.infernal, 43);

        System.out.println(evaluateSudoku(deb1));
        System.out.println(evaluateSudoku(deb2));

        for (Field  f : deb2            ) {
            if (f.isSolved() && f.getCurrentValue() != f.getSolution() )
                System.out.println("errrrr");
            f.setCurrentValue(f.getSolution(), false);
        }
        System.out.println(deb2);
        System.out.println(deb2.getSudokuType().checkSudoku(deb2));
*/

        for (SudokuTypes st : SudokuTypes.values()){
            st = SudokuTypes.Xsudoku;

            System.out.println(st);
            for(Complexity c : Complexity.playableValues()){
                System.out.println(" " + c);
                for (int i = 1; i <= numberOfFilesInDir(path+File.separator+st+File.separator+c) ; i++) {
                    System.out.print("  " + (i<10 ? " " : "") + i + " ");//offset

                    Sudoku s = Files.getSudoku(dir, st, c, i);
                    System.out.println(evaluateSudoku(s));
                }
            }

            break;
        }
    }





    public static String evaluateSudoku(Sudoku s){
        Solver solver = new Solver(s);
        boolean hasSolution = solver.solveAll(true, false, false);
        String solvable = hasSolution ? "" : "NO SOLUTION?!";
        String statistic = solver.getHintCountString();

        String listScore =  String.format("%1$6s", solver.getHintScore());
        assert solver.getHintScore() == solver.getSolverSudoku().getComplexityValue();

        String ambiguous = solver.severalSolutionsExist() ? " a " : "   ";

        int predefined = 0;
        for (Cell f : s)
            if(f.isSolved())
                predefined++;


        return solvable+ambiguous+ " score" + listScore + " predef: "+predefined+" "+statistic;
    }

    public static String displayHelperScores(){
        Solver solver = new Solver(new Sudoku(SudokuType.getSudokuType(SudokuTypes.standard9x9)));
        String s ="";
        String blank = "                          ";
        for(SolveHelper h : solver.helperIterator()){
            s+= "\n" + h.hintType + blank.substring(h.hintType.toString().length()) + String.format("%1$6s",h.getComplexityScore());
        }
        return s.substring(1);
    }

    private static int numberOfFilesInDir(String path) {

        int counter=0;


        File[] files = new File(path).listFiles();

        for (File file : files)
            if (file.isFile() && file.getName().startsWith("sudoku"))
                counter++;

        return counter;
    }
}
