package de.sudoq.model.solverGenerator.solution;

import de.sudoq.model.solvingAssistant.HintTypes;

/**
 * Created by timo on 04.10.16.
 */
public class BacktrackingDerivation extends SolveDerivation {
    public BacktrackingDerivation() {
        technique = HintTypes.Backtracking;
    }
}
