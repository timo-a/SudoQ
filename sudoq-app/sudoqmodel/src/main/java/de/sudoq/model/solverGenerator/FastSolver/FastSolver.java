package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public interface FastSolver {

    public boolean hasSolution();

    /**
     * assumes prior call to hasSolution, undefined bahaviour if no solution
     */
    public PositionMap<Integer> getSolutions();

    public boolean isAmbiguous();
    /**
     * assumes prior call of `isAmbiguous`, undef behaviour if not ambiguous
     */
    public Position getAmbiguousPos();
}
