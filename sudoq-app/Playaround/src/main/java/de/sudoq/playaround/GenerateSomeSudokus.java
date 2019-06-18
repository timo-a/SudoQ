package de.sudoq.playaround;

import java.io.File;
import java.util.List;
import java.util.Random;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.solverGenerator.GenerationAlgo;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;


public class GenerateSomeSudokus {

    private static Sudoku sudoku;
    private static List<Solution> solutions;

    String SUDOKU_LOCATION  = "/home/t/Code/SudoQ/DebugOnPC/sudokufiles";
    String PROFILE_LOCATION = "/home/t/Code/SudoQ/DebugOnPC/profilefiles";

    public void setup() {
        FileManager.initialize(new File(PROFILE_LOCATION), new File(SUDOKU_LOCATION));
        //Profile.getInstance();
        //SudokuType.getSudokuType(SudokuTypes.standard9x9);
    }

    public void generate1() {
        setup();

        GeneratorCallback gc = getCallbackObj();

        System.out.println("567"+gc);

        Generator g = new Generator();
        g.generate(SudokuTypes.standard9x9, Complexity.infernal, gc);
        Thread t = g.getLastThread();
        try {
            t.join(); //waits for thread to end before we continue. Otherwise we might get NullPointerExceptions because objects not created yet.
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
                System.out.println("Callbackobj receives null for solutions: " + (slist == null));
            }

            @Override
            public String toString() {
                return "experiment";
            }

        };
    }

    public void generate2(){
        setup();

        Complexity c  = Complexity.infernal;
        SudokuTypes st = SudokuTypes.standard9x9;
        Sudoku sudoku = new SudokuBuilder(st).createSudoku();
		sudoku.setComplexity(c);

		Random random = new Random(0);

		GenerationAlgo ga = new GenerationAlgo(sudoku, getCallbackObj(), random);
		ga.run();
    }

    public Sudoku getSudoku() {
        return GenerateSomeSudokus.sudoku;
    }
    public List<Solution> getSolutions(){
        return solutions;
    }
    public void printDebugMsg() {
        (new Generator()).printDebugMsg();
    }
}