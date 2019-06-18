package de.sudoq.model.solverGenerator;

import java.util.List;

import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

public class AmbiguityChecker {


    public static boolean isAmbiguous(Sudoku sudoku){
        Solver solver = new Solver(sudoku);


        solver.solveAll(false, false,false);

        //lastsolutions might be set to null, e.g. if we kill branch but dont find another solution
		//if we want to call getSolutions later on we'll be interested in the first solution anyway
		List<Solution> ls = solver.getSolutions();

		while (solver.getSolverSudoku().hasBranch()) {
			solver.getSolverSudoku().killCurrentBranch();//NB killing a branch is like a clockwork, we can't go back the same branches, bec we eliminate the candidate we chose last time
            //solver stores all the candidates per cell so after deleting the one chosen at backtracking, we can explore alternate paths
            //therefore validate must be true the second time! so the candidates don't get deleted and the same candidate isn't tried again.
            //alternate approach could be, get critical position, iterate over candidates, return true if any one leads to a solution
			if (solver.solveAll(false, false, true))//why is it invalid if solved and another solve?
				return true;
		}
		return false;
    }

}
