package de.sudoq.model.solverGenerator.FastSolver;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.sudoq.model.solverGenerator.GenerationAlgo;
import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo2;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilderNew;
import de.sudoq.model.sudoku.SudokuUnderConstruction;
import de.sudoq.model.sudoku.complexity.Complexity;


class XSudokuTest {

    private static SudokuTypeRepo4Tests sudokuTypeRepo = new SudokuTypeRepo4Tests();

    private PrettySudokuRepo2 sudokuRepo = new PrettySudokuRepo2(sudokuTypeRepo);

    //todo this test doesn't have any assertions...
    @Test
    void solveXSudoku() {
        Path sudokuPath = Paths.get("sudokus/x_easy_1.pretty");
        Sudoku s_ = sudokuRepo.read(sudokuPath, Complexity.easy);
        SudokuUnderConstruction s = new SudokuUnderConstruction(-1, s_.getSudokuType(), s_.getComplexity(), s_.getCells());
        System.out.println(s);
        FastSolver fs = FastSolverFactory.getSolver(s_);

        if (fs.hasSolution()) {
            System.out.println("Fast finds solution");
            //System.out.println("Fast finds solution");
            PositionMap<Integer> solution = fs.getSolutions();
            SudokuBuilderNew sub = new SudokuBuilderNew(s.getSudokuType());
			for(Position p : GenerationAlgo.getPositions(s)) {
			    if (solution.get(p) < 0)
			        System.out.println("hier");
                sub.addSolution(p, solution.get(p));//fill in all solutions
            }
			SudokuUnderConstruction sudoku = sub.build();
            for (Position p: GenerationAlgo.getPositions(sudoku)) {
                Cell f = sudoku.getCell(p);
                f.setCurrentValue(f.getSolution());
            }
			System.out.println(sudoku );
        }


        //fs = new BranchAndBoundSolver(s);
        //System.out.println(fs.hasSolution());
    }


}
