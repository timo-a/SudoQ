package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.solverGenerator.AmbiguityChecker;
import de.sudoq.model.solverGenerator.FastSolver.BranchAndBound.FastBranchAndBound;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class BranchAndBoundSolver implements FastSolver {

    private Sudoku s;


    public BranchAndBoundSolver(Sudoku s){
        this.s = s;
    }

    @Override
    public boolean hasSolution() {
        //return new FastBranchAndBound(s).solveAll(false, false);
        return new FastBranchAndBound(s).solveAll2();
    }

    @Override
    public PositionMap<Integer> getSolutions() {
        //Solver ss = new Solver(s);
        //boolean success = ss.solveAll(false, true);
        //return ss.getSolutionsMap();
        FastBranchAndBound fb = new FastBranchAndBound(s);
        boolean success = fb.solveAll2();
        return fb.getSolutionsMap();
    }

    @Override
    public boolean isAmbiguous() {
        //return AmbiguityChecker.isAmbiguous(s);
        return FastAmbiguityChecker.isAmbiguous2(s);
    }

    @Override
    public Position getAmbiguousPos() {
        //return AmbiguityChecker.getFirstBranchPosition();
        return FastAmbiguityChecker.getFirstBranchPosition();
    }

}
