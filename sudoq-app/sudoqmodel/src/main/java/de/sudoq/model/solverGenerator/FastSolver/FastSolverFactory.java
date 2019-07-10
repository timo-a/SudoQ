package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class FastSolverFactory {

    public static FastSolver getSolver(Sudoku s){
        switch (s.getSudokuType().getEnumType()){
            case standard16x16:
                return new DLXSolver(s);
            default:
                return new BranchAndBoundSolver(s);
        }
    }
}
