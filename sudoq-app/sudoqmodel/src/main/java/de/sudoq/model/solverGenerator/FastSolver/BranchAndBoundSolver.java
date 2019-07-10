package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.solverGenerator.AmbiguityChecker;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class BranchAndBoundSolver implements FastSolver {

    private Sudoku s;

    public BranchAndBoundSolver(Sudoku s){
        this.s = s;
    }

    @Override
    public boolean hasSolution() {
        return new Solver(s).solveAll(false, false);
    }

    @Override
    public boolean isAmbiguous() {
        return AmbiguityChecker.isAmbiguous(s);
    }

    @Override
    public PositionMap<Integer> getSolutions() {
        Solver ss = new Solver(s);
        ss.solveAll(false, false);
        return ss.getSolutionsMap();
    }
}
