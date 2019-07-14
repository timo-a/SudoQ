package de.sudoq.model.solverGenerator.FastSolver;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.files.FileManagerTests;
import de.sudoq.model.solverGenerator.GenerationAlgo;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.XmlHelper;



public class SamuraiTest {

    @BeforeClass
    public static void init() {
        FileManagerTests.init();
    }

    @Test
	public void testSolveSamurai() {
        Sudoku s = getSudoku(FileManager.getSudokuDir(), SudokuTypes.samurai, Complexity.easy, 1);
        System.out.println(s);
        FastSolver fs = FastSolverFactory.getSolver(s);
        if (fs.hasSolution()) {
            System.out.println("Fast finds solution");
            PositionMap<Integer> solution = fs.getSolutions();
            SudokuBuilder sub = new SudokuBuilder(s.getSudokuType());
			for(Position p : GenerationAlgo.getPositions(s)) {
			    Integer v = solution.get(p);
                if(v==null) {
                    System.out.println(p+"macht null");
                }else if (v < 0){
                    System.out.println(p+"macht negativ");
                }
                else if (v >= 9){}
                //System.out.println("hier");
                else{
                    sub.addSolution(p, solution.get(p));//fill in all solutions
                }
			}
			Sudoku sudoku = sub.createSudoku();
            for (Position p: GenerationAlgo.getPositions(sudoku)) {
                Field f = sudoku.getField(p);
                f.setCurrentValue(f.getSolution());
            }
			System.out.println(sudoku );
        }
        System.out.println(fs.hasSolution());

        fs = new BranchAndBoundSolver(s);
        System.out.println(fs.hasSolution());


    }



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
