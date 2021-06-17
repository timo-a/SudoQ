package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver;
import de.sudoq.model.sudoku.Sudoku;

public class FastSolverFactory {

    public static FastSolver getSolver(Sudoku s) {
        switch (s.getSudokuType().getEnumType()) {
            case standard16x16:
                //return new BranchAndBoundSolver(s);
                return new DLXSolver(s);
            //return new Standard16Solver(s);
            case Xsudoku:
            case standard9x9:
                return new DLXSolver(s);

            //there are problems with dlx1, therefore the we use a different implementation for sudokus where we've observed problems
            case samurai:
                //return new SamuraiSolver(s);
                //return new BranchAndBoundSolver(s);
                return new DLXSolver(s);

            default:
                return new BranchAndBoundSolver(s);
        }
    }
}
