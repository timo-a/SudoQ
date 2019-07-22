package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.solverGenerator.FastSolver.BranchAndBound.FastBranchAndBound;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

public class FastAmbiguityChecker {

    private static Position FirstBranchPosition;


    /**
     * Determines whether there are multiple solutions to the passed sudoku.
     * @param sudoku Sudoku object to be tested for multiple solutions
     * @return true if there are several solutions false otherwise
     */
    public static boolean isAmbiguous(Sudoku sudoku){
        FastBranchAndBound solver = new FastBranchAndBound(sudoku);

        boolean result = solver.solveAll();

        FirstBranchPosition = solver.getSolverSudoku().hasBranch() ? solver.getSolverSudoku().getFirstBranchPosition()
                : null; //lest old values persist

        return result && solver.severalSolutionsExist();
    }

    public static boolean isAmbiguous2(Sudoku sudoku){
        FastBranchAndBound solver = new FastBranchAndBound(sudoku);

        //looking for first solution

        boolean result = solver.solveAll2();


        FirstBranchPosition = solver.getSolverSudoku().hasBranch() ? solver.getSolverSudoku().getFirstBranchPosition()
                : null; //lest old values persist

        return result && solver.severalSolutionsExist();
    }














    /**
     * If there are several solutions, this method holds the position of the first branch point.
     * Call only after an `isAmbiguous` call that returned `true`. Otherwise it is null.
     * @return position of the first branchingPoint, i.e. where backtracking was first applied or `null` if last call to `isAmbiguous` was unsuccessful.
     */
    public static Position getFirstBranchPosition() {
        return FirstBranchPosition;
    }
}
