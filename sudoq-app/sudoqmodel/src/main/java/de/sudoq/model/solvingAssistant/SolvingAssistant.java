package de.sudoq.model.solvingAssistant;

import java.util.LinkedList;
import java.util.Queue;

import de.sudoq.model.solverGenerator.solution.BacktrackingDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solverGenerator.solver.helper.LastDigitHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper;
import de.sudoq.model.sudoku.Sudoku;

/**
 * Created by timo on 25.09.16.
 */
public class SolvingAssistant {
    public static SolveDerivation giveAHint(Sudoku sudoku){
        SolverSudoku s = new SolverSudoku(sudoku);
        Queue<SolveHelper> helpers = new LinkedList<>();
        helpers.add(new LastDigitHelper(s,  0));
        helpers.add(new NakedHelper    (s,1,0));

        for (SolveHelper sh : helpers)
            if(sh.update(true))
                return sh.getDerivation();

        return new BacktrackingDerivation();

    }
}
