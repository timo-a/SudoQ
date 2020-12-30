package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public interface FastSolver {

    public boolean hasSolution();

    /**
     * assumes prior call to hasSolution, undefined behaviour if no solution
     * @return a PositionMap of the solutions for each Position
     */
    public PositionMap<Integer> getSolutions();

    public boolean isAmbiguous();
    /**
     * assumes prior call of `isAmbiguous`, undef behaviour if not ambiguous
     * @return a Position that is ambiguous.
     */
    public Position getAmbiguousPos();
}
