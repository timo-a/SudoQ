package de.sudoq.model.solverGenerator.FastSolver;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import de.sudoq.model.TestWithInitCleanforSingletons;
import de.sudoq.model.Utility;
import de.sudoq.model.solverGenerator.GenerationAlgo;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;


public class SudokuTest {
    private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

    @BeforeClass
    public static void init() {
        TestWithInitCleanforSingletons.legacyInit();
    }

    @Test
	public void testSolveSudoku() {

        Sudoku s = getSudoku(sudokuDir, SudokuTypes.standard9x9, Complexity.easy, 1);
        System.out.println(s);
        FastSolver fs = FastSolverFactory.getSolver(s);

        if (fs.hasSolution()) {
            System.out.println("Fast finds solution");
            //System.out.println("Fast finds solution");
            PositionMap<Integer> solution = fs.getSolutions();
            SudokuBuilder sub = new SudokuBuilder(s.getSudokuType());
			for(Position p : GenerationAlgo.getPositions(s)) {
			    if (solution.get(p) < 0)
			        System.out.println("hier");
                sub.addSolution(p, solution.get(p));//fill in all solutions
            }
			Sudoku sudoku = sub.createSudoku();
            for (Position p: GenerationAlgo.getPositions(sudoku)) {
                Cell f = sudoku.getCell(p);
                f.setCurrentValue(f.getSolution());
            }
			System.out.println(sudoku );
        }


        //fs = new BranchAndBoundSolver(s);
        //System.out.println(fs.hasSolution());
    }





    public static Sudoku getSudoku(File dir, SudokuTypes st, Complexity c, int i){
        File f = new File(dir, st.toString()
                + File.separator
                + c.toString()
                + File.separator
                + "sudoku_" + i + ".xml");

//        Sudoku s = new Sudoku(SudokuTypeProvider.getSudokuType(st, sudokuDir));
//        try {
//            s.fillFromXml(new XmlHelper().loadXml(f), sudokuDir);
//            s.setComplexity(Complexity.arbitrary);//justincase
//            return s;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } todo load from resources
        return null;
    }
}
