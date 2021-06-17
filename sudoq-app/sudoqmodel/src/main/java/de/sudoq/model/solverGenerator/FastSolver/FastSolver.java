package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

public interface FastSolver {

    boolean hasSolution();

    /**
     * assumes prior call to hasSolution, undefined behaviour if no solution
     *
     * @return a PositionMap of the solutions for each Position
     */
    PositionMap<Integer> getSolutions();

    boolean isAmbiguous();

    /**
     * assumes prior call of `isAmbiguous`, undef behaviour if not ambiguous
     *
     * @return a Position that is ambiguous.
     */
    Position getAmbiguousPos();
}
