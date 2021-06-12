package de.sudoq.playaround;

import java.io.File;
import java.util.List;
import java.util.Random;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.solverGenerator.GenerationAlgo;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.xml.SudokuXmlHandler;


public class GenerateSomeSudokus {

    private static Sudoku sudoku;
    private static List<Solution> solutions;
    private static Random random;

    String SUDOKU_LOCATION  = "/home/t/Code/SudoQ/DebugOnPC/sudokufiles";
    String SUDOKU_LOCATION2 = "/home/t/Code/SudoQ/DebugOnPC/sudokufiles2";
    String PROFILE_LOCATION = "/home/t/Code/SudoQ/DebugOnPC/profilefiles";

    public void setup(String profiles, String sudokus, long seed) {
        FileManager.initialize(new File(sudokus));
        //Profile.getInstance();
        random = new Random(seed);
        //random = new Random(111398881573105l);

    }

    public void setSeed(long s){
        random = new Random(s);
    }

    public void setup() {
        setup(PROFILE_LOCATION, SUDOKU_LOCATION2,0);
    }

    public void changeSudokuFile(File f){FileManager.initialize(f);}

    public void generate10infernal(){
        generate(Complexity.infernal, SudokuTypes.standard9x9, 10);
    }
    public void generate10easy(){
        generate(Complexity.easy, SudokuTypes.standard9x9, 10);
    }

    public void generate(Complexity c, SudokuTypes st, int numberOfGenerated){
        Solver ss;
        for (int i = 0; i < numberOfGenerated; i++) {
            generate(c, st);
            ss = new Solver(sudoku);
            ss.solveAll(true, false, false);
            //System.out.println(ss.getHintCountString());

        }
    }

    public void generate(Complexity c, SudokuTypes st){
        Sudoku sudoku = new SudokuBuilder(st, new File(SUDOKU_LOCATION2)).createSudoku();
		sudoku.setComplexity(c);

		GenerationAlgo ga = new GenerationAlgo(sudoku, getCallbackObj(), random);
		ga.run();
    }


    private GeneratorCallback getCallbackObj(){
        return new GeneratorCallback() {
            @Override
            public void generationFinished(Sudoku sudoku) {
                if(sudoku==null)
                    System.out.println("sudoku received is null");
                GenerateSomeSudokus.sudoku = sudoku;
                System.out.println("hhellooo");
            }

            @Override
            public void generationFinished(Sudoku sudoku, List<Solution> slist) {
                if(sudoku==null)
                    System.out.println("sudoku received is null");
                GenerateSomeSudokus.sudoku    = sudoku;
                GenerateSomeSudokus.solutions = slist;
                if (slist == null)
                    System.out.println("Callbackobj receives null for solutions: ");
            }

            @Override
            public String toString() {
                return "experiment";
            }

        };
    }



    public Sudoku getSudoku() {
        return GenerateSomeSudokus.sudoku;
    }
    public List<Solution> getSolutions(){
        return solutions;
    }

    public  Random getRandom() {
        return random;
    }

    public  void saveSudoku(String path){
        File sudokuLocation = FileManager.getSudokuDir();
        changeSudokuFile(new File(path));
        new SudokuXmlHandler().saveAsXml(sudoku);
        changeSudokuFile(sudokuLocation);
    }

    /* need to save a sudoku for debugging? just copy paste this ,method */
    //careful: we need to ensure there is a folder structure path/type/complexity/
    public  void saveSudokuAllInOne(String path, Sudoku sudoku){
        File sudokuLocation = FileManager.getSudokuDir();
        FileManager.initialize(new File(path));
        new SudokuXmlHandler().saveAsXml(sudoku);
    }
}